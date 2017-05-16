package com.chuangju.pathnote.lib;

import android.graphics.Paint;

/**
 * Use Singleton mode to get Brush class
 */
public class Brush extends Paint
{
	/**
	 * Generate the instance when the class is loaded
	 */
	private static Brush brush = new Brush();

	/**
	 * Make the constructor private, to stop others to get instance by the
	 * default constructor
	 */
	private Brush()
	{
	}

	/**
	 * Provide a static method that can be access by others.
	 * 
	 * @return the single instance
	 */
	public static Brush getPen()
	{
		if (brush == null) {
			brush = new Brush();
			brush.reset();
		}
		return brush;
	}
	public Brush clone() {
		Brush newbrush = new Brush();
		newbrush.setAntiAlias(true);
		newbrush.setDither(true);
		newbrush.setColor(brush.getColor());
		newbrush.setStyle(Paint.Style.STROKE);
		newbrush.setStrokeJoin(Paint.Join.ROUND);
		newbrush.setStrokeCap(Paint.Cap.ROUND);
		newbrush.setStrokeWidth(brush.getStrokeWidth());
		return newbrush;
	}
	/**
	 * reSet the brush
	 */
	public void reset()
	{
		brush.setXfermode(null);
		brush.setAntiAlias(true);
		brush.setDither(true);
		brush.setColor(0xFF000000);
		brush.setStyle(Paint.Style.STROKE);
		brush.setStrokeJoin(Paint.Join.ROUND);
		brush.setStrokeCap(Paint.Cap.ROUND);
		brush.setStrokeWidth(2);
	}
}