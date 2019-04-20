package com.shizhefei.indicator.proxyfragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.shizhefei.fragment.ProxyLazyFragment;
import com.shizhefei.indicator.demo.R;

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

    private class AA extends FragmentStatePagerAdapter {
        public AA(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("tttt", "getItem position:" + position);
            Bundle bundle = new Bundle();
            bundle.putInt(BookFragment.EXTRA_INT_POSITION, position);
            return ProxyLazyFragment.lazy(BookFragment.class, bundle);
        }

        @Override
        public int getCount() {
            return 10;
        }
    }
}
