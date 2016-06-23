package com.shizhefei.indicator.year;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shizhefei.indicator.demo.R;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.RecyclerIndicatorView;
import com.shizhefei.view.indicator.slidebar.SpringBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

/**
 * Created by LuckyJayce on 2016/6/23.
 */
public class YearActivity extends FragmentActivity {
    private IndicatorViewPager indicatorViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);
        ViewPager viewPager = (ViewPager) findViewById(R.id.year_viewPager);
        Indicator indicator = (RecyclerIndicatorView) findViewById(R.id.year_indicator);
        int selectColorId = Color.parseColor("#f8f8f8");
        int unSelectColorId = Color.parseColor("#010101");
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColorId, unSelectColorId));
        indicator.setScrollBar(new SpringBar(getApplicationContext(), Color.GRAY));
        viewPager.setOffscreenPageLimit(4);
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new YearAdapter(1800, 2100));
        indicatorViewPager.setCurrentItem(2016 - 1800, false);
    }

    private class YearAdapter extends IndicatorViewPager.IndicatorViewPagerAdapter {
        private int startYear;
        private int endYear;

        public YearAdapter(int startYear, int endYear) {
            this.startYear = startYear;
            this.endYear = endYear;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.tab_top2, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setPadding(60, 0, 60, 0);
            textView.setText(String.valueOf(startYear + position));
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.fragment_tabmain_item, container, false);
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
            return endYear - startYear;
        }
    }
}
