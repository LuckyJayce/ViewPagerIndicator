package com.shizhefei.indicator.tabmain;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhefei.indicator.BaseFragment;
import com.shizhefei.indicator.R;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorViewPagerAdapter;

public class MainFragment extends BaseFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
	private String tabName;

	@Override
	protected void onCreateView(Bundle savedInstanceState) {
		super.onCreateView(savedInstanceState);
		setContentView(R.layout.fragment_tabmain);
		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);
		viewPager.setOffscreenPageLimit(4);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());
		indicatorViewPager.setAdapter(adapter);
	}

	private IndicatorPagerAdapter adapter = new IndicatorViewPagerAdapter() {

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top, container, false);
			}
			TextView textView = (TextView) convertView;
			textView.setText(tabName + " " + position);
			return convertView;
		}

		@Override
		public View getViewForPage(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.fragment_tabmain_item, container, false);
			}
			final TextView textView = (TextView) convertView.findViewById(R.id.fragment_mainTab_item_textView);
			textView.setText(tabName + " " + position + " 界面加载完毕");
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
