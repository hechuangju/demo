package com.chuangju.pathnote.lib.view;

import android.content.Context;
import android.util.AttributeSet;

import com.chuangju.pathnote.lib.shape.BaseDraw;


public class MicroClassPlayView extends BaseMicroClassView {

    public MicroClassPlayView(Context context) {
        this(context, null);
    }

    public MicroClassPlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicroClassPlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void seekTo(int position) {
        updatePosition(position);
    }

    public void updatePosition(int position) {
        clearCanvas();
        for (int i = 0; i < list.size(); i++) {
            BaseDraw baseDraw = list.get(i);
            baseDraw.updatePosition(mCanvas, position);
        }
        invalidate();
    }

    public long getDuration() {
        if (list == null || list.isEmpty()) return -1;
        BaseDraw baseDraw = list.get(list.size() - 1);
        if (baseDraw.tList == null || baseDraw.tList.isEmpty()) return -1;
        return baseDraw.tList.get(baseDraw.tList.size() - 1);
    }
}
