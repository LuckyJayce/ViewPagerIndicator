package com.shizhefei.indicator.year;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.indicator.DisplayUtil;
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
        indicatorViewPager.setAdapter(new YearAdapter(1800, 10000000));
        indicatorViewPager.setCurrentItem(2016-1800, false);
    }

    private class YearAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private int startYear;
        private int endYear;

        public YearAdapter(int startYear, int endYear) {
            super(getSupportFragmentManager());
            this.startYear = startYear;
            this.endYear = endYear;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            int padding = DisplayUtil.dipToPix(getApplicationContext(), 12);
            textView.setPadding(padding, 0, padding, 0);
            textView.setText(String.valueOf(startYear + position));
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            YearFragment yearFragment = new YearFragment();
            Bundle a = new Bundle();
            a.putInt(YearFragment.INTENT_INT_POSITION, position);
            yearFragment.setArguments(a);
            return yearFragment;
        }

        @Override
        public int getCount() {
            return endYear - startYear;
        }
    }
}
