package com.shizhefei.view.indicator;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
/**
 * 通过ScrollView形式动态扩充的Indicator，适用于项比较多。
 * 
 * @author Administrator
 * 
 */
public class ScrollIndicatorView extends HorizontalScrollView implements Indicator {
	private IndicatorAdapter mAdapter;
	private OnItemSelectedListener onItemSelectedListener;
	private LinearLayout layout;
	private int mSelectedTabIndex = -1;

	public ScrollIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		layout = new LinearLayout(context);
		addView(layout);
		setHorizontalScrollBarEnabled(false);
	}

	@Override
	public void setAdapter(IndicatorAdapter adapter) {
		if (this.mAdapter != null) {
			this.mAdapter.setDataSetObserver(null);
		}
		this.mAdapter = adapter;
		adapter.setDataSetObserver(dataSetObserver);
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

	private List<ViewGroup> views = new LinkedList<ViewGroup>();

	private DataSetObserver dataSetObserver = new DataSetObserver() {

		@Override
		public void onChange() {
			int count = getChildCount();
			int newCount = mAdapter.getCount();
			views.clear();
			for (int i = 0; i < count && i < newCount; i++) {
				views.add((ViewGroup) getChildAt(i));
			}
			layout.removeAllViews();
			int size = views.size();
			for (int i = 0; i < newCount; i++) {
				LinearLayout result = new LinearLayout(getContext());
				View view;
				if (i < size) {
					view = mAdapter.getView(i, views.get(i).getChildAt(0), result);
				} else {
					view = mAdapter.getView(i, null, result);
				}
				result.addView(view);
				result.setOnClickListener(onClickListener);
				result.setTag(i);
				layout.addView(result, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			}
			setCurrentItem(0, false);
		}

		private OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				int i = (Integer) v.getTag();
				ViewGroup parent = (ViewGroup) v;
				setCurrentItem(i);
				if (onItemSelectedListener != null) {
					onItemSelectedListener.onItemSelected(parent.getChildAt(0), i);
				}
			}
		};
	};

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			// Re-post the selector we saved
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	private Runnable mTabSelector;

	private void animateToTab(final int position) {
		final View tabView = layout.getChildAt(position);
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			public void run() {
				final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}

	@Override
	public void setCurrentItem(int item) {
		setCurrentItem(item, true);
	}

	@Override
	public void setCurrentItem(int item, boolean anim) {
		mSelectedTabIndex = item;
		final int tabCount = mAdapter.getCount();
		for (int i = 0; i < tabCount; i++) {
			final ViewGroup group = (ViewGroup) layout.getChildAt(i);
			View child = group.getChildAt(0);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if (isSelected) {
				if (anim) {
					animateToTab(item);
				} else {
					final View tabView = layout.getChildAt(item);
					final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
					scrollTo(scrollPos, 0);
				}
			}
		}
	}

	@Override
	public int getCurrentItem() {
		return mSelectedTabIndex;
	}
}
