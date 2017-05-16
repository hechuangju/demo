package com.chuangju.pathnote.lib.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;


import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by hechuangju on 15/12/11.
 */
public class MathDraw extends BaseDraw {
    private float rotate = 0;
    private LongSparseArray<BaseDraw> childList = new LongSparseArray<>();
    private RectF rectF = new RectF();
    private int mathType = 0;

    public void addChild(BaseDraw baseDraw) {
        baseDraw.rotate = rotate;
        baseDraw.px = rectF.centerX();
        baseDraw.py = rectF.centerY();
        childList.put(baseDraw.id, baseDraw);
    }

    public MathDraw(Context context) {
        super(context, DrawingType.Math);
    }

    public MathDraw(Context context, Attributes attributes) throws Exception {
        super(context, attributes);
        rotate = Float.valueOf(attributes.getValue("rotate"));
        mathType = Integer.valueOf(attributes.getValue("mathtp"));
        String[] xs = new String[]{attributes.getValue("x1"), attributes.getValue("x2")};
        String[] ys = new String[]{attributes.getValue("y1"), attributes.getValue("y2")};
        for (int i = 0; i < xs.length; i++) {
            xList.add(Float.valueOf(xs[i]));
            yList.add(Float.valueOf(ys[i]));
            tList.add((long) 0);
        }
        float startX = xList.get(0);
        float startY = yList.get(0);
        float x0 = xList.get(xList.size() - 1);
        float y0 = yList.get(yList.size() - 1);
        rectF.left = Math.min(startX, x0);
        rectF.right = Math.max(startX, x0);
        rectF.top = Math.min(startY, y0);
        rectF.bottom = Math.max(startY, y0);
    }

    public MathDraw(MathDraw math) {
        super(math);
    }

    @Override
    public void setBuilderFromBody(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.startTag("", "object");
        xmlSerializer.attribute("", "id", String.valueOf(id));
        xmlSerializer.attribute("", "rotate", String.valueOf((int) rotate));
        if (!TextUtils.isEmpty(ver))
            xmlSerializer.attribute("", "ver", ver);
        if (!TextUtils.isEmpty(border))
            xmlSerializer.attribute("", "border", border);
        xmlSerializer.attribute("", "mathtp", String.valueOf(mathType));
        xmlSerializer.attribute("", "type", String.valueOf(type.getType()));
        xmlSerializer.attribute("", "size", String.valueOf((int) paint.getStrokeWidth()));
        xmlSerializer.attribute("", "color", String.format("#%06X", (0xFFFFFF & paint.getColor())));
        if (xList.size() >= 2 && yList.size() >= 2) {
            xmlSerializer.attribute("", "x1", String.valueOf(xList.get(0)));
            xmlSerializer.attribute("", "x2", String.valueOf(xList.get(xList.size() - 1)));
            xmlSerializer.attribute("", "y1", String.valueOf(yList.get(0)));
            xmlSerializer.attribute("", "y2", String.valueOf(yList.get(yList.size() - 1)));
        }
        for (int i = 0; i < childList.size(); i++) {
            BaseDraw baseDraw = childList.valueAt(i);
            baseDraw.setBuilderFromBody(xmlSerializer);
        }
        xmlSerializer.endTag("", "object");
    }

    @Override
    public void drawAllOnCanvas(Canvas canvas) {
        if (canvas == null) return;
        for (int i = 0; i < childList.size(); i++) {
            BaseDraw baseDraw = childList.valueAt(i);
            baseDraw.drawAllOnCanvas(canvas);
        }
    }

    @Override
    protected void setBuildFromPoints(XmlSerializer xmlSerializer) throws IOException {

    }

    @Override
    public void updatePosition(Canvas canvas, long currentPosition) {

    }

    @Override
    protected void playDown(float x, float y) {

    }

    @Override
    protected void playMove(float x, float y) {

    }

    @Override
    protected void playUp(float x, float y) {
    }
}
