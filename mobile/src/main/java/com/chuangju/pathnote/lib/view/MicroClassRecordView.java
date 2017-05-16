package com.chuangju.pathnote.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.chuangju.pathnote.R;
import com.chuangju.pathnote.lib.DensityUtil;
import com.chuangju.pathnote.lib.DrawingFactory;
import com.chuangju.pathnote.lib.DrawingType;
import com.chuangju.pathnote.lib.shape.BaseDraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MicroClassRecordView extends BaseMicroClassView {
    private ArrayList<BaseDraw> RecordList = new ArrayList<>();
    private SparseArray<BaseDraw> touchList = new SparseArray<>();
    public boolean isRecord = false;
    private long recordStartTime, offsetTime, pauseTime;
    private DrawListener mDrawListener;
    private Paint localPaint;
    private int actionButtonSize;
    private Bitmap deleteBitmap, queBitmap;
    private Map<View, EditBtnEntry> childEntries = new HashMap<>();

    public interface DrawListener {
        long resultCallBack(BaseDraw interactiveDrawing);
    }

    public MicroClassRecordView(Context context) {
        this(context, null);
    }

    public MicroClassRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicroClassRecordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        localPaint = new Paint();
        localPaint.setColor(Color.RED);
        localPaint.setAntiAlias(true);
        localPaint.setDither(true);
        localPaint.setStyle(Paint.Style.STROKE);
        localPaint.setStrokeWidth(2.0f);
        actionButtonSize = (int) DensityUtil.dip2px(context, 40);
        deleteBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.youshi_guanbi);
        queBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.youshi_queren);
    }

    public void reSet() {
        super.reSet();
        recordStartTime = 0;
        offsetTime = 0;
        RecordList.clear();
        removeAllViews();
    }

    public void setDrawListener(DrawListener mDrawListener) {
        this.mDrawListener = mDrawListener;
    }

    public ArrayList<BaseDraw> getRecordList() {
        return RecordList;
    }

    public boolean isRecord() {
        return isRecord;
    }

    public void pause() {
        pauseTime = System.currentTimeMillis();
    }

    public void restore() {
        offsetTime = offsetTime + System.currentTimeMillis() - pauseTime;
    }

    public void setRecord(boolean isRecord) {
        this.isRecord = isRecord;
    }

    public void initRecord(long beginTime) {
        recordStartTime = beginTime;
        isRecord = true;
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnTouchListener(null);
            getChildAt(i).setEnabled(false);
        }
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                int id = event.getPointerId(0);
                float x = event.getX(0);
                float y = event.getY(0);
                for (int i = 0; i < getChildCount(); i++) {
                    View view = getChildAt(i);
                    if (view.isEnabled()) {
                        EditBtnEntry editBtnEntry = childEntries.get(view);
                        if(editBtnEntry!=null) {
                            if (editBtnEntry.deleteRect.contains(x, y)) {
                                removeView(view);
                                invalidate();
                                return true;
                            } else if (editBtnEntry.queRect.contains(x, y)) {
                                view.setEnabled(false);
                                invalidate();
                                return true;
                            }
                        }
                    }
                }
                fingerDown(id, 0, event);
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                int pointerIndex = event.getActionIndex();
                int id = event.getPointerId(pointerIndex);
                fingerDown(id, pointerIndex, event);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                fingersMove(event);
                break;

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = event.getActionIndex();
                int id = event.getPointerId(pointerIndex);
                fingerUp(id, pointerIndex, event);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                int id = event.getPointerId(0);
                fingerUp(id, 0, event);
                touchList.clear();
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        for (int i = 0; i < touchList.size(); i++) {
            BaseDraw interactiveDrawing = touchList.valueAt(i);
            canvas.drawPath(interactiveDrawing.getTransformPath(getMeasuredWidth(), getMeasuredHeight()), interactiveDrawing.getPaint());
        }

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.isEnabled()) {
                EditBtnEntry editBtnEntry = childEntries.get(view);
                if (editBtnEntry == null) continue;
                float[] leftTop = {view.getLeft(), view.getTop()};
                float[] rightTop = {view.getRight(), view.getTop()};
                float[] leftBottom = {view.getLeft(), view.getBottom()};
                float[] rightBottom = {view.getRight(), view.getBottom()};
                view.getMatrix().mapPoints(leftTop);
                view.getMatrix().mapPoints(rightTop);
                view.getMatrix().mapPoints(leftBottom);
                view.getMatrix().mapPoints(rightBottom);
                editBtnEntry.deleteRect.left = rightTop[0] - actionButtonSize / 2;
                editBtnEntry.deleteRect.top = rightTop[1] - actionButtonSize / 2;
                editBtnEntry.deleteRect.right = rightTop[0] + actionButtonSize / 2;
                editBtnEntry.deleteRect.bottom = rightTop[1] + actionButtonSize / 2;
                editBtnEntry.queRect.left = leftTop[0] - actionButtonSize / 2;
                editBtnEntry.queRect.top = leftTop[1] - actionButtonSize / 2;
                editBtnEntry.queRect.right = leftTop[0] + actionButtonSize / 2;
                editBtnEntry.queRect.bottom = leftTop[1] + actionButtonSize / 2;
                canvas.drawLine(leftTop[0], leftTop[1], leftBottom[0], leftBottom[1], localPaint);
                canvas.drawLine(leftBottom[0], leftBottom[1], rightBottom[0], rightBottom[1], localPaint);
                canvas.drawLine(rightBottom[0], rightBottom[1], rightTop[0], rightTop[1], localPaint);
                canvas.drawLine(rightTop[0], rightTop[1], leftTop[0], leftTop[1], localPaint);
                canvas.drawBitmap(deleteBitmap, null, editBtnEntry.deleteRect, null);
                canvas.drawBitmap(queBitmap, null, editBtnEntry.queRect, null);
            }
        }
    }

    private class EditBtnEntry {
        RectF deleteRect = new RectF();
        RectF queRect = new RectF();
    }


    private void fingerDown(int id, int index, MotionEvent event) {
        float x = event.getX(index);
        float y = event.getY(index);
        fingerDown(id, x, y);
    }


    private void fingersMove(MotionEvent event) {
        for (int index = 0; index < event.getPointerCount(); index++) {
            int id = event.getPointerId(index);
            fingerMove(id, index, event);
        }
    }

    private void fingerMove(int id, int index, MotionEvent event) {
        float x = event.getX(index);
        float y = event.getY(index);
        fingerMove(id, x, y);
    }


    private void fingerUp(int id, int index, MotionEvent event) {
        float x = event.getX(index);
        float y = event.getY(index);
        fingerUp(id, x, y);
    }

    private void fingerDown(int id, float x, float y) {
        BaseDraw curDrawing = DrawingFactory.createDrawing(getContext(), mDrawingType);
        curDrawing.paint.setColor(mDrawingType == DrawingType.Rubber ? Color.TRANSPARENT : mPaint.getColor());
        curDrawing.paint.setStrokeWidth(mDrawingType == DrawingType.Rubber ? DensityUtil.dip2px(getContext(), 7) : mPaint.getStrokeWidth());
        curDrawing.startTime = isRecord ? (System.currentTimeMillis() - recordStartTime - offsetTime) : 0;
        curDrawing.fingerDown(x / getMeasuredWidth(), y / getMeasuredHeight());
        touchList.put(id, curDrawing);
    }
    public void penDown(float x,float y){
        BaseDraw curDrawing = DrawingFactory.createDrawing(getContext(), mDrawingType);
        curDrawing.paint.setColor(mDrawingType == DrawingType.Rubber ? Color.TRANSPARENT : mPaint.getColor());
        curDrawing.paint.setStrokeWidth(mDrawingType == DrawingType.Rubber ? DensityUtil.dip2px(getContext(), 7) : mPaint.getStrokeWidth());
        curDrawing.startTime = isRecord ? (System.currentTimeMillis() - recordStartTime - offsetTime) : 0;
        curDrawing.fingerDown(x, y);
        touchList.put(0, curDrawing);
    }
    public void penMove(float x,float y){
        if (touchList.get(0) != null) {
            touchList.get(0).fingerMove(x, y);
            invalidate();
        }
    }
    private void fingerMove(int id, float x, float y) {
        if (touchList.get(id) != null) {
            touchList.get(id).fingerMove(x / getMeasuredWidth(), y / getMeasuredHeight());
            invalidate();
        }
    }
    public void penUp(float x,float y){
        BaseDraw interactiveDrawing = touchList.get(0);
        if (interactiveDrawing != null) {
            interactiveDrawing.fingerUp(x, y);
            interactiveDrawing.id = mDrawListener == null ? alreadyDrawnList.size() : mDrawListener.resultCallBack(interactiveDrawing);
            mCanvas.drawPath(interactiveDrawing.getTransformPath(getMeasuredWidth(), getMeasuredHeight()), interactiveDrawing.paint);
            alreadyDrawnList.add(interactiveDrawing);
            if (isRecord)
                RecordList.add(interactiveDrawing);
        }
        invalidate();
    }
    private void fingerUp(int id, float x, float y) {
        BaseDraw interactiveDrawing = touchList.get(id);
        if (interactiveDrawing != null) {
            interactiveDrawing.fingerUp(x / getMeasuredWidth(), y / getMeasuredHeight());
            interactiveDrawing.id = mDrawListener == null ? alreadyDrawnList.size() : mDrawListener.resultCallBack(interactiveDrawing);
            mCanvas.drawPath(interactiveDrawing.getTransformPath(getMeasuredWidth(), getMeasuredHeight()), interactiveDrawing.paint);
            alreadyDrawnList.add(interactiveDrawing);
            if (isRecord)
                RecordList.add(interactiveDrawing);
        }
    }

    public void addBitmap(Bitmap bitmap) {
        final ImageView imageView = new AutoImageView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.default_image_width), FrameLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(params);
        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(new MultiTouchListener());
        addView(imageView);
        childEntries.put(imageView, new EditBtnEntry());
        imageView.post(new Runnable() {
            @Override
            public void run() {
                imageView.setTranslationX(getWidth() / 2 - imageView.getWidth() / 2);
                imageView.setTranslationY(getHeight() / 2 - imageView.getHeight() / 2);
                invalidate();
            }
        });
    }

    public void addText(String text) {
        final TextView textView = new TextView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.default_text_width), FrameLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setMinHeight((int) DensityUtil.dip2px(getContext(), 100));
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(30);
        textView.setOnTouchListener(new MultiTouchListener());
        addView(textView);
        childEntries.put(textView, new EditBtnEntry());
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setTranslationX(getWidth() / 2 - textView.getWidth() / 2);
                textView.setTranslationY(getHeight() / 2 - textView.getHeight() / 2);
                invalidate();
            }
        });
    }

    public void setDrawingType(DrawingType mDrawingType) {
        this.mDrawingType = mDrawingType;
    }
}
