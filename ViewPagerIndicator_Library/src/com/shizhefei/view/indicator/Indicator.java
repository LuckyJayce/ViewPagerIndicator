package com.shizhefei.view.indicator;

import java.util.LinkedHashSet;
import java.util.Set;

import android.view.View;
import android.view.ViewGroup;

import com.shizhefei.view.indicator.slidebar.ScrollBar;

/**
 * 指示器
 * 
 * @author Administrator
 * 
 */
public interface Indicator {

	public void setAdapter(IndicatorAdapter adapter);

	public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener);

	public OnItemSelectedListener getOnItemSelectListener();

	public void setOnTransitionListener(OnTransitionListener onPageScrollListener);

	public OnTransitionListener getOnTransitionListener();

	public void setScrollBar(ScrollBar scrollBar);

	public IndicatorAdapter getAdapter();

	public void setCurrentItem(int item);

	public void setCurrentItem(int item, boolean anim);

	public View getItemView(int item);

	public int getCurrentItem();

	public int getPreSelectItem();

	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

	public static abstract class IndicatorAdapter {
		private Set<DataSetObserver> observers = new LinkedHashSet<Indicator.DataSetObserver>(2);

		public abstract int getCount();

		public abstract View getView(int position, View convertView, ViewGroup parent);

		public void notifyDataSetChanged() {
			for (DataSetObserver dataSetObserver : observers) {
				dataSetObserver.onChange();
			}
		}

		public void registDataSetObserver(DataSetObserver observer) {
			observers.add(observer);
		}

		public void unRegistDataSetObserver(DataSetObserver observer) {
			observers.remove(observer);
		}

	}

	static interface DataSetObserver {
		public void onChange();
	}

	public static interface OnItemSelectedListener {
		/**
		 * 注意 preItem 可能为 -1。表示之前没有选中过,每次adapter.notifyDataSetChanged也会将preItem
		 * 设置为-1；
		 * 
		 * @param selectItemView
		 *            当前选中的view
		 * @param select
		 *            当前选中项的索引
		 * @param preSelect
		 *            之前选中项的索引
		 */
		public void onItemSelected(View selectItemView, int select, int preSelect);
	}

	public static interface OnTransitionListener {
		public void onTransition(View view, int position, float selectPercent);
	}
}