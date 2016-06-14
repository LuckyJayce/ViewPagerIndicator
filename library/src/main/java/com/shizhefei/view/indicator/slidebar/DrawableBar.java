package com.shizhefei.view.indicator.slidebar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

/**
 * 
 * 通过图片来设置滑动块
 */
public class DrawableBar implements ScrollBar {
	protected Gravity gravity;
	protected View view;
	protected int drawableId;
	protected Drawable drawable;

	public DrawableBar(Context context, int drawableId) {
		this(context, drawableId, Gravity.BOTTOM);
	}

	public DrawableBar(Context context, int drawableId, Gravity gravity) {
		this(context, context.getResources().getDrawable(drawableId), gravity);
	}

	public DrawableBar(Context context, Drawable drawable) {
		this(context, drawable, Gravity.BOTTOM);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public DrawableBar(Context context, Drawable drawable, Gravity gravity) {
		view = new View(context);
		this.drawable = drawable;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			view.setBackground(drawable);
		} else {
			view.setBackgroundDrawable(drawable);
		}
		this.gravity = gravity;
	}

	public int getColor() {
		return drawableId;
	}

	public void setColor(int color) {
		this.drawableId = color;
		view.setBackgroundColor(color);
	}

	@Override
	public int getHeight(int tabHeight) {
		return drawable.getIntrinsicHeight();
	}

	@Override
	public int getWidth(int tabWidth) {
		return drawable.getIntrinsicWidth();
	}

	@Override
	public View getSlideView() {
		return view;
	}

	@Override
	public Gravity getGravity() {
		return gravity;
	}

	public DrawableBar setGravity(Gravity gravity) {
		this.gravity = gravity;
		return this;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
	}

}
