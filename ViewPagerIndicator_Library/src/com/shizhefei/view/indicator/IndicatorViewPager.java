package com.shizhefei.view.indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

import com.shizhefei.view.indicator.Indicator.IndicatorAdapter;
import com.shizhefei.view.indicator.Indicator.OnItemSelectedListener;
import com.shizhefei.view.viewpager.RecyclingPagerAdapter;

/**
 * 
 * @author试着飞
 * @date 2014年11月1日
 * @version 1.0
 * 将indicatorView，ViewPager联合使用
 */
public class IndicatorViewPager {
	private Indicator indicatorView;
	private ViewPager viewPager;
	private IndicatorPagerAdapter adapter;
	private OnIndicatorPageChangeListener onIndicatorPageChangeListener;

	public IndicatorViewPager(Indicator indicator, ViewPager viewPager) {
		super();
		this.indicatorView = indicator;
		this.viewPager = viewPager;
		viewPager.setOnPageChangeListener(onPageChangeListener);
		indicatorView.setOnItemSelectListener(onItemSelectedListener);
	}

	public OnIndicatorPageChangeListener getOnIndicatorPageChangeListener() {
		return onIndicatorPageChangeListener;
	}

	/**
	 * 设置页面切换监听
	 * 
	 * @return
	 */
	public void setOnIndicatorPageChangeListener(OnIndicatorPageChangeListener onIndicatorPageChangeListener) {
		this.onIndicatorPageChangeListener = onIndicatorPageChangeListener;
	}

	/**
	 * 设置适配器
	 * 
	 * @param adapter
	 */
	public void setAdapter(IndicatorPagerAdapter adapter) {
		this.adapter = adapter;
		viewPager.setAdapter(adapter.getPagerAdapter());
		indicatorView.setAdapter(adapter.getIndicatorAdapter());
	}

	/**
	 * 切换至指定的页面
	 * 
	 * @param item
	 *            页面的索引
	 * @param anim
	 *            是否动画效果
	 */
	public void setCurrentItem(int item, boolean anim) {
		viewPager.setCurrentItem(item, anim);
		indicatorView.setCurrentItem(item, anim);
	}

	public int getCurrentItem() {
		return viewPager.getCurrentItem();
	}

	public Indicator getIndicatorView() {
		return indicatorView;
	}

	public ViewPager getViewPager() {
		return viewPager;
	}

	public IndicatorPagerAdapter getAdapter() {
		return this.adapter;
	}

	/**
	 * 通知适配器数据变化，重新加载页面
	 */
	public void notifyDataSetChanged() {
		if (this.adapter != null) {
			this.adapter.notifyDataSetChanged();
		}
	}

	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(View selectItemView, int select, int preSelect) {
			viewPager.setCurrentItem(select, viewPager.isCanScroll());
			if (onIndicatorPageChangeListener != null) {
				onIndicatorPageChangeListener.onIndicatorPageChange(preSelect, select);
			}
		}
	};

	public static interface OnIndicatorPageChangeListener {
		/**
		 * 注意 preItem 可能为 -1。表示之前没有选中过,每次adapter.notifyDataSetChanged也会将preItem 设置为-1；
		 * 
		 * @param preItem
		 * @param currentItem
		 */
		public void onIndicatorPageChange(int preItem, int currentItem);
	}

	private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int position) {
			indicatorView.setCurrentItem(position, true);
			if (onIndicatorPageChangeListener != null) {
				onIndicatorPageChangeListener.onIndicatorPageChange(indicatorView.getPreSelectItem(), position);
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			indicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	};

	public static interface IndicatorPagerAdapter {

		PagerAdapter getPagerAdapter();

		IndicatorAdapter getIndicatorAdapter();

		void notifyDataSetChanged();

	}

	/**
	 * viewpage 的每个页面是view的形式
	 * 
	 * @author Administrator
	 * 
	 */
	public static abstract class IndicatorViewPagerAdapter implements IndicatorPagerAdapter {

		private IndicatorAdapter indicatorAdapter = new IndicatorAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return IndicatorViewPagerAdapter.this.getViewForTab(position, convertView, parent);
			}

			@Override
			public int getCount() {
				return IndicatorViewPagerAdapter.this.getCount();
			}
		};

		private RecyclingPagerAdapter pagerAdapter = new RecyclingPagerAdapter() {

			@Override
			public int getCount() {
				return IndicatorViewPagerAdapter.this.getCount();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup container) {
				return IndicatorViewPagerAdapter.this.getViewForPage(position, convertView, container);
			}
		};

		public abstract int getCount();

		public abstract View getViewForTab(int position, View convertView, ViewGroup container);

		public abstract View getViewForPage(int position, View convertView, ViewGroup container);

		@Override
		public void notifyDataSetChanged() {
			indicatorAdapter.notifyDataSetChanged();
			pagerAdapter.notifyDataSetChanged();
		}

		@Override
		public PagerAdapter getPagerAdapter() {
			return pagerAdapter;
		}

		@Override
		public IndicatorAdapter getIndicatorAdapter() {
			return indicatorAdapter;
		}

	}

	/**
	 * viewpage 的每个页面是Fragment的形式
	 * 
	 * @author Administrator
	 * 
	 */
	public static abstract class IndicatorFragmentPagerAdapter implements IndicatorPagerAdapter {
		private FragmentListPageAdapter pagerAdapter;

		public IndicatorFragmentPagerAdapter(FragmentManager fragmentManager) {
			super();
			pagerAdapter = new FragmentListPageAdapter(fragmentManager) {

				@Override
				public int getCount() {
					return IndicatorFragmentPagerAdapter.this.getCount();
				}

				@Override
				public Fragment getItem(int position) {
					return IndicatorFragmentPagerAdapter.this.getFragmentForPage(position);
				}
			};
		}

		private IndicatorAdapter indicatorAdapter = new IndicatorAdapter() {

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return IndicatorFragmentPagerAdapter.this.getViewForTab(position, convertView, parent);
			}

			@Override
			public int getCount() {
				return IndicatorFragmentPagerAdapter.this.getCount();
			}
		};

		public abstract int getCount();

		public abstract View getViewForTab(int position, View convertView, ViewGroup container);

		public abstract Fragment getFragmentForPage(int position);

		@Override
		public void notifyDataSetChanged() {
			indicatorAdapter.notifyDataSetChanged();
			pagerAdapter.notifyDataSetChanged();
		}

		@Override
		public PagerAdapter getPagerAdapter() {
			return pagerAdapter;
		}

		@Override
		public IndicatorAdapter getIndicatorAdapter() {
			return indicatorAdapter;
		}

	}

}
