package com.shizhefei.indicator.proxyfragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shizhefei.fragment.ProxyLazyFragment;
import com.shizhefei.indicator.demo.R;
import com.shizhefei.view.indicator.FragmentListPageAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class TestProxyFragmentActivity extends FragmentActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_proxy_fragment);
        viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new AA(getSupportFragmentManager()));

        findViewById(R.id.jumpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TestExceptionActivity.class));
            }
        });
    }

    private class AA extends FragmentListPageAdapter {
        public AA(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("tttt", "getItem position:" + position);
            Bundle arguments = new Bundle();
            arguments.putInt(BookFragment.EXTRA_INT_POSITION, position);
            return ProxyLazyFragment.lazy(BookFragment.class, arguments);
        }

        @Override
        public int getCount() {
            return 10;
        }
    }
}
