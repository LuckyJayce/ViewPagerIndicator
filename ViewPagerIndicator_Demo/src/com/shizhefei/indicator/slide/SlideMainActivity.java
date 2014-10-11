package com.shizhefei.indicator.slide;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.shizhefei.indicator.R;

public class SlideMainActivity extends FragmentActivity {
	private SlidingMenu menu;
	private ListView menuListView;
	private ViewPager viewPager;
	private String[] pageNnames = { "界面1", "界面2", "界面3", "界面4" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slidemain);
		viewPager = (ViewPager) findViewById(R.id.slideMain_viewPager);
		/**
		 * 初始化右边菜单
		 */
		menu = new SlidingMenu(this);
		menu.setMenu(R.layout.layout_slide_menu);
		// 滑动 菜单 配置
		menu.setShadowWidthRes(R.dimen.ap_base_menu_shadow_width);
		menu.setBehindOffsetRes(R.dimen.ap_base_menu_shadow_offset);
		menu.setShadowDrawable(R.drawable.ap_base_menu_shadow);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setBehindScrollScale(0.5f);

		/**
		 * 设置右边菜单项
		 */
		menuListView = (ListView) menu.getMenu().findViewById(R.id.slideMenu_listView);
		menuListView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.item_slidemenu, pageNnames));
		menuListView.setOnItemClickListener(onMenuItemClickListener);

		// 设置viewpager保留界面，不重新加载数量
		viewPager.setOffscreenPageLimit(pageNnames.length);
		// 默认是1,，自动预加载左右两边的界面。设置viewpager预加载数为0。只加载加载当前界面。
		viewPager.setPrepareNumber(0);
		// 禁止viewpager 滑动
		viewPager.setCanScroll(false);
		viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));

		menu.showMenu();
	}

	private OnItemClickListener onMenuItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			viewPager.setCurrentItem(position,false);
			menu.toggle();
		}
	};

	private class MyAdapter extends FragmentStatePagerAdapter {

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SlideFragment fragment = new SlideFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(SlideFragment.INTENT_INT_INDEX, position);
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return pageNnames.length;
		}

	}
}
