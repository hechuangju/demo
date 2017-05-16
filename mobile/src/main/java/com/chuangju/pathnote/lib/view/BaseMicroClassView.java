package com.chuangju.pathnote.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.chuangju.pathnote.lib.DensityUtil;
import com.chuangju.pathnote.lib.DrawingParser;
import com.chuangju.pathnote.lib.DrawingType;
import com.chuangju.pathnote.lib.Page;
import com.chuangju.pathnote.lib.Pages;
import com.chuangju.pathnote.lib.shape.BaseDraw;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public abstract class BaseMicroClassView extends FrameLayout {
    public ArrayList<BaseDraw> alreadyDrawnList = new ArrayList<>();
    private static final String wei_backBmpPath = "/_background.jpg";
    private static final String wei_voicePath = "/_sound.mp3";
    private static final String wei_cacheBmpPath = "/_cover.jpg";
    private static final String wei_xmlPath = "/_draw.xml";
    private String voicePath;
    protected BitmapDrawable backDrawable;
    protected BitmapDrawable crownDrawable;
    //    protected Matrix backDrawableMatrix = new Matrix();
    protected ArrayList<BaseDraw> list = new ArrayList<>();
    protected Canvas mCanvas;
    protected boolean eraseEnable;
    protected Paint mPaint = new Paint();
    protected Paint mTeacherRulePaint = new Paint();
    protected DrawingType mDrawingType = DrawingType.HandDraw;
    float distance;

    public BaseMicroClassView(Context context) {
        this(context, null);
    }


    public BaseMicroClassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseMicroClassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
//        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3);


        distance = DensityUtil.dip2px(context, 5);
        mTeacherRulePaint.setAntiAlias(true);
        mTeacherRulePaint.setDither(true);
        mTeacherRulePaint.setStrokeWidth(1);
        mTeacherRulePaint.setTextSize(DensityUtil.dip2px(context, 12));
        mTeacherRulePaint.setColor(Color.RED);
        mTeacherRulePaint.setStyle(Paint.Style.FILL);
        setBackgroundColor(Color.WHITE);
    }


    public void setBackLayout(final int width, final int height) {
        if (width > 0 && height > 0) {
            if (width != getWidth() || height != getHeight())
                setBackImage(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
        }
    }

    public void setBackImage(int resId) {
        setBackImage(BitmapFactory.decodeResource(getResources(), resId));
    }

    public void setBackImage(Bitmap bitmap) {
        setBackImage(new BitmapDrawable(getResources(), bitmap));
    }

    ImageView imageView;

    public void setBackImage(BitmapDrawable drawable) {
        if (this.backDrawable != null && this.backDrawable != drawable)
            unscheduleDrawable(backDrawable);
        this.backDrawable = drawable;
        if (imageView != null)
            removeView(imageView);
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setImageDrawable(this.backDrawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView);
//        requestLayout();
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
    }

    public void setPaintWidth(float width) {
        mPaint.setStrokeWidth(width);
    }

    public void setScaleEnable(boolean enable) {
        setOnTouchListener(enable ? new ViewScaleTouchListener() : null);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
//        if (backDrawable != null)
//            canvas.drawBitmap(backDrawable.getBitmap(), backDrawableMatrix, null);
        super.dispatchDraw(canvas);
        if (teacherRuleContent.gridShow) {
            mTeacherRulePaint.setStyle(Paint.Style.STROKE);
            mTeacherRulePaint.setPathEffect(new DashPathEffect(new float[]{6, 6}, 1));
            mTeacherRulePaint.setColor(Color.DKGRAY);
            float startY = teacherRuleContent.yLineLocation * getHeight();
            float startX = teacherRuleContent.xLineLocation * getWidth();
            float step = 30f * (float) getWidth() / (float) teacherRuleContent.width;
            for (float i = startX + step; i < getWidth(); i = i + step) {
                canvas.drawLine(i, 0, i, getHeight(), mTeacherRulePaint);
            }
            for (float i = startX; i > 0; i = i - step) {
                canvas.drawLine(i, 0, i, getHeight(), mTeacherRulePaint);
            }
            for (float i = startY + step; i < getHeight(); i = i + step) {
                canvas.drawLine(0, i, getWidth(), i, mTeacherRulePaint);
            }
            for (float i = startY; i > 0; i = i - step) {
                canvas.drawLine(0, i, getWidth(), i, mTeacherRulePaint);
            }
        }
        if (teacherRuleContent.xLineShow || teacherRuleContent.yLineShow) {
            mTeacherRulePaint.setColor(Color.RED);
            mTeacherRulePaint.setPathEffect(null);
            if (teacherRuleContent.xLineShow) {
                xLinePath.reset();
                mTeacherRulePaint.setStyle(Paint.Style.STROKE);
                xLinePath.moveTo(0, teacherRuleContent.yLineLocation * getHeight());
                xLinePath.lineTo(getWidth(), teacherRuleContent.yLineLocation * getHeight());
                canvas.drawPath(xLinePath, mTeacherRulePaint);
                xLinePath.reset();
                mTeacherRulePaint.setStyle(Paint.Style.FILL);
                xLinePath.moveTo(getWidth() - 2 * distance, teacherRuleContent.yLineLocation * getHeight() - distance);
                xLinePath.lineTo(getWidth(), teacherRuleContent.yLineLocation * getHeight());
                xLinePath.lineTo(getWidth() - 2 * distance, teacherRuleContent.yLineLocation * getHeight() + distance);
                canvas.drawPath(xLinePath, mTeacherRulePaint);
                canvas.drawText("X", getWidth() - mTeacherRulePaint.measureText("X") - 2 * distance, teacherRuleContent.yLineLocation * getHeight() + 3 * distance, mTeacherRulePaint);
            }
            if (teacherRuleContent.yLineShow) {
                yLinePath.reset();
                mTeacherRulePaint.setStyle(Paint.Style.STROKE);
                yLinePath.moveTo(teacherRuleContent.xLineLocation * getWidth(), 0);
                yLinePath.lineTo(teacherRuleContent.xLineLocation * getWidth(), getHeight());
                canvas.drawPath(yLinePath, mTeacherRulePaint);
                yLinePath.reset();
                mTeacherRulePaint.setStyle(Paint.Style.FILL);
                yLinePath.moveTo(teacherRuleContent.xLineLocation * getWidth() - distance, 2 * distance);
                yLinePath.lineTo(teacherRuleContent.xLineLocation * getWidth(), 0);
                yLinePath.lineTo(teacherRuleContent.xLineLocation * getWidth() + distance, 2 * distance);
                canvas.drawPath(yLinePath, mTeacherRulePaint);
                canvas.drawText("Y", teacherRuleContent.xLineLocation * getWidth() + 2 * distance, 4 * distance, mTeacherRulePaint);
            }
            canvas.drawText("O", teacherRuleContent.xLineLocation * getWidth() - distance - mTeacherRulePaint.measureText("X"), teacherRuleContent.yLineLocation * getHeight() + 3 * distance, mTeacherRulePaint);
        }
        if (crownDrawable != null)
            canvas.drawBitmap(crownDrawable.getBitmap(), 0, 0, null);
        if (teacherRule.show) {
            mTeacherRulePaint.setColor(Color.RED);
            mTeacherRulePaint.setPathEffect(null);
            mTeacherRulePaint.setStyle(Paint.Style.FILL);
            teacherRulePath.reset();
            teacherRuleRect.set(teacherRule.x * getWidth(), teacherRule.y * getHeight(), (teacherRule.x + teacherRule.width) * getWidth(), (teacherRule.y + teacherRule.height) * getHeight());
            teacherRulePath.moveTo(teacherRuleRect.right - teacherRuleRect.width() / 4, teacherRuleRect.top);
            teacherRulePath.lineTo(teacherRuleRect.right, teacherRuleRect.centerY());
            teacherRulePath.lineTo(teacherRuleRect.right - teacherRuleRect.width() / 4, teacherRuleRect.bottom);
            teacherRulePath.addRect(teacherRuleRect.left, teacherRuleRect.top + teacherRuleRect.height() / 4, teacherRuleRect.right - teacherRuleRect.width() / 4, teacherRuleRect.bottom - teacherRuleRect.height() / 4, Path.Direction.CW);
            canvas.drawPath(teacherRulePath, mTeacherRulePaint);
        }
    }

    private Page.TeacherRule teacherRule = new Page.TeacherRule();
    private Page.Content teacherRuleContent = new Page.Content();
    private Path teacherRulePath = new Path();
    private Path xLinePath = new Path();
    private Path yLinePath = new Path();
    private RectF teacherRuleRect = new RectF();

    public void updatePage(Page page) {
        teacherRule = page.teacherRule;
        teacherRuleContent = page.content;
        invalidate();
    }

    public void drawLine(BaseDraw baseDraw) {
        alreadyDrawnList.add(baseDraw);
        if (mCanvas != null) {
            Log.e(getClass().getSimpleName(), "drawLine" + baseDraw.toString());
            baseDraw.drawAllOnCanvas(mCanvas);
            invalidate();
        }
    }

    public void drawLines() {
        clearCanvas();
        alreadyDrawnList.addAll(list);
        for (int i = 0; i < list.size(); i++) {
            BaseDraw baseDraw = list.get(i);
            baseDraw.drawAllOnCanvas(mCanvas);
        }
        invalidate();
    }

    public BaseDraw Revocation() {
        if (alreadyDrawnList.size() > 0) {
            BaseDraw baseDraw = alreadyDrawnList.get(alreadyDrawnList.size() - 1);
            alreadyDrawnList.remove(baseDraw);
            ArrayList<BaseDraw> oldList = new ArrayList<>(alreadyDrawnList);
            clearCanvas();
            for (int i = 0; i < oldList.size(); i++) {
                drawLine(oldList.get(i));
            }
            return baseDraw;
        }
        return null;
    }

    public void clearCanvas() {
        if (mCanvas != null) {
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawRect(0, 0, mCanvas.getWidth(), mCanvas.getHeight(), clearPaint);
            alreadyDrawnList.clear();
            eraseEnable(false);
            invalidate();
        }
    }

    public void reSet() {
        clearCanvas();
    }

    public void eraseEnable(boolean enable) {
        eraseEnable = enable;
        if (enable)
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            mPaint.setXfermode(null);
    }

    public void setupFile(String dirFile) throws FileNotFoundException {
        String backBmpPath = dirFile + wei_backBmpPath;
        voicePath = dirFile + wei_voicePath;
        String xmlPath = dirFile + wei_xmlPath;
        Pages pages = DrawingParser.parserXml(getContext(), new FileInputStream(xmlPath));
        Page page = pages.pageList.get(pages.currentPage);
        if (page != null) {
            Page.Content content = page.getContent();
            if (content != null) {
                backBmpPath = dirFile + "/" + content.imagePath;
                voicePath = dirFile + "/" + content.audioPath;
            }
            ArrayList<BaseDraw> list = new ArrayList<>();
            Bitmap bitmap = BitmapFactory.decodeFile(backBmpPath);
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(content.backGroundWidth, content.backGroundHeight, Bitmap.Config.ARGB_8888);
            }
            setBackImage(bitmap);
            int pageIndex = page.getContent() == null ? 0 : page.getContent().current_document;
            for (int i = 0; i < page.subPages.get(pageIndex).lines.size(); i++) {
                BaseDraw interactiveDrawing = page.subPages.get(pageIndex).lines.valueAt(i);
                interactiveDrawing.formatPath(1f / bitmap.getWidth(), 1f / bitmap.getHeight());
                list.add(interactiveDrawing);
            }
            this.list = list;
        }
    }

    public static boolean checkFile(String dirFile) {
        File backBmpPath = new File(dirFile + wei_backBmpPath);
        File voicePath = new File(dirFile + wei_voicePath);
        File cachBmpPath = new File(dirFile + wei_cacheBmpPath);
        File xmlPath = new File(dirFile + wei_xmlPath);
        return backBmpPath.exists() && voicePath.exists() && cachBmpPath.exists() && xmlPath.exists();
    }

    public String getVoicePath() {
        return voicePath;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.e(getClass().getSimpleName(), "onDetachedFromWindow--->");
        if (this.crownDrawable != null) {
            unscheduleDrawable(crownDrawable);
            crownDrawable = null;
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float viewWidth = getMeasuredWidth();
        float viewHeight = getMeasuredHeight();
        if (this.crownDrawable != null) {
            unscheduleDrawable(crownDrawable);
            crownDrawable = null;
        }
        if (viewHeight > 0 && viewWidth > 0) {
            if (backDrawable != null) {
                float backWidth = backDrawable.getIntrinsicWidth();
                float backHeight = backDrawable.getIntrinsicHeight();
                float scaleX = viewWidth / backWidth;
                float scaleY = viewHeight / backHeight;
                float scale = Math.min(scaleX, scaleY);
                viewWidth = backWidth * scale;
                viewHeight = backHeight * scale;
            } else {
                viewWidth = getMeasuredWidth();
                viewHeight = getMeasuredHeight();
            }
            Bitmap bitmap = Bitmap.createBitmap((int) viewWidth, (int) viewHeight, Bitmap.Config.ARGB_8888);
            crownDrawable = new BitmapDrawable(getResources(), bitmap);
            setMeasuredDimension((int) viewWidth, (int) viewHeight);
            if (imageView != null) {
                Log.e("hechuangju", imageView.getMeasuredHeight() + "====");
                int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec((int) viewWidth, MeasureSpec.EXACTLY);
                int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) viewHeight, MeasureSpec.EXACTLY);
                imageView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
//                imageView.getLayoutParams().width = (int) viewWidth;
//                imageView.getLayoutParams().height = (int) viewHeight;
            }
//            super.measure(MeasureSpec.makeMeasureSpec((int) viewWidth, MeasureSpec.getMode(widthMeasureSpec)), MeasureSpec.makeMeasureSpec((int) viewHeight, MeasureSpec.getMode(heightMeasureSpec)));
            mCanvas = new Canvas(crownDrawable.getBitmap());
            ArrayList<BaseDraw> oldList = new ArrayList<>(alreadyDrawnList);
            alreadyDrawnList.clear();
            clearCanvas();
            for (int i = 0; i < oldList.size(); i++) {
                drawLine(oldList.get(i));
            }
            invalidate();
        }
    }
}
