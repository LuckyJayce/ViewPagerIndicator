package com.shizhefei.view.indicator;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar.Gravity;

/**
 * 主要用于多个tab可以进行滑动
 */
public class ScrollIndicatorView extends HorizontalScrollView implements Indicator {
    private final ProxyOnItemSelectListener proxyOnItemSelectListener;
    private SFixedIndicatorView fixedIndicatorView;
    private boolean isPinnedTabView = false;
    private Paint defaultShadowPaint = null;
    private Drawable customShadowDrawable;
    private int shadowWidth;
    private int state = ViewPager.SCROLL_STATE_IDLE;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ScrollIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fixedIndicatorView = new SFixedIndicatorView(context);
        addView(fixedIndicatorView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        setHorizontalScrollBarEnabled(false);
        setSplitAuto(true);

        defaultShadowPaint = new Paint();
        defaultShadowPaint.setAntiAlias(true);
        defaultShadowPaint.setColor(0x33AAAAAA);
        shadowWidth = dipToPix(3);
        defaultShadowPaint.setShadowLayer(shadowWidth, 0, 0, 0xFF000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        fixedIndicatorView.setOnItemSelectListener(proxyOnItemSelectListener = new ProxyOnItemSelectListener());
    }

    public void setSplitAuto(boolean splitAuto) {
        setFillViewport(splitAuto);
        fixedIndicatorView.setSplitAuto(splitAuto);
    }

    public boolean isSplitAuto() {
        return fixedIndicatorView.isSplitAuto();
    }

    @Override
    public void setAdapter(IndicatorAdapter adapter) {
        if (getIndicatorAdapter() != null) {
            getIndicatorAdapter().unRegistDataSetObserver(dataSetObserver);
        }
        fixedIndicatorView.setAdapter(adapter);
        adapter.registDataSetObserver(dataSetObserver);
        dataSetObserver.onChange();
    }

    @Override
    public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener) {
        proxyOnItemSelectListener.setOnItemSelectedListener(onItemSelectedListener);
    }

    private class ProxyOnItemSelectListener implements OnItemSelectedListener {
        private OnItemSelectedListener onItemSelectedListener;

        public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
            this.onItemSelectedListener = onItemSelectedListener;
        }

        public OnItemSelectedListener getOnItemSelectedListener() {
            return onItemSelectedListener;
        }

        @Override
        public void onItemSelected(View selectItemView, int select, int preSelect) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                animateToTab(select);
            }
            if (onItemSelectedListener != null) {
                onItemSelectedListener.onItemSelected(selectItemView, select, preSelect);
            }
        }
    }

    @Override
    public IndicatorAdapter getIndicatorAdapter() {
        return fixedIndicatorView.getIndicatorAdapter();
    }

    public void setPinnedTabView(boolean isPinnedTabView) {
        this.isPinnedTabView = isPinnedTabView;
        if (isPinnedTabView) {
            if (fixedIndicatorView.getChildCount() > 0) {
                pinnedTabView = fixedIndicatorView.getChildAt(0);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setPinnedShadow(Drawable shadowDrawable, int shadowWidth) {
        this.customShadowDrawable = shadowDrawable;
        this.shadowWidth = shadowWidth;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setPinnedTabBg(Drawable pinnedTabBgDrawable) {
        this.pinnedTabBgDrawable = pinnedTabBgDrawable;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void setPinnedTabBgColor(int color) {
        setPinnedTabBg(new ColorDrawable(color));
    }

    public void setPinnedTabBgId(int pinnedTabBgDrawableId) {
        setPinnedTabBg(ContextCompat.getDrawable(getContext(), pinnedTabBgDrawableId));
    }

    private Drawable pinnedTabBgDrawable;

    public void setPinnedShadow(int shadowDrawableId, int shadowWidth) {
        setPinnedShadow(ContextCompat.getDrawable(getContext(), shadowDrawableId), shadowWidth);
    }

    private DataSetObserver dataSetObserver = new DataSetObserver() {

        @Override
        public void onChange() {
            if (mTabSelector != null) {
                removeCallbacks(mTabSelector);
            }
            positionOffset = 0;
            setCurrentItem(fixedIndicatorView.getCurrentItem(), false);
            if (isPinnedTabView) {
                if (fixedIndicatorView.getChildCount() > 0) {
                    pinnedTabView = fixedIndicatorView.getChildAt(0);
                }
            }
        }
    };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            post(mTabSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private Runnable mTabSelector;
    private View pinnedTabView;
    private boolean mActionDownHappened;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (fixedIndicatorView.getCount() > 0) {
            animateToTab(fixedIndicatorView.getCurrentItem());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (unScrollPosition != -1) {
            final View tabView = fixedIndicatorView.getChildAt(unScrollPosition);
            if (tabView != null) {
                final int scrollPos = tabView.getLeft() - (getMeasuredWidth() - tabView.getMeasuredWidth()) / 2;
                if (scrollPos >= 0) {
                    smoothScrollTo(scrollPos, 0);
                    unScrollPosition = -1;
                }
            }
        }
    }

    private void animateToTab(final int position) {
        if (position < 0 || position > fixedIndicatorView.getCount() - 1) {
            return;
        }
        final View tabView = fixedIndicatorView.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean anim) {
        int count = fixedIndicatorView.getCount();
        if (count == 0) {
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item > count - 1) {
            item = count - 1;
        }
        unScrollPosition = -1;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (anim) {
                animateToTab(item);
            } else {
                final View tabView = fixedIndicatorView.getChildAt(item);
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                scrollTo(scrollPos, 0);
                unScrollPosition = item;
            }
        } else {

        }
        fixedIndicatorView.setCurrentItem(item, anim);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isPinnedTabView) {
            float x = ev.getX();
            float y = ev.getY();
            if (pinnedTabView != null && y >= pinnedTabView.getTop() && y <= pinnedTabView.getBottom() && x > pinnedTabView.getLeft()
                    && x < pinnedTabView.getRight()) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    mActionDownHappened = true;
                } else if (ev.getAction() == MotionEvent.ACTION_UP) {
                    if (mActionDownHappened) {
                        pinnedTabView.performClick();
                        invalidate(new Rect(0, 0, pinnedTabView.getMeasuredWidth(), pinnedTabView.getMeasuredHeight()));
                        mActionDownHappened = false;
                    }
                }
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private int unScrollPosition = -1;

    @Override
    public int getCurrentItem() {
        return fixedIndicatorView.getCurrentItem();
    }

    @Override
    public OnItemSelectedListener getOnItemSelectListener() {
        return proxyOnItemSelectListener.getOnItemSelectedListener();
    }

    public void setOnIndicatorItemClickListener(OnIndicatorItemClickListener onIndicatorItemClickListener) {
        fixedIndicatorView.setOnIndicatorItemClickListener(onIndicatorItemClickListener);
    }

    public OnIndicatorItemClickListener getOnIndicatorItemClickListener() {
        return fixedIndicatorView.getOnIndicatorItemClickListener();
    }

    @Override
    public void setOnTransitionListener(OnTransitionListener onPageScrollListener) {
        fixedIndicatorView.setOnTransitionListener(onPageScrollListener);
    }

    @Override
    public OnTransitionListener getOnTransitionListener() {
        return fixedIndicatorView.getOnTransitionListener();
    }

    @Override
    public void setScrollBar(ScrollBar scrollBar) {
        fixedIndicatorView.setScrollBar(scrollBar);
    }

    private float positionOffset;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.positionOffset = positionOffset;
        final View tabView = fixedIndicatorView.getChildAt(position);
        if (tabView == null) {
            return;
        }
        final View tabView2 = fixedIndicatorView.getChildAt(position + 1);
        float offset = (tabView.getWidth() + (tabView2 == null ? tabView.getWidth() : tabView2.getWidth())) / 2 * positionOffset;
        final int scrollPos = (int) (tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2 + offset);
        scrollTo(scrollPos, 0);
        fixedIndicatorView.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        this.state = state;
        fixedIndicatorView.onPageScrollStateChanged(state);
//		if (state == ViewPager.SCROLL_STATE_IDLE) {
//			onPageScrolled(getCurrentItem(), 0, 0);
//		}
    }

    @Override
    public void setItemClickable(boolean clickable) {
        fixedIndicatorView.setItemClickable(clickable);
    }

    @Override
    public boolean isItemClickable() {
        return fixedIndicatorView.isItemClickable();
    }

    @Override
    public int getPreSelectItem() {
        return fixedIndicatorView.getPreSelectItem();
    }

    @Override
    public View getItemView(int item) {
        return fixedIndicatorView.getItemView(item);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isPinnedTabView) {
            int scrollX = getScrollX();
            if (pinnedTabView != null && scrollX > 0) {
                int saveCount = canvas.save();

                // 绘制固定在开始位置的pinnedTabView
                canvas.translate(scrollX + getPaddingLeft(), getPaddingTop());

                if (pinnedTabBgDrawable != null) {
                    pinnedTabBgDrawable.setBounds(0, 0, pinnedTabView.getWidth(), pinnedTabView.getHeight());
                    pinnedTabBgDrawable.draw(canvas);
                }

                ScrollBar scrollBar = fixedIndicatorView.getScrollBar();
                if (scrollBar != null && scrollBar.getGravity() == Gravity.CENTENT_BACKGROUND) {
                    drawScrollBar(canvas);
                }
                pinnedTabView.draw(canvas);
                if (scrollBar != null && scrollBar.getGravity() != Gravity.CENTENT_BACKGROUND) {
                    drawScrollBar(canvas);
                }

                int x = pinnedTabView.getWidth();
                // pinnedTabView的分割绘制阴影
                canvas.translate(x, 0);
                int shadowHeight = getHeight() - getPaddingTop() - getPaddingBottom();
                if (customShadowDrawable != null) {
                    customShadowDrawable.setBounds(0, 0, shadowWidth, shadowHeight);
                    customShadowDrawable.draw(canvas);
                } else {
                    canvas.clipRect(0, 0, shadowWidth + dipToPix(1), shadowHeight);
                    canvas.drawRect(0, 0, dipToPix(1), shadowHeight, defaultShadowPaint);
                }

                canvas.restoreToCount(saveCount);
            }
        }
    }

    private void drawScrollBar(Canvas canvas) {
        ScrollBar scrollBar = fixedIndicatorView.getScrollBar();
        // 如果scrollBar不为空，且刚好选中的是第一个的时候需要在这里重新绘制scrollBar，因为原先fixedIndicatorView回执的scrollBar被遮挡了
        if (scrollBar != null && fixedIndicatorView.getCurrentItem() == 0) {
            int drawScrollBarCount = canvas.save();

            int offsetY = 0;
            switch (scrollBar.getGravity()) {
                case CENTENT_BACKGROUND:
                case CENTENT:
                    offsetY = (getHeight() - scrollBar.getHeight(getHeight())) / 2;
                    break;
                case TOP:
                case TOP_FLOAT:
                    offsetY = 0;
                    break;
                case BOTTOM:
                case BOTTOM_FLOAT:
                default:
                    offsetY = getHeight() - scrollBar.getHeight(getHeight());
                    break;
            }
            int scrollBarWidth = scrollBar.getWidth(pinnedTabView.getWidth());
            int scrollBarHeight = scrollBar.getHeight(pinnedTabView.getHeight());
            scrollBar.getSlideView().measure(scrollBarWidth, scrollBarHeight);
            scrollBar.getSlideView().layout(0, 0, scrollBarWidth, scrollBarHeight);

            int offsetX = (pinnedTabView.getWidth() - scrollBarWidth) / 2;

            canvas.translate(offsetX, offsetY);
            canvas.clipRect(0, 0, scrollBarWidth, scrollBarHeight); // needed
            scrollBar.getSlideView().draw(canvas);

            canvas.restoreToCount(drawScrollBarCount);
        }
    }

    private static class SFixedIndicatorView extends FixedIndicatorView {

        private boolean isAutoSplit;

        public SFixedIndicatorView(Context context) {
            super(context);
        }

        public boolean isSplitAuto() {
            return isAutoSplit;
        }

        public void setSplitAuto(boolean isAutoSplit) {
            if (this.isAutoSplit != isAutoSplit) {
                this.isAutoSplit = isAutoSplit;
                if (!isAutoSplit) {
                    setSplitMethod(SPLITMETHOD_WRAP);
                }
                requestLayout();
                invalidate();
            }
        }

        @Override
        protected void onMeasure(int widthSpec, int heightSpec) {
            if (isAutoSplit) {
                ScrollIndicatorView group = (ScrollIndicatorView) getParent();
                int layoutWidth = group.getMeasuredWidth();
                if (layoutWidth != 0) {
                    int totalWidth = 0;
                    int count = getChildCount();
                    int maxCellWidth = 0;
                    for (int i = 0; i < count; i++) {
                        int width = measureChildWidth(getChildAt(i), widthSpec, heightSpec);
                        maxCellWidth = maxCellWidth < width ? width : maxCellWidth;
                        totalWidth += width;
                    }
                    if (totalWidth > layoutWidth) {
                        group.setFillViewport(false);
                        setSplitMethod(SPLITMETHOD_WRAP);
                    } else if (maxCellWidth * count > layoutWidth) {
                        group.setFillViewport(true);
                        setSplitMethod(SPLITMETHOD_WEIGHT);
                    } else {
                        group.setFillViewport(true);
                        setSplitMethod(SPLITMETHOD_EQUALS);
                    }
                }
            }
            super.onMeasure(widthSpec, heightSpec);
        }

        private int measureChildWidth(View view, int widthSpec, int heightSpec) {
            LayoutParams p = (LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), LayoutParams.WRAP_CONTENT);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec, childHeightSpec);
            return view.getMeasuredWidth() + p.leftMargin + p.rightMargin;
        }

    }

    /**
     * 根据dip值转化成px值
     *
     * @param dip
     * @return
     */
    private int dipToPix(float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
        return size;
    }

}
