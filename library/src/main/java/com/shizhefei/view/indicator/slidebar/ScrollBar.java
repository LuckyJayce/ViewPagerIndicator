package com.shizhefei.view.indicator.slidebar;

import android.view.View;

public interface ScrollBar {
    public static enum Gravity {
        TOP,
        TOP_FLOAT,
        BOTTOM,
        BOTTOM_FLOAT,
        CENTENT,
        CENTENT_BACKGROUND
    }

    public int getHeight(int tabHeight);

    public int getWidth(int tabWidth);

    public View getSlideView();

    public Gravity getGravity();

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
}
