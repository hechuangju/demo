package com.chuangju.pathnote.lib.shape;

import android.content.Context;
import android.graphics.DashPathEffect;


import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;

/**
 * Created by hechuangju on 15/12/5.
 */
public class LineDash extends Line {
    public LineDash(Context context) {
        super(context, DrawingType.LineDash);
        paint.setPathEffect(new DashPathEffect(new float[]{6, 6}, 1));
    }

    public LineDash(LineDash lineDash) {
        super(lineDash);
    }

    public LineDash(Context context, Attributes attributes) throws Exception {
        super(context, attributes);
        paint.setPathEffect(new DashPathEffect(new float[]{6, 6}, 1));
    }

}