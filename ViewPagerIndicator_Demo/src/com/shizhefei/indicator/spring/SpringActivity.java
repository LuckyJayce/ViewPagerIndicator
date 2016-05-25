package com.shizhefei.indicator.spring;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhefei.indicator.R;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorViewPagerAdapter;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.slidebar.SpringBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

public class SpringActivity extends Activity {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_spring);
		ViewPager viewPager = (ViewPager) findViewById(R.id.spring_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.spring_indicator);
		int selectColorId = Color.parseColor("#f8f8f8");
		int unSelectColorId = Color.parseColor("#010101");
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColorId, unSelectColorId));
		indicator.setScrollBar(new SpringBar(getApplicationContext(), Color.GRAY));
		viewPager.setOffscreenPageLimit(4);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());
		indicatorViewPager.setAdapter(adapter);
		indicatorViewPager.setCurrentItem(1, false);
	}

	private IndicatorPagerAdapter adapter = new IndicatorViewPagerAdapter() {

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top2, container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(String.valueOf(position));
			return convertView;
		}

		@Override
		public View getViewForPage(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.fragment_tabmain_item, container, false);
			}
			final TextView textView = (TextView) convertView.findViewById(R.id.fragment_mainTab_item_textView);
			textView.setText(" " + position + " 界面加载完毕");
			final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.fragment_mainTab_item_progressBar);
			new Handler() {
				public void handleMessage(android.os.Message msg) {
					textView.setVisibility(View.VISIBLE);
					progressBar.setVisibility(View.GONE);
				}
			}.sendEmptyMessageDelayed(1, 3000);
			return convertView;
		}

		@Override
		public int getCount() {
			return 4;
		}
	};

}
