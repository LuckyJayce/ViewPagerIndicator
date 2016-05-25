package com.shizhefei.view.indicator.slidebar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * 
 * @author试着飞
 * @date 2014年11月2日
 * @version 1.0 通过xml来设置 滑动块
 */
public class LayoutBar implements ScrollBar {
	protected Context context;
	protected int layoutId;
	protected View view;
	protected int height;
	protected int width;
	protected Gravity gravity;
	private LayoutParams layoutParams;

	public LayoutBar(Context context, int layoutId) {
		this(context, layoutId, Gravity.BOTTOM);
	}

	public LayoutBar(Context context, int layoutId, Gravity gravity) {
		super();
		this.context = context;
		this.layoutId = layoutId;
		this.view = LayoutInflater.from(context).inflate(layoutId, new LinearLayout(context), false);
		layoutParams = view.getLayoutParams();
		this.height = view.getLayoutParams().height;
		this.width = view.getLayoutParams().width;
		this.gravity = gravity;
	}

	@Override
	public int getHeight(int tabHeight) {
		if (height <= 0) {
			layoutParams.height = tabHeight;
			return tabHeight;
		}
		return height;
	}

	@Override
	public int getWidth(int tabWidth) {
		if (width <= 0) {
			layoutParams.width = tabWidth;
			return tabWidth;
		}
		return width;
	}

	@Override
	public View getSlideView() {
		return view;
	}

	@Override
	public Gravity getGravity() {
		return gravity;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

}
