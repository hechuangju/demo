package com.chuangju.pathnote.lib.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AutoImageView extends android.support.v7.widget.AppCompatImageView {

	public AutoImageView(Context context) {
		super(context);
	}

	public AutoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getDrawable() != null) {
			int width = getDrawable().getIntrinsicWidth();
			int height = getDrawable().getIntrinsicHeight();
			int currentWidth = getMeasuredWidth();
			int currentHeight = getMeasuredHeight();
			float xx = (float) currentWidth / (float) width;
			currentHeight = (int) (xx * height);
			setMeasuredDimension(currentWidth, currentHeight);
		}
	}
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		requestLayout();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable != null) {
			float scale = (float) getMeasuredWidth() / (float) drawable.getIntrinsicWidth();
			canvas.save();
			canvas.scale(scale, scale);
			drawable.draw(canvas);
			canvas.restore();
		}
	}
}