package com.shizhefei.view.indicator.slidebar;

import android.view.View;

public interface ScrollBar {
	public static enum Gravity {
		TOP, TOP_FLOAT, BOTTOM, BOTTOM_FLOAT, CENTENT, CENTENT_BACKGROUND
	}

	/**
	 * SlideView显示的高度
	 * 
	 * @return
	 */
	public int getHeight(int tabHeight);

	public int getWidth(int tabWidth);

	/**
	 * 滑动显示的view
	 * 
	 * @return
	 */
	public View getSlideView();

	/**
	 * 位置
	 * 
	 * @return
	 */
	public Gravity getGravity();

	/**
	 * 当page滑动的时候
	 * 
	 * @param selectView
	 * @param unSelectView
	 * @param position
	 * @param positionOffset
	 */
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
}
