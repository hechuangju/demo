package com.chuangju.pathnote.lib;

import android.content.Context;


import com.chuangju.pathnote.lib.shape.Arc;
import com.chuangju.pathnote.lib.shape.BaseDraw;
import com.chuangju.pathnote.lib.shape.Circle;
import com.chuangju.pathnote.lib.shape.Curve;
import com.chuangju.pathnote.lib.shape.Ellipse;
import com.chuangju.pathnote.lib.shape.HandDraw;
import com.chuangju.pathnote.lib.shape.Line;
import com.chuangju.pathnote.lib.shape.LineDash;
import com.chuangju.pathnote.lib.shape.Mask;
import com.chuangju.pathnote.lib.shape.MathDraw;
import com.chuangju.pathnote.lib.shape.Rect;
import com.chuangju.pathnote.lib.shape.Rubber;

import org.w3c.dom.Text;
import org.xml.sax.Attributes;

public class DrawingFactory {

    public static BaseDraw createDrawing(Context context, DrawingType type) {
        BaseDraw drawing = null;
        switch (type) {
            case HandDraw:
                drawing = new HandDraw(context);
                break;
            case Circle:
                drawing = new Circle(context);
                break;
            case Curve:
                drawing = new Curve(context);
                break;
            case Rubber:
                drawing = new Rubber(context);
                break;
            case Mask:
                drawing = new Mask(context);
                break;
            case Ellipse:
                drawing = new Ellipse(context);
                break;
            case Picture:
                drawing = new Rect(context, DrawingType.Text);
                break;
            case Rect:
                drawing = new Rect(context);
                break;
            case LineDash:
                drawing = new LineDash(context);
                break;
            case Line:
                drawing = new Line(context);
                break;
            case Text:
//                drawing = new Text(context);
                break;
            case Math:
                drawing = new MathDraw(context);
                break;
            case Arc:
                drawing = new Arc(context);
                break;
        }
        return drawing;
    }

    public static BaseDraw createDrawing(Context context, Attributes attributes) throws Exception {
        BaseDraw drawing = null;
        DrawingType type = DrawingType.from(Integer.parseInt(attributes.getValue("type")));
        switch (type) {
            case HandDraw:
                drawing = new HandDraw(context, attributes);
                break;
            case Circle:
                drawing = new Circle(context, attributes);
                break;
            case Curve:
                drawing = new Curve(context, attributes);
                break;
            case Rubber:
                drawing = new Rubber(context, attributes);
                break;
            case Mask:
                drawing = new Mask(context, attributes);
                break;
            case Ellipse:
                drawing = new Ellipse(context, attributes);
                break;
            case Picture:
                drawing = new Rect(context, attributes);
                break;
            case Rect:
                drawing = new Rect(context, attributes);
                break;
            case LineDash:
                drawing = new LineDash(context, attributes);
                break;
            case Line:
                drawing = new Line(context, attributes);
                break;
            case Text:
//                drawing = new Text(context, attributes);
                break;
            case Math:
                drawing = new MathDraw(context, attributes);
                break;
            case Arc:
                drawing = new Arc(context, attributes);
                break;
        }
        return drawing;
    }

}