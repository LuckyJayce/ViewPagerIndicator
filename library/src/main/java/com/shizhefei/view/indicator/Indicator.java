/*
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
 */
package com.shizhefei.view.indicator;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.shizhefei.view.indicator.slidebar.ScrollBar;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 指示器
 */
public interface Indicator {
    /**
     * 设置适配器
     */
    void setAdapter(IndicatorAdapter adapter);

    /**
     * 设置选中监听
     *
     * @param onItemSelectedListener
     */
    void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener);

    /**
     * 获取选中监听
     *
     * @return OnItemSelectedListener
     */
    OnItemSelectedListener getOnItemSelectListener();

    /**
     * 设置Indicator tab项的点击事件，在Indicator 的 onItemSelectListener前触发和拦截处理
     * @param onIndicatorItemClickListener
     */
    void setOnIndicatorItemClickListener(OnIndicatorItemClickListener onIndicatorItemClickListener);

    OnIndicatorItemClickListener getOnIndicatorItemClickListener();

    /**
     * 设置滑动变化的转换监听，tab在切换过程中会调用此监听。<br>
     * 设置它可以自定义实现在滑动过程中，tab项的字体变化，颜色变化等等效果<br>
     * 目前提供的子类
     * {@link com.shizhefei.view.indicator.transition.OnTransitionTextListener}
     *
     * @param onTransitionListener
     */
    void setOnTransitionListener(OnTransitionListener onTransitionListener);

    OnTransitionListener getOnTransitionListener();

    /**
     * 设置滑动块<br>
     * 设置它可以自定义滑动块的样式。<br>
     * 目前提供的子类 {@link com.shizhefei.view.indicator.slidebar.ColorBar}
     * {@link com.shizhefei.view.indicator.slidebar.DrawableBar}
     * {@link com.shizhefei.view.indicator.slidebar.LayoutBar}
     *
     * @param scrollBar
     */
    void setScrollBar(ScrollBar scrollBar);

    IndicatorAdapter getIndicatorAdapter();

    /**
     * 设置当前项.<br>
     * 如果使用IndicatorViewPager则使用IndicatorViewPager.setCurrentItem而不是在调用该方法
     *
     * @param item
     */
    void setCurrentItem(int item);

    void setCurrentItem(int item, boolean anim);

    /**
     * 获取每一项的tab
     *
     * @param item 索引
     * @return ItemView
     */
    @Nullable
    View getItemView(int item);

    /**
     * 获取当前选中项
     *
     * @return current
     */
    int getCurrentItem();

    /**
     * 获取之前选中的项,可能返回-1，表示之前没有选中
     *
     * @return PreSelectItem
     */
    int getPreSelectItem();

    /**
     * ViewPager切换变化的函数
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    /**
     * 适配器
     */
    abstract class IndicatorAdapter {
        private boolean isLoop;

        boolean isLoop() {
            return isLoop;
        }

        void setIsLoop(boolean isLoop) {
            this.isLoop = isLoop;
        }

        private Set<DataSetObserver> observers = new LinkedHashSet<DataSetObserver>(2);

        public abstract int getCount();

        public abstract View getView(int position, View convertView, ViewGroup parent);

        public void notifyDataSetChanged() {
            for (DataSetObserver dataSetObserver : observers) {
                dataSetObserver.onChange();
            }
        }

        public void registDataSetObserver(DataSetObserver observer) {
            observers.add(observer);
        }

        public void unRegistDataSetObserver(DataSetObserver observer) {
            observers.remove(observer);
        }

    }

    /**
     * 数据源观察者
     */
    interface DataSetObserver {
        void onChange();
    }

    /**
     * tab项选中监听
     */
    interface OnItemSelectedListener {
        /**
         * 注意 preItem 可能为 -1。表示之前没有选中过,每次adapter.notifyDataSetChanged也会将preItem
         * 设置为-1；
         *
         * @param selectItemView 当前选中的view
         * @param select         当前选中项的索引
         * @param preSelect      之前选中项的索引
         */
        void onItemSelected(View selectItemView, int select, int preSelect);
    }

    /**
     * tab项点击监听和拦截
     */
    interface OnIndicatorItemClickListener {
        /**
         * @param clickItemView
         * @param position 点击的tab的position
         * @return 返回true表示 拦截点击事件，不会继续触发Indicator的setCurrent和调用OnItemSelectedListener
         *  返回false 按照Indicator原有的处理方式
         */
        boolean onItemClick(View clickItemView, int position);
    }

    /**
     * tab滑动变化的转换监听，tab在切换过程中会调用此监听。<br>
     * 通过它可以自定义实现在滑动过程中，tab项的字体变化，颜色变化等等效果<br>
     * 目前提供的子类
     * {@link com.shizhefei.view.indicator.transition.OnTransitionTextListener}
     */
    interface OnTransitionListener {
        void onTransition(View view, int position, float selectPercent);
    }

    void onPageScrollStateChanged(int state);

    void setItemClickable(boolean clickable);

    boolean isItemClickable();
}
