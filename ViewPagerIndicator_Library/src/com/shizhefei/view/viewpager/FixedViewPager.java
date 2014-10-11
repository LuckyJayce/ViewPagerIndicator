package com.shizhefei.view.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 可以动态设置ViewPager 是否可以左右滑动切换页面
 * {@link com.shizhefei.view.viewpager.FixedViewPager#setScanScroll(boolean)}
 * 
 * @author Administrator
 * 
 */
public class FixedViewPager extends ViewPager {

	private boolean isCanScroll = false;

	public FixedViewPager(Context context) {
		super(context);
	}
	public FixedViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (isCanScroll) {
			return super.onTouchEvent(arg0);
		} else {
			return false;
		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if (isCanScroll) {
			return super.onInterceptTouchEvent(arg0);
		} else {
			return false;
		}

	}
}