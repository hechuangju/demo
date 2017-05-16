package com.chuangju.pathnote.lib.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;


import com.chuangju.pathnote.lib.BaseXmlObject;
import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDraw extends BaseXmlObject {
    public final DrawingType type;
    protected final Context context;
    public long id;
    protected Path mPath = new Path();
    public Paint paint = new Paint();
    public List<Float> xList = new ArrayList<>();
    public List<Float> yList = new ArrayList<>();
    public List<Long> tList = new ArrayList<>();
    public long startTime = 0;
    public float rotate = 0;
    public float px;
    public float py;
    protected String border, ver;
    private String creator;
    protected BaseDraw(Context context, DrawingType type) {
        this.context = context;
        this.type = type;
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(Color.BLACK);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeWidth(3);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    protected BaseDraw(Context context, Attributes attributes) throws Exception {
        this(context, DrawingType.from(parseString2Int(attributes.getValue("type"))));
        this.id = Long.parseLong(attributes.getValue("id"));
        this.border = attributes.getValue("border");
        this.ver = attributes.getValue("ver");
        this.paint.setStrokeWidth(parseString2Int(attributes.getValue("size")));
        String color = attributes.getValue("color");
        creator = attributes.getValue("creator");
        try {
            if (!TextUtils.isEmpty(color)) {
                if (color.startsWith("#")) {
                    this.paint.setColor(Color.parseColor(color));
                } else {
                    int in = Integer.valueOf(color);
                    int blue = (in >> 16) & 0xFF;
                    int green = (in >> 8) & 0xFF;
                    int red = (in >> 0) & 0xFF;
                    int out = (red << 16) | (green << 8) | (blue << 0);
                    this.paint.setColor(Color.parseColor(String.format("#%06X", 0xFFFFFF & out)));
                }
            }

        } catch (Exception e) {
            this.paint.setColor(Color.BLACK);
        }
    }

    protected BaseDraw(BaseDraw baseDraw) {
        this.context = baseDraw.context;
        this.id = baseDraw.id;
        this.type = baseDraw.type;
        this.mPath = new Path(baseDraw.mPath);
        this.paint = new Paint(baseDraw.paint);
        this.xList = new ArrayList<>(baseDraw.xList);
        this.yList = new ArrayList<>(baseDraw.yList);
        this.tList = new ArrayList<>(baseDraw.tList);
    }

    @Override
    public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "object");
        xmlSerializer.attribute("", "id", String.valueOf(id));
        xmlSerializer.attribute("", "type", String.valueOf(type.getType()));
        xmlSerializer.attribute("", "size", String.valueOf((int) paint.getStrokeWidth()));
        xmlSerializer.attribute("", "color", String.format("#%06X", (0xFFFFFF & paint.getColor())));
        if (!TextUtils.isEmpty(creator))
            xmlSerializer.attribute("", "creator", creator);
        if (!TextUtils.isEmpty(ver))
            xmlSerializer.attribute("", "ver", ver);
        else
            xmlSerializer.attribute("", "v", "1");
        if (!TextUtils.isEmpty(border))
            xmlSerializer.attribute("", "border", border);
        setBuildFromPoints(xmlSerializer);
        xmlSerializer.endTag("", "object");
    }

    protected abstract void setBuildFromPoints(XmlSerializer xmlSerializer) throws IOException;


    public Paint getPaint() {
        return paint;
    }

    private long beginFingerTime = 0;

    public void fingerDown(float x, float y) {
        beginFingerTime = System.currentTimeMillis();
        xList.clear();
        yList.clear();
        tList.clear();
        xList.add(x);
        yList.add(y);
        tList.add(startTime);
        mPath.reset();
    }

    public void fingerMove(float x, float y) {
        xList.add(x);
        yList.add(y);
        tList.add(System.currentTimeMillis() - beginFingerTime + startTime);
    }

    public void fingerUp(float x, float y) {
        fingerMove(x, y);
    }

    public void drawAllOnCanvas(Canvas canvas) {
        updatePosition(canvas, Long.MAX_VALUE);
    }

    protected int drawOffset = 0;
    protected long previousPosition = 0;

    public void updatePosition(Canvas canvas, long currentPosition) {
        if (canvas == null) return;
        if (currentPosition <= previousPosition) {
            previousPosition = 0;
            drawOffset = 0;
            mPath.reset();
        }
        for (; drawOffset < xList.size(); drawOffset++) {
            long time = tList.get(drawOffset);
            if (time > currentPosition) break;
            float x = xList.get(drawOffset);
            float y = yList.get(drawOffset);
            if (drawOffset == 0) {
                mPath.reset();
                playDown(x, y);
            } else if (drawOffset == xList.size() - 1) {
                playUp(x, y);
            } else {
                playMove(x, y);
            }
        }
        this.previousPosition = currentPosition;
        canvas.drawPath(getTransformPath(canvas.getWidth(), canvas.getHeight()), paint);
    }

    protected abstract void playDown(float x, float y);

    protected abstract void playMove(float x, float y);

    protected abstract void playUp(float x, float y);

    public void formatPath(float xCoefficient, float yCoefficient) {
        for (int i = 0; i < xList.size(); i++) {
            float formatx = xList.get(i) * xCoefficient;
            float formaty = yList.get(i) * yCoefficient;
            xList.set(i, formatx);
            yList.set(i, formaty);
        }
    }

    public String buildXString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < xList.size(); i++) {
            buffer.append(xList.get(i));
            if (i != xList.size() - 1)
                buffer.append(",");
        }
        return buffer.toString();
    }

    public String buildYString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < yList.size(); i++) {
            buffer.append(yList.get(i));
            if (i != yList.size() - 1)
                buffer.append(",");
        }
        return buffer.toString();
    }

    public String buildTimeString() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < tList.size(); i++) {
            buffer.append(tList.get(i));
            if (i != tList.size() - 1)
                buffer.append(",");
        }
        return buffer.toString();
    }

    public Path getTransformPath(float width, float height) {
        Path formatPath = new Path(mPath);
        Matrix matrix = new Matrix();
        matrix.setScale(width, height);
        if (rotate != 0) {
            matrix.postRotate(rotate, px * width, py * height);
        }
        formatPath.transform(matrix);
        return formatPath;
    }

    @Override
    public String toString() {
        return "BaseDraw{" +
                "type=" + type +
                ", id=" + id +
                '}';
    }
}
