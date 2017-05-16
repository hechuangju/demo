package com.chuangju.pathnote.lib.shape;


import android.content.Context;


import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Track the finger's movement on the screen.
 */
public class HandDraw extends BaseDraw {

    public HandDraw(Context context) {
        super(context, DrawingType.HandDraw);
    }

    public HandDraw(Context context, DrawingType type) {
        super(context, type);
    }

    public HandDraw(HandDraw handDraw) {
        super(handDraw);
    }

    public HandDraw(Context context, Attributes attributes) throws Exception {
        super(context, attributes);
        String[] xs = attributes.getValue("xs").split(",");
        String[] ys = attributes.getValue("ys").split(",");
        String[] ts = attributes.getValue("ts").split(",");
        for (int i = 0; i < xs.length; i++) {
            xList.add(Float.valueOf(xs[i]));
            yList.add(Float.valueOf(ys[i]));
            tList.add(Long.valueOf(ts[i]));
        }
    }

    @Override
    protected void setBuildFromPoints(XmlSerializer xmlSerializer) throws IOException {
        xmlSerializer.attribute("", "xs", buildXString());
        xmlSerializer.attribute("", "ys", buildYString());
        xmlSerializer.attribute("", "ts", buildTimeString());
    }

    @Override
    public void fingerDown(float x, float y) {
        super.fingerDown(x, y);
        mPath.moveTo(x, y);
    }

    @Override
    public void fingerMove(float x, float y) {
        super.fingerMove(x, y);
        float oldX = xList.size() > 2 ? xList.get(xList.size() - 2) : x;
        float oldY = yList.size() > 2 ? yList.get(yList.size() - 2) : y;
        mPath.quadTo(oldX, oldY, (x + oldX) / 2, (y + oldY) / 2);
    }

    @Override
    public void fingerUp(float x, float y) {
        super.fingerUp(x, y);
        mPath.lineTo(x, y);
    }

    @Override
    protected void playDown(float x, float y) {
        mPath.moveTo(x, y);
    }

    @Override
    protected void playMove(float x, float y) {
        float oldX = xList.get(drawOffset - 1);
        float oldY = yList.get(drawOffset - 1);
        mPath.quadTo(oldX, oldY, (x + oldX) / 2, (y + oldY) / 2);
    }

    @Override
    protected void playUp(float x, float y) {
        mPath.lineTo(x, y);
    }
}
