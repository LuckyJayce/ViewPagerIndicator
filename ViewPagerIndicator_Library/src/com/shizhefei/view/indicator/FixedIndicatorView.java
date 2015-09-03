package com.shizhefei.view.indicator;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar.Gravity;

public class FixedIndicatorView extends LinearLayout implements Indicator {

	private IndicatorAdapter mAdapter;

	private OnItemSelectedListener onItemSelectedListener;

	private int mSelectedTabIndex = -1;

	public static final int SPLITMETHOD_EQUALS = 0;
	public static final int SPLITMETHOD_WEIGHT = 1;
	public static final int SPLITMETHOD_WRAP = 2;

	private int splitMethod = SPLITMETHOD_EQUALS;

	public FixedIndicatorView(Context context) {
		super(context);
		init();
	}

	@SuppressLint("NewApi")
	public FixedIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public FixedIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		inRun = new InRun();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		inRun.stop();
	}

	@Override
	public void setAdapter(IndicatorAdapter adapter) {
		if (this.mAdapter != null) {
			this.mAdapter.unRegistDataSetObserver(dataSetObserver);
		}
		this.mAdapter = adapter;
		adapter.registDataSetObserver(dataSetObserver);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener) {
		this.onItemSelectedListener = onItemSelectedListener;
	}

	@Override
	public IndicatorAdapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setCurrentItem(int item) {
		setCurrentItem(item, true);
	}

	private int mPreSelectedTabIndex = -1;

	@Override
	public void setCurrentItem(int item, boolean anim) {
		int count = getCount();
		if (count == 0) {
			return;
		}
		if (item < 0) {
			item = 0;
		} else if (item > count - 1) {
			item = count - 1;
		}
		if (mSelectedTabIndex != item) {
			mPreSelectedTabIndex = mSelectedTabIndex;
			mSelectedTabIndex = item;
			final int tabCount = mAdapter.getCount();
			for (int i = 0; i < tabCount; i++) {
				final ViewGroup group = (ViewGroup) getChildAt(i);
				View child = group.getChildAt(0);
				final boolean isSelected = (i == item);
				child.setSelected(isSelected);
			}

			if (!inRun.isFinished()) {
				inRun.stop();
			}
			if (positionOffset < 0.02f || positionOffset > 0.98f || !anim) {
				notifyPageScrolled(item, 0, 0);
				initNotifyOnPageScrollListener();
			}
			if (getWidth() != 0 && anim && mPositionOffset < 0.01f && mPreSelectedTabIndex >= 0 && mPreSelectedTabIndex < getChildCount()) {
				int sx = getChildAt(mPreSelectedTabIndex).getLeft();
				int ex = getChildAt(item).getLeft();
				final float pageDelta = (float) Math.abs(ex - sx) / (getChildAt(item).getWidth());
				int duration = (int) ((pageDelta + 1) * 100);
				duration = Math.min(duration, 600);
				inRun.startScroll(sx, ex, duration);
			}
		}
	}

	private void initNotifyOnPageScrollListener() {
		int tabCount;
		if (mAdapter != null && (tabCount = mAdapter.getCount()) > 1) {
			if (onPageScrollListener != null && tabCount > 1) {
				if (mPreSelectedTabIndex >= 0) {
					View view1 = getItemView(mPreSelectedTabIndex);
					if (view1 != null) {
						onPageScrollListener.onTransition(view1, mPreSelectedTabIndex, 0);
					}
				}
				if (mSelectedTabIndex >= 0) {
					View view1 = getItemView(mSelectedTabIndex);
					if (view1 != null) {
						onPageScrollListener.onTransition(view1, mSelectedTabIndex, 1);
					}
				}
			}
		}
	}

	@Override
	public int getCurrentItem() {
		return mSelectedTabIndex;
	}

	private List<ViewGroup> views = new LinkedList<ViewGroup>();

	private DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChange() {
			if (!inRun.isFinished()) {
				inRun.stop();
			}
			positionOffset = 0;
			int count = getChildCount();
			int newCount = mAdapter.getCount();
			views.clear();
			for (int i = 0; i < count && i < newCount; i++) {
				views.add((ViewGroup) getChildAt(i));
			}
			removeAllViews();
			int size = views.size();
			for (int i = 0; i < newCount; i++) {
				LinearLayout result = new LinearLayout(getContext());
				View view;
				if (i < size) {
					View temp = views.get(i).getChildAt(0);
					views.get(i).removeView(temp);
					view = mAdapter.getView(i, temp, result);
				} else {
					view = mAdapter.getView(i, null, result);
				}
				result.addView(view);
				result.setOnClickListener(onClickListener);
				result.setTag(i);
				addView(result, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			}
			mPreSelectedTabIndex = -1;
			setCurrentItem(mSelectedTabIndex, false);
			measureTabs();
		}
	};

	public void setSplitMethod(int splitMethod) {
		this.splitMethod = splitMethod;
		measureTabs();
	}

	public int getSplitMethod() {
		return splitMethod;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int i = (Integer) v.getTag();
			ViewGroup parent = (ViewGroup) v;
			setCurrentItem(i);
			if (onItemSelectedListener != null) {
				onItemSelectedListener.onItemSelected(parent.getChildAt(0), i, mPreSelectedTabIndex);
			}
		}
	};

	private ScrollBar scrollBar;

	@Override
	public void setScrollBar(ScrollBar scrollBar) {
		int paddingBottom = getPaddingBottom();
		int paddingTop = getPaddingTop();
		if (this.scrollBar != null) {
			switch (this.scrollBar.getGravity()) {
			case BOTTOM_FLOAT:
				paddingBottom = paddingBottom - scrollBar.getHeight(getHeight());
				break;

			case TOP_FLOAT:
				paddingTop = paddingTop - scrollBar.getHeight(getHeight());
				break;
			default:
				break;
			}
		}
		this.scrollBar = scrollBar;
		switch (this.scrollBar.getGravity()) {
		case BOTTOM_FLOAT:
			paddingBottom = paddingBottom + scrollBar.getHeight(getHeight());
			break;

		case TOP_FLOAT:
			paddingTop = paddingTop + scrollBar.getHeight(getHeight());
			break;
		default:
			break;
		}
		setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), paddingBottom);
		// measureScrollBar(true);
	}

	private InRun inRun;

	private class InRun implements Runnable {
		private int updateTime = 20;

		private Scroller scroller;
		private final Interpolator sInterpolator = new Interpolator() {
			public float getInterpolation(float t) {
				t -= 1.0f;
				return t * t * t * t * t + 1.0f;
			}
		};

		public InRun() {
			super();
			scroller = new Scroller(getContext(), sInterpolator);
		}

		public void startScroll(int startX, int endX, int dration) {
			scroller.startScroll(startX, 0, endX - startX, 0, dration);
			ViewCompat.postInvalidateOnAnimation(FixedIndicatorView.this);
			post(this);
		}

		public boolean isFinished() {
			return scroller.isFinished();
		}

		public boolean computeScrollOffset() {
			return scroller.computeScrollOffset();
		}

		public int getCurrentX() {
			return scroller.getCurrX();
		}

		public void stop() {
			if (scroller.isFinished()) {
				scroller.abortAnimation();
			}
			removeCallbacks(this);
		}

		@Override
		public void run() {
			ViewCompat.postInvalidateOnAnimation(FixedIndicatorView.this);
			if (!scroller.isFinished()) {
				postDelayed(this, updateTime);
			}
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		boolean in = false;
		if (scrollBar != null && scrollBar.getGravity() == Gravity.CENTENT_BACKGROUND) {
			in = true;
			drawSlideBar(canvas);
		}
		super.dispatchDraw(canvas);
		if (scrollBar != null && scrollBar.getGravity() != Gravity.CENTENT_BACKGROUND) {
			in = true;
			drawSlideBar(canvas);
		}
		if (!in) {
			inRun.stop();
		}
	}

	private void drawSlideBar(Canvas canvas) {
		if (mAdapter == null || scrollBar == null) {
			return;
		}
		final int count = mAdapter.getCount();
		if (count == 0) {
			inRun.stop();
			return;
		}
		if (getCurrentItem() >= count) {
			setCurrentItem(count - 1);
			inRun.stop();
			return;
		}
		float offsetX = 0;
		int offsetY = 0;
		switch (this.scrollBar.getGravity()) {
		case CENTENT_BACKGROUND:
		case CENTENT:
			offsetY = (getHeight() - scrollBar.getHeight(getHeight())) / 2;
			break;
		case TOP:
		case TOP_FLOAT:
			offsetY = 0;
			break;
		case BOTTOM:
		case BOTTOM_FLOAT:
		default:
			offsetY = getHeight() - scrollBar.getHeight(getHeight());
			break;
		}
		View currentView = null;
		if (!inRun.isFinished() && inRun.computeScrollOffset()) {
			offsetX = inRun.getCurrentX();
			int position = 0;
			for (int i = 0; i < count; i++) {
				currentView = getChildAt(i);
				if (currentView.getLeft() <= offsetX && offsetX < currentView.getRight()) {
					position = i;
					break;
				}
			}
			int width = currentView.getWidth();
			int positionOffsetPixels = (int) (offsetX - currentView.getLeft());
			float positionOffset = (offsetX - currentView.getLeft()) / width;
			notifyPageScrolled(position, positionOffset, positionOffsetPixels);
		} else if (mPositionOffset > 0.001f) {
			currentView = getChildAt(mPosition);
			int width = currentView.getWidth();
			offsetX = currentView.getLeft() + width * mPositionOffset;
			notifyPageScrolled(mPosition, mPositionOffset, mPositionOffsetPixels);
		} else {
			currentView = getChildAt(mPosition);
			if (currentView == null) {
				return;
			}
			offsetX = currentView.getLeft();
		}
		if (inRun.isFinished()) {
			inRun.stop();
		}
		int tabWidth = measureScrollBar(mPosition, mPositionOffset, true);
		int width = scrollBar.getSlideView().getWidth();
		offsetX += (tabWidth - width) / 2;
		int saveCount = canvas.save();
		canvas.translate(offsetX, offsetY);
		canvas.clipRect(0, 0, width, scrollBar.getSlideView().getHeight()); // needed
		scrollBar.getSlideView().draw(canvas);
		canvas.restoreToCount(saveCount);
	}

	private int[] prePositions = { -1, -1 };
	private float positionOffset;

	private void notifyPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		if (position < 0 || position > getCount() - 1) {
			return;
		}
		this.positionOffset = positionOffset;
		if (scrollBar != null) {
			scrollBar.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}
		if (onPageScrollListener != null) {
			for (int i : prePositions) {
				if (i != position && i != position + 1) {
					View view = getItemView(i);
					if (view != null) {
						onPageScrollListener.onTransition(view, i, 0);
					}
				}
			}
			prePositions[0] = position;
			prePositions[1] = position + 1;
			View view = getItemView(position);
			onPageScrollListener.onTransition(view, position, 1 - positionOffset);
			view = getItemView(position + 1);
			if (view != null) {
				onPageScrollListener.onTransition(view, position + 1, positionOffset);
			}

		}
	}

	private int measureScrollBar(int position, float selectPercent, boolean needChange) {
		if (scrollBar == null)
			return 0;
		View view = scrollBar.getSlideView();
		if (view.isLayoutRequested() || needChange) {
			View selectV = getChildAt(position);
			View unSelectV = getChildAt(position + 1);
			if (selectV != null) {
				int tabWidth = (int) (selectV.getWidth() * (1 - selectPercent) + (unSelectV == null ? 0 : unSelectV.getWidth() * selectPercent));
				int width = scrollBar.getWidth(tabWidth);
				int height = scrollBar.getHeight(getHeight());
				view.measure(width, height);
				view.layout(0, 0, width, height);
				return tabWidth;
			}
		}
		return scrollBar.getSlideView().getWidth();
	}

	private void measureTabs() {
		// int width = getMeasuredWidth();
		int count = getChildCount();
		// if (count == 0 || width == 0) {
		// return;
		// }
		switch (splitMethod) {
		case SPLITMETHOD_EQUALS:
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
				layoutParams.width = 0;
				layoutParams.weight = 1;
				view.setLayoutParams(layoutParams);
			}
			break;
		case SPLITMETHOD_WRAP:
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
				layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
				layoutParams.weight = 0;
				view.setLayoutParams(layoutParams);
			}
			break;
		case SPLITMETHOD_WEIGHT:
			for (int i = 0; i < count; i++) {
				View view = getChildAt(i);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
				layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
				layoutParams.weight = 1;
				view.setLayoutParams(layoutParams);
			}
			break;
		}
	}

	public int getCount() {
		if (mAdapter == null) {
			return 0;
		}
		return mAdapter.getCount();
	}

	@Override
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		super.measureChildren(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		measureScrollBar(mSelectedTabIndex, 1, true);
	}

	private int mPosition;
	private int mPositionOffsetPixels;

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		this.mPosition = position;
		this.mPositionOffset = positionOffset;
		this.mPositionOffsetPixels = positionOffsetPixels;
		if (scrollBar != null) {
			ViewCompat.postInvalidateOnAnimation(this);
		} else {
			notifyPageScrolled(position, positionOffset, positionOffsetPixels);
		}
	}

	private float mPositionOffset;

	@Override
	public void setOnTransitionListener(OnTransitionListener onPageScrollListener) {
		this.onPageScrollListener = onPageScrollListener;
		initNotifyOnPageScrollListener();
	}

	private OnTransitionListener onPageScrollListener;

	@Override
	public View getItemView(int position) {
		if (position < 0 || position > mAdapter.getCount() - 1) {
			return null;
		}
		final ViewGroup group = (ViewGroup) getChildAt(position);
		return group.getChildAt(0);
	}

	@Override
	public OnItemSelectedListener getOnItemSelectListener() {
		return onItemSelectedListener;
	}

	@Override
	public OnTransitionListener getOnTransitionListener() {
		return onPageScrollListener;
	}

	@Override
	public int getPreSelectItem() {
		return mPreSelectedTabIndex;
	}

}
