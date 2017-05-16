package com.chuangju.pathnote.lib.shape;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;


import com.chuangju.pathnote.lib.DrawingType;

import org.xml.sax.Attributes;

public class Rubber extends HandDraw {

    public Rubber(Context context) {
        super(context, DrawingType.Rubber);
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public Rubber(Context context, Attributes attributes) throws Exception {
        super(context, attributes);
    }

    public Rubber(Rubber rubber) {
        super(rubber);
    }
}
