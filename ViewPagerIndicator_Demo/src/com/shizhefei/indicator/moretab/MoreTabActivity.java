package com.shizhefei.indicator.moretab;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.indicator.R;
import com.shizhefei.indicator.slide.SlideFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

public class MoreTabActivity extends FragmentActivity {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moretab);
		ViewPager viewPager = (ViewPager) findViewById(R.id.moretab_viewPager);
		ScrollIndicatorView indicator = (ScrollIndicatorView) findViewById(R.id.moretab_indicator);
		indicator.setScrollBar(new ColorBar(this, Color.RED, 5));

		// 设置滚动监听
		int selectColorId = R.color.tab_top_text_2;
		int unSelectColorId = R.color.tab_top_text_1;
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColorId(this, selectColorId, unSelectColorId));

		viewPager.setOffscreenPageLimit(2);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());
		indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 10;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top, container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText("tab" + position);
			textView.setPadding(30, 0, 30, 0);
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			MoreFragment fragment = new MoreFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(SlideFragment.INTENT_INT_INDEX, position);
			fragment.setArguments(bundle);
			return fragment;
		}

	};

}
