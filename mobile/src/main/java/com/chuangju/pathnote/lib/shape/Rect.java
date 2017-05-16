package com.chuangju.pathnote.lib.shape;

import android.content.Context;
import android.graphics.Path.Direction;
import android.graphics.RectF;

import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * A rectangle.
 */
public class Rect extends BaseDraw {
    public Rect(Context context) {
        super(context, DrawingType.Rect);
    }

    public Rect(Context context, DrawingType type) {
        super(context, type);
    }

    public Rect(Context context, Attributes attributes) throws Exception {
        super(context, attributes);
        String[] xs = new String[]{attributes.getValue("x1"), attributes.getValue("x2")};
        String[] ys = new String[]{attributes.getValue("y1"), attributes.getValue("y2")};
        for (int i = 0; i < xs.length; i++) {
            xList.add(Float.valueOf(xs[i]));
            yList.add(Float.valueOf(ys[i]));
            tList.add((long) 0);
        }
    }

    public Rect(Rect rect) {
        super(rect);
    }

    @Override
    protected void setBuildFromPoints(XmlSerializer xmlSerializer) throws IOException {
        if (xList.size() >= 2 && yList.size() >= 2) {
            xmlSerializer.attribute("", "x1", String.valueOf(xList.get(0)));
            xmlSerializer.attribute("", "x2", String.valueOf(xList.get(xList.size() - 1)));
            xmlSerializer.attribute("", "y1", String.valueOf(yList.get(0)));
            xmlSerializer.attribute("", "y2", String.valueOf(yList.get(yList.size() - 1)));
        }
    }

    @Override
    public void fingerMove(float x, float y) {
        super.fingerMove(x, y);
        mPath.reset();
        float startX = xList.get(0);
        float startY = yList.get(0);
        RectF rectF = new RectF();
        rectF.left = Math.min(startX, x);
        rectF.right = Math.max(startX, x);
        rectF.top = Math.min(startY, y);
        rectF.bottom = Math.max(startY, y);
        mPath.addRect(rectF, Direction.CW);
    }

    @Override
    protected void playDown(float x, float y) {
        playMove(x, y);
    }

    @Override
    protected void playMove(float x, float y) {
        mPath.reset();
        float startX = xList.get(0);
        float startY = yList.get(0);
        RectF rectF = new RectF();
        rectF.left = Math.min(startX, x);
        rectF.right = Math.max(startX, x);
        rectF.top = Math.min(startY, y);
        rectF.bottom = Math.max(startY, y);
        mPath.addRect(rectF, Direction.CW);
    }

    @Override
    protected void playUp(float x, float y) {
        playMove(x, y);
    }
}
