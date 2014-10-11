package com.shizhefei.view.indicator;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class FixedIndicatorView extends LinearLayout implements Indicator {

	private IndicatorAdapter mAdapter;

	private OnItemSelectedListener onItemSelectedListener;

	private int mSelectedTabIndex = -1;

	public FixedIndicatorView(Context context) {
		super(context);
	}

	@SuppressLint("NewApi")
	public FixedIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FixedIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
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

	@Override
	public void setCurrentItem(int item) {
		setCurrentItem(item, true);
	}

	@Override
	public void setCurrentItem(int item, boolean anim) {
		mSelectedTabIndex = item;
		final int tabCount = mAdapter.getCount();
		for (int i = 0; i < tabCount; i++) {
			final ViewGroup group = (ViewGroup) getChildAt(i);
			View child = group.getChildAt(0);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
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
				addView(result, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1));
			}
			setCurrentItem(0, false);
		}
	};

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

}
