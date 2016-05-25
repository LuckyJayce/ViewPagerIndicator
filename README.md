ViewPagerIndicator
==================  
点击上方的release，可以下载最新的jar包    
Download sample [Apk](https://github.com/LuckyJayce/ViewPagerIndicator/blob/master/raw/ViewPagerIndicator_Demo.apk?raw=true)   

###1. 支持自定义tab样式  
###2. 支持自定义滑动块样式和位置  
###3. 支持自定义切换tab的过渡效果  
###4. 支持子界面的预加载和界面缓存  
###5. 支持设置界面是否可滑动  
###6. android:minSdkVersion="8" android:targetSdkVersion="21" 


# 使用方法 #  
    package com.shizhefei.indicator.guide;
    
    import android.os.Bundle;
    import android.support.v4.app.FragmentActivity;
    import android.support.v4.view.ViewPager;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.ViewGroup.LayoutParams;
    
    import com.shizhefei.indicator.R;
    import com.shizhefei.view.indicator.Indicator;
    import com.shizhefei.view.indicator.IndicatorViewPager;
    import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorPagerAdapter;
    import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorViewPagerAdapter;
    
    public class GuideActivity extends FragmentActivity {
    	private IndicatorViewPager indicatorViewPager;
    	private LayoutInflater inflate;
    
    	@Override
    	protected void onCreate(Bundle arg0) {
    		super.onCreate(arg0);
    		setContentView(R.layout.activity_guide);
    		ViewPager viewPager = (ViewPager) findViewById(R.id.guide_viewPager);
    		Indicator indicator = (Indicator) findViewById(R.id.guide_indicator);
    		// 将viewPager和indicator使用
    		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
    		inflate = LayoutInflater.from(getApplicationContext());
    		// 设置indicatorViewPager的适配器
    		indicatorViewPager.setAdapter(adapter);
    	}
    
    	private IndicatorPagerAdapter adapter = new IndicatorViewPagerAdapter() {
    		private int[] images = { R.drawable.p1, R.drawable.p2, R.drawable.p3,
    				R.drawable.p4 };
    
    		/**
    		 * 获取tab
    		 */
    		@Override
    		public View getViewForTab(int position, View convertView,
    				ViewGroup container) {
    			if (convertView == null) {
    				convertView = inflate.inflate(R.layout.tab_guide, container,
    						false);
    			}
    			return convertView;
    		}
    
    		/**
    		 * 获取每一个界面
    		 */
    		@Override
    		public View getViewForPage(int position, View convertView,
    				ViewGroup container) {
    			if (convertView == null) {
    				convertView = new View(getApplicationContext());
    				convertView.setLayoutParams(new LayoutParams(
    						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    			}
    			convertView.setBackgroundResource(images[position]);
    			return convertView;
    		}
    
    		/**
    		 * 获取界面数量
    		 */
    		@Override
    		public int getCount() {
    			return images.length;
    		}
    	};
    }

# 效果图 #
有了该类库你可以实现以下布局
![image](https://github.com/LuckyJayce/ViewPagerIndicator/blob/master/raw/1.png)
![image](https://github.com/LuckyJayce/ViewPagerIndicator/blob/master/raw/2.png)
![image](https://github.com/LuckyJayce/ViewPagerIndicator/blob/master/raw/3.png)
![image](https://github.com/LuckyJayce/ViewPagerIndicator/blob/master/raw/4.png)
# 主要的类 #
## 1.ViewPager ##  
这个版本以后 android-support-v4 可以用原生的了 没有重写ViewPager。  
原先重写的ViewPager的setPrepareNumber 用 Fragment继承于LazyFragment代替实现懒加载  
原先setCanScroll 转移到了 ViewPager的子类SViewPager上  

2.使用LazyFragment来配合ViewPager的setOffscreenPageLimit进行懒加载界面和防止重新创建界面**

## 2.Indicator ##
顾名思义是指示器的意思。有点像水平方向的listview 可以自定义item。

**子类FixedIndicatorView 主要用于固定大小来平均分配tab的情况。
子类ScrollIndicatorView 主要用于多个tab可以进行滑动。**

## 3.indicatorViewPager  ##
用于将ViewPager和Indicator 联合使用。  


			indicatorViewPager.setAdapter(IndicatorPagerAdapter adapter)  
	        // 设置它可以自定义实现在滑动过程中，tab项的字体变化，颜色变化等等过渡效果  
    		indicatorViewPager.setIndicatorOnTransitionListener(onTransitionListener);  
    		// 设置它可以自定义滑动块的样式  
    		indicatorViewPager.setIndicatorScrollBar(scrollBar);  
    		// 设置page是否可滑动切换  
    		indicatorViewPager.setPageCanScroll(false);  
    		/*
    		 * 设置缓存界面的个数，左右两边缓存界面的个数，不会被重新创建。 默认是1，表示左右两边  
    		 * 相连的1个界面和当前界面都会被缓存住，比如切换到左边的一个界面，那个界面是不会重新创建的。  
    		 */  
    		indicatorViewPager.setPageOffscreenLimit(1);  
    		/*
    		 * 设置预加载界面的个数。左右两边加载界面的个数 默认是1，表示左右两边 相连的1个界面会和当前界面同时加载  
    		 */  
    		indicatorViewPager.setPagePrepareNumber(1);  
    		// 设置页面切换监听  
    		indicatorViewPager.setOnIndicatorPageChangeListener(onIndicatorPageChangeListener);  
    		// 设置page间的图片的宽度  
    		indicatorViewPager.setPageMargin(1);  
    		// 设置page间的图片  
    		indicatorViewPager.setPageMarginDrawable(d);  

## 4.IndicatorPagerAdapter  ##
子类IndicatorFragmentPagerAdapter 用于 界面是fragment的形式。
子类IndicatorViewPagerAdapter 用于 界面是View的形式.

indicatorViewPager.setOnIndicatorPageChangeListener(onIndicatorPageChangeListener)设置界面的切换监听。

Indicator 既可以单独使用。也可以通过indicatorViewPager的形式联合viewpager一起使用。

## 5.ScrollBar  ##
tab的滑动块通过indicatorViewPager.setIndicatorScrollBar(scrollBar);进行设置
子类有  
ColorBar 颜色的滑动块   
DrawableBar 图片滑动块  
LayoutBar 布局滑动块  
TextWidthColorBar 大小同tab里的text一样宽的颜色的滑动块  
SpringBar 实现拖拽效果的圆形滑动块  该类修改于https://github.com/chenupt/SpringIndicator  

## 5.OnTransitionListener  ##
子类有  
OnTransitionTextListener tab的字体颜色变化，和字体大小变化效果    

## 6.LazyFragment 懒加载Fragment    
Fragment继承该类实现 显示Framgment的时候才会去创建你自己的界面布局，否则不创建。  
  
1.实现原理：  
一开始onCreateView的时候只是加载一个空的FrameLayout  
当通过结合onCreateView和setUserVisibleHint两个方法进行判断是否需要加载真正的布局界面，需要的时候把真正的布局界面添加到之前的空的FrameLayout上面  
  
2.回调方法：  
            onCreateViewLazy  对应 onCreateView  
            onDestroyViewLazy 对应 onDestroyView  
            onResumeLazy      对应 onResume  
            onPauseLazy       对应 onPause  
            onDestroy 和 onCreate 方法不变  
            另外添加了onFragmentStartLazy fragment显示的时候调用，在ViewPager界面切换的时候你就可以通过这个判断是否显示  
            onFragmentStopLazy fragment不显示的时候调用  
            上面所有有lazy结尾的方法都意味着 真正的布局正在显示  
              
3.使用方法  
放心在onCreateViewLazy 的时候创建布局，初始化数据。调用该方法的时候，界面已经要显示啦
  在onDestroyViewLazy的方法里面做释放操作  

## 说明 ##
项目 ViewPagerIndicator_Demo 是示例代码。 看了这个例子你会惊奇的发现里面居然都是通过viewpager实现，没有使用tabhost，而所有形式的tab都是用Indicator实现。
项目 ViewPagerIndicator_Library 是类库

有什么建议可以发到我的邮箱  794629068@qq.com


License
=======

    Copyright 2014 shizhefei（LuckyJayce）

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
