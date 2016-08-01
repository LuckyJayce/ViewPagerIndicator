package com.shizhefei.view.viewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class DurationScroller extends Scroller {
    
    private int mScrollDuration = 800;

    public DurationScroller(Context context) {
        super(context);
    }

    public DurationScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public DurationScroller(Context context, Interpolator interpolator,
                            boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }

    public int getScrollDuration() {
        return mScrollDuration;
    }

    public void setScrollDuration(int scrollDuration) {
        this.mScrollDuration = scrollDuration;
    }
}