package com.chuangju.pathnote.lib.shape;

import android.content.Context;
import android.graphics.RectF;


import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

/**
 * Created by hechuangju on 15/12/5.
 */
public class Curve extends BaseDraw {
    private RectF rectF = new RectF();
    private Type type;

    public enum Type {
        Sin(1),
        Cos(2),
        Tan(3),
        Cot(4),
        ArcSin(5),
        ArcCos(6),
        ArcTan(7),
        ArcCot(8),
        Exponent(16),
        ExponentLTone(17),
        Log10(18),
        Log10LTone(19),
        ParaCurve(32);
        public int code;

        Type(int code) {
            this.code = code;
        }

        public static Type from(int code) {
            for (Type type : Type.values()) {
                if (type.code == code)
                    return type;
            }
            return null;
        }
    }

    public Curve(Context context) {
        super(context, DrawingType.Curve);
    }

    public Curve(Context context, Attributes attributes) throws Exception {
        super(context, attributes);
        type = Type.from(Integer.parseInt(attributes.getValue("tp")));
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

    public Curve(Curve curve) {
        super(curve);
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
    public void fingerDown(float x, float y) {
        super.fingerDown(x, y);
        mPath.moveTo(x, y);
    }

    @Override
    public void fingerMove(float x, float y) {
        super.fingerMove(x, y);
        float oldX = xList.get(xList.size() - 1);
        float oldY = yList.get(yList.size() - 1);
        mPath.quadTo(oldX, oldY, (x + oldX) / 2, (y + oldY) / 2);
    }

    @Override
    public void fingerUp(float x, float y) {
        super.fingerUp(x, y);
        mPath.lineTo(x, y);
    }

    @Override
    protected void playDown(float x, float y) {
    }

    @Override
    protected void playMove(float x, float y) {
    }

    @Override
    protected void playUp(float x, float y) {
        mPath.reset();
        float step = rectF.width() / 80f;
        float oldX = rectF.left;
        float oldY = rectF.centerY();
        float newX = oldX;
        while (newX < rectF.right) {
            float newY = 0;
            switch (type) {
                case Sin:
                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.sin(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
                    break;
                case Cos:
                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.cos(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
                    break;
                case Tan:
                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.tan(((newX - rectF.left) * Math.PI / rectF.width()) - Math.PI / 2);
                    break;
                case Cot:
                    newY = rectF.centerY() - rectF.height() / 2 * (1f / (float) Math.tan(((newX - rectF.left) * Math.PI / rectF.width())));
                    break;
//                case ArcSin:
//                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.asin(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
//                    break;
//                case ArcCos:
//                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.acos(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
//                    break;
//                case ArcTan:
//                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.atan(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
//                    break;
//                case ArcCot:
//                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.atan(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
//                    break;
                case Exponent:
//                    double db1 = rectF.width() / 50;
//                    newY = (float) (rectF.bottom - db1 * Math.exp((newX - rectF.left - step) / 50.0));
                    newY = rectF.bottom - rectF.height() * (float) Math.exp(((newX * 1.03f - rectF.right) * 7 / rectF.width()));
                    break;
                case ExponentLTone:
                    newY = rectF.bottom - rectF.height() * (float) Math.exp(((rectF.left + rectF.right - newX * 1.03f - rectF.right) * 7 / rectF.width()));
                    break;
                case Log10:
                    newY = rectF.centerY() - rectF.height() / 2 * (float) Math.log10(((newX - rectF.left) * 2 * Math.PI / rectF.width()));
                    break;
                case Log10LTone:
                    newY = rectF.bottom - rectF.height() * (float) Math.exp(((rectF.left + rectF.right - newX * 1.03f - rectF.right) * 7 / rectF.width()));
                    break;
                case ParaCurve:
                    newY = (float) (rectF.bottom - 4 * rectF.height() * Math.pow(newX - rectF.centerX(), 2) / Math.pow(rectF.width(), 2));
                    break;
            }
            if (rectF.contains(newX, newY)) {
                if (mPath.isEmpty())
                    mPath.moveTo(newX, newY);
                else {
                    mPath.quadTo(oldX, oldY, newX, newY);
                }
            }
            oldX = newX;
            oldY = newY;
            newX = oldX + step;
        }
    }
}