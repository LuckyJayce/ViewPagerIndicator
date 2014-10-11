package com.shizhefei.view.indicator;

import android.view.View;
import android.view.ViewGroup;
/**
 * 指示器
 * 
 * @author Administrator
 * 
 */
public interface Indicator {

	public void setAdapter(IndicatorAdapter adapter);

	public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener);

	public IndicatorAdapter getAdapter();

	public void setCurrentItem(int item);

	public void setCurrentItem(int item, boolean anim);

	public int getCurrentItem();

	public static abstract class IndicatorAdapter {
		private DataSetObserver observer;

		public abstract int getCount();

		public abstract View getView(int position, View convertView, ViewGroup parent);

		public void notifyDataSetChanged() {
			observer.onChange();
		}

		void setDataSetObserver(DataSetObserver observer) {
			this.observer = observer;
		}

	}

	static interface DataSetObserver {
		public void onChange();
	}

	public static interface OnItemSelectedListener {
		public void onItemSelected(View view, int position);
	}
}
