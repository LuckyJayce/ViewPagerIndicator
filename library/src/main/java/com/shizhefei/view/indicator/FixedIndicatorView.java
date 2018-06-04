package com.shizhefei.view.indicator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar.Gravity;

import java.util.LinkedList;
import java.util.List;

public class FixedIndicatorView extends LinearLayout implements Indicator {

    private IndicatorAdapter mAdapter;

    private OnItemSelectedListener onItemSelectedListener;

    private OnIndicatorItemClickListener onIndicatorItemClickListener;

    private int mSelectedTabIndex = -1;

    public static final int SPLITMETHOD_EQUALS = 0;
    public static final int SPLITMETHOD_WEIGHT = 1;
    public static final int SPLITMETHOD_WRAP = 2;

    private int splitMethod = SPLITMETHOD_EQUALS;
    private int state = ViewPager.SCROLL_STATE_IDLE;

    public FixedIndicatorView(Context context) {
        super(context);
        init();
    }

    @SuppressLint("NewApi")
    public FixedIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public FixedIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inRun = new InRun();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        inRun.stop();
    }

    @Override
    public void setAdapter(IndicatorAdapter adapter) {
        if (this.mAdapter != null) {
            this.mAdapter.unRegistDataSetObserver(dataSetObserver);
        }
        this.mAdapter = adapter;
        adapter.registDataSetObserver(dataSetObserver);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int count = getChildCount();
        if (count == 1) {
            centerView = getChildAt(0);
            centerViewLayoutParams = (LayoutParams) centerView.getLayoutParams();
        }
    }

    public ScrollBar getScrollBar() {
        return scrollBar;
    }

    @Override
    public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public IndicatorAdapter getIndicatorAdapter() {
        return mAdapter;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    private boolean itemClickable = true;

    public void setItemClickable(boolean clickable) {
        this.itemClickable = clickable;
    }

    @Override
    public boolean isItemClickable() {
        return itemClickable;
    }

    private int mPreSelectedTabIndex = -1;

    @Override
    public void setCurrentItem(int item, boolean anim) {
        int count = getCount();
        if (count == 0) {
            return;
        }
        if (item < 0) {
            item = 0;
        } else if (item > count - 1) {
            item = count - 1;
        }
        if (mSelectedTabIndex != item) {
            mPreSelectedTabIndex = mSelectedTabIndex;
            mSelectedTabIndex = item;

            if (!inRun.isFinished()) {
                inRun.stop();
            }
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                updateTabSelectState(item);
                if (anim && getMeasuredWidth() != 0 && getItemOutView(item).getMeasuredWidth() != 0 && mPreSelectedTabIndex >= 0 && mPreSelectedTabIndex < getTabCountInLayout()) {
                    int sx = getItemOutView(mPreSelectedTabIndex).getLeft();
                    int ex = getItemOutView(item).getLeft();
                    final float pageDelta = (float) Math.abs(ex - sx) / (getItemOutView(item).getMeasuredWidth());
                    int duration = (int) ((pageDelta + 1) * 100);
                    duration = Math.min(duration, 600);
                    inRun.startScroll(sx, ex, duration);
                } else {
                    notifyPageScrolled(item, 0, 0);
                }
            } else {
                if (onTransitionListener == null) {
                    updateTabSelectState(item);
                }
            }
        }
    }

    private void updateTabSelectState(int selectItem) {
        if (mAdapter == null) {
            return;
        }
        int count = mAdapter.getCount();
        for (int i = 0; i < count; i++) {
            View selectView = getItemViewUnCheck(i);
            if (selectView != null) {
                selectView.setSelected(selectItem == i);
            }
        }
    }

    @Override
    public int getCurrentItem() {
        return mSelectedTabIndex;
    }

    private List<ViewGroup> views = new LinkedList<ViewGroup>();

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChange() {
            if (!inRun.isFinished()) {
                inRun.stop();
            }
            int count = getTabCountInLayout();
            int newCount = mAdapter.getCount();
            views.clear();
            for (int i = 0; i < count && i < newCount; i++) {
                views.add((ViewGroup) getItemOutView(i));
            }
            removeAllViews();
            int size = views.size();
            for (int i = 0; i < newCount; i++) {
                LinearLayout result = new LinearLayout(getContext());
                View view;
                if (i < size) {
                    View temp = views.get(i).getChildAt(0);
                    views.get(i).removeView(temp);
                    view = mAdapter.getView(i, temp, result);
                } else {
                    view = mAdapter.getView(i, null, result);
                }
                if (onTransitionListener != null) {
                    onTransitionListener.onTransition(view, i, i == mSelectedTabIndex ? 1 : 0);
                }
                result.addView(view);
                result.setOnClickListener(onClickListener);
                result.setTag(i);
                addView(result, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            }
            if (centerView != null) {
                setCenterView(centerView, centerViewLayoutParams);
            }
            mPreSelectedTabIndex = -1;
            setCurrentItem(mSelectedTabIndex, false);
            measureTabs();
        }
    };

    public void setSplitMethod(int splitMethod) {
        this.splitMethod = splitMethod;
        measureTabs();
    }

    public int getSplitMethod() {
        return splitMethod;
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (itemClickable) {
                int i = (Integer) v.getTag();
                ViewGroup parent = (ViewGroup) v;
                View itemView = parent.getChildAt(0);
                if (onIndicatorItemClickListener == null || !onIndicatorItemClickListener.onItemClick(itemView, i)) {
                    setCurrentItem(i);
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.onItemSelected(parent.getChildAt(0), i, mPreSelectedTabIndex);
                    }
                }
            }
        }
    };

    private ScrollBar scrollBar;

    @Override
    public void setScrollBar(ScrollBar scrollBar) {
        int paddingBottom = getPaddingBottom();
        int paddingTop = getPaddingTop();
        if (this.scrollBar != null) {
            switch (this.scrollBar.getGravity()) {
                case BOTTOM_FLOAT:
                    paddingBottom = paddingBottom - scrollBar.getHeight(getHeight());
                    break;

                case TOP_FLOAT:
                    paddingTop = paddingTop - scrollBar.getHeight(getHeight());
                    break;
                default:
                    break;
            }
        }
        this.scrollBar = scrollBar;
        switch (this.scrollBar.getGravity()) {
            case BOTTOM_FLOAT:
                paddingBottom = paddingBottom + scrollBar.getHeight(getHeight());
                break;

            case TOP_FLOAT:
                paddingTop = paddingTop + scrollBar.getHeight(getHeight());
                break;
            default:
                break;
        }
        setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), paddingBottom);
        // measureScrollBar(true);
    }

    private InRun inRun;

    private class InRun implements Runnable {
        private int updateTime = 20;

        private Scroller scroller;
        private final Interpolator sInterpolator = new Interpolator() {
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        };

        public InRun() {
            super();
            scroller = new Scroller(getContext(), sInterpolator);
        }

        public void startScroll(int startX, int endX, int dration) {
            scroller.startScroll(startX, 0, endX - startX, 0, dration);
            ViewCompat.postInvalidateOnAnimation(FixedIndicatorView.this);
            post(this);
        }

        public boolean isFinished() {
            return scroller.isFinished();
        }

        public boolean computeScrollOffset() {
            return scroller.computeScrollOffset();
        }

        public int getCurrentX() {
            return scroller.getCurrX();
        }

        public void stop() {
            if (scroller.isFinished()) {
                scroller.abortAnimation();
            }
            removeCallbacks(this);
        }

        @Override
        public void run() {
            ViewCompat.postInvalidateOnAnimation(FixedIndicatorView.this);
            if (!scroller.isFinished()) {
                postDelayed(this, updateTime);
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (scrollBar != null && scrollBar.getGravity() == Gravity.CENTENT_BACKGROUND) {
            drawSlideBar(canvas);
        }
        super.dispatchDraw(canvas);
        if (scrollBar != null && scrollBar.getGravity() != Gravity.CENTENT_BACKGROUND) {
            drawSlideBar(canvas);
        }
    }

    private void drawSlideBar(Canvas canvas) {
        if (mAdapter == null || scrollBar == null) {
            inRun.stop();
            return;
        }
        final int count = mAdapter.getCount();
        if (count == 0) {
            inRun.stop();
            return;
        }
        if (getCurrentItem() >= count) {
            setCurrentItem(count - 1);
            inRun.stop();
            return;
        }
        float offsetX;
        float offsetY;
        switch (this.scrollBar.getGravity()) {
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
        int tabWidth;
        View currentView = null;
        if (!inRun.isFinished() && inRun.computeScrollOffset()) {
            offsetX = inRun.getCurrentX();
            int position = 0;
            for (int i = 0; i < count; i++) {
                currentView = getItemOutView(i);
                if (currentView.getLeft() <= offsetX && offsetX < currentView.getRight()) {
                    position = i;
                    break;
                }
            }
            int width = currentView.getWidth();
            int positionOffsetPixels = (int) (offsetX - currentView.getLeft());
            float positionOffset = (offsetX - currentView.getLeft()) / width;
            notifyPageScrolled(position, positionOffset, positionOffsetPixels);
            tabWidth = measureScrollBar(position, positionOffset, true);

        } else if (state != ViewPager.SCROLL_STATE_IDLE) {
            currentView = getItemOutView(mPosition);
            int width = currentView.getWidth();
            offsetX = currentView.getLeft() + width * mPositionOffset;
            notifyPageScrolled(mPosition, mPositionOffset, mPositionOffsetPixels);
            tabWidth = measureScrollBar(mPosition, mPositionOffset, true);

        } else {
            tabWidth = measureScrollBar(mSelectedTabIndex, 0, true);
            currentView = getItemOutView(mSelectedTabIndex);
            if (currentView == null) {
                return;
            }
            offsetX = currentView.getLeft();
        }
        int scrollBarHeight = scrollBar.getSlideView().getHeight();
        int scrollBarWidth = scrollBar.getSlideView().getWidth();
        offsetX += (tabWidth - scrollBarWidth) / 2;

        int saveCount = canvas.save();

        int indicatorWidth = getMeasuredWidth();
        int indicatorHeight = getMeasuredHeight();

        //如果绘制的scrollbar超出了IndicatorView，那么就把超出的部分绘制在最前面，相当于loop的展示，末尾的部分又重新回到最开始的位置
        //为了实现这一点，首先要把scrollbar先绘制到cacheBitmap上，然后就可以把分两部分通过canvas绘制到view上
        if (mAdapter.isLoop() && offsetX + scrollBarWidth > indicatorWidth) {

            //创建一个和IndicatorView一样大小的Bitmap用于绘制
            if (cacheBitmap == null || cacheBitmap.getWidth() < scrollBarWidth || cacheBitmap.getWidth() < scrollBarHeight) {
                cacheBitmap = Bitmap.createBitmap(indicatorWidth, indicatorHeight, Bitmap.Config.ARGB_8888);
                cacheCanvas.setBitmap(cacheBitmap);
            }

            float unDraw = offsetX + scrollBarWidth - indicatorWidth;
            cacheCanvas.save();
            cacheCanvas.clipRect(0, 0, scrollBarWidth, scrollBarHeight);
            cacheCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            scrollBar.getSlideView().draw(cacheCanvas);
            cacheCanvas.restore();

            int saveCount2 = canvas.save();
            canvas.translate(offsetX, offsetY);
            canvas.clipRect(0, 0, scrollBarWidth, scrollBarHeight); // needed
            //绘制前面一部分
            canvas.drawBitmap(cacheBitmap, 0, 0, null);
            canvas.restoreToCount(saveCount2);

            //绘制后面超出的一部分
            canvas.clipRect(0, 0, unDraw, scrollBarHeight); // needed
            cacheMatrix.setTranslate(unDraw - tabWidth, 0);
            canvas.drawBitmap(cacheBitmap, cacheMatrix, null);
        } else {
            canvas.translate(offsetX, offsetY);
            canvas.clipRect(0, 0, scrollBarWidth, scrollBarHeight); // needed
            //直接绘制
            scrollBar.getSlideView().draw(canvas);
        }
        canvas.restoreToCount(saveCount);
    }

    private Bitmap cacheBitmap;
    private Matrix cacheMatrix = new Matrix();
    private Canvas cacheCanvas = new Canvas();

    private int[] prePositions = {-1, -1};

    private void notifyPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position < 0 || position > getCount() - 1) {
            return;
        }
        if (scrollBar != null) {
            scrollBar.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
        if (onTransitionListener != null) {
            for (int i : prePositions) {
                if (i != position && i != position + 1) {
                    View view = getItemView(i);
                    if (view != null) {
                        onTransitionListener.onTransition(view, i, 0);
                    }
                }
            }
            prePositions[0] = position;
            prePositions[1] = position + 1;

            View view = getItemView(mPreSelectedTabIndex);
            if (view != null) {
                onTransitionListener.onTransition(view, mPreSelectedTabIndex, 0);
            }

            view = getItemView(position);
            if (view != null) {
                onTransitionListener.onTransition(view, position, 1 - positionOffset);
            }

            view = getItemView(position + 1);
            if (view != null) {
                onTransitionListener.onTransition(view, position + 1, positionOffset);
            }
        }
    }


    private int measureScrollBar(int position, float selectPercent, boolean needChange) {
        if (scrollBar == null || mAdapter == null)
            return 0;
        View view = scrollBar.getSlideView();
        if (view.isLayoutRequested() || needChange) {
            View selectV = getItemOutView(position);
            View unSelectV;
            if (position + 1 < mAdapter.getCount()) {
                unSelectV = getItemOutView(position + 1);
            } else {
                unSelectV = getItemOutView(0);
            }
            if (selectV != null) {
                int tabWidth = (int) (selectV.getWidth() * (1 - selectPercent) + (unSelectV == null ? 0 : unSelectV.getWidth() * selectPercent));
                int width = scrollBar.getWidth(tabWidth);
                int height = scrollBar.getHeight(getHeight());
                view.measure(width, height);
                view.layout(0, 0, width, height);
                return tabWidth;
            }
        }
        return scrollBar.getSlideView().getWidth();
    }

    private void measureTabs() {
        int count = getTabCountInLayout();
        switch (splitMethod) {
            case SPLITMETHOD_EQUALS:
                for (int i = 0; i < count; i++) {
                    View view = getItemOutView(i);
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.width = 0;
                    layoutParams.weight = 1;
                    view.setLayoutParams(layoutParams);
                }
                break;
            case SPLITMETHOD_WRAP:
                for (int i = 0; i < count; i++) {
                    View view = getItemOutView(i);
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.width = LayoutParams.WRAP_CONTENT;
                    layoutParams.weight = 0;
                    view.setLayoutParams(layoutParams);
                }
                break;
            case SPLITMETHOD_WEIGHT:
                for (int i = 0; i < count; i++) {
                    View view = getItemOutView(i);
                    LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                    layoutParams.width = LayoutParams.WRAP_CONTENT;
                    layoutParams.weight = 1;
                    view.setLayoutParams(layoutParams);
                }
                break;
        }
    }

    public int getCount() {
        if (mAdapter == null) {
            return 0;
        }
        return mAdapter.getCount();
    }

    @Override
    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
        super.measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measureScrollBar(mSelectedTabIndex, 1, true);
    }

    private int mPosition;
    private int mPositionOffsetPixels;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mPosition = position;
        this.mPositionOffset = positionOffset;
        this.mPositionOffsetPixels = positionOffsetPixels;
        if (scrollBar != null) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            notifyPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        this.state = state;
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            updateTabSelectState(mSelectedTabIndex);
        }
    }

    private float mPositionOffset;

    @Override
    public void setOnTransitionListener(OnTransitionListener onTransitionListener) {
        this.onTransitionListener = onTransitionListener;
        updateTabSelectState(mSelectedTabIndex);
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                View tab = getItemView(i);
                if (tab != null) {
                    onTransitionListener.onTransition(tab, i, mSelectedTabIndex == i ? 1 : 0);
                }
            }
        }
    }

    private OnTransitionListener onTransitionListener;

    @Override
    public View getItemView(int position) {
        if (mAdapter == null) {
            return null;
        }
        if (position < 0 || position > mAdapter.getCount() - 1) {
            return null;
        }
        return getItemViewUnCheck(position);
    }

    private View getItemViewUnCheck(int position) {
        final ViewGroup group = (ViewGroup) getItemOutView(position);
        if (group == null) {
            return null;
        }
        return group.getChildAt(0);
    }

    private View getItemOutView(int position) {
        //(getChildCount() - 1) / 2就是centerView的位置
        //(getChildCount() - 1) 总数减去centerView的1
        if (centerView != null && position >= (getChildCount() - 1) / 2) {
            position++;
        }
        return getChildAt(position);
    }


    private View centerView;
    private LayoutParams centerViewLayoutParams;

    public void setCenterView(View centerView, int width, int height) {
        this.centerView = centerView;
        LayoutParams layoutParams = new LayoutParams(width, height);
        layoutParams.gravity = android.view.Gravity.CENTER_VERTICAL;
        setCenterView(centerView, layoutParams);
    }

    public void setCenterView(View centerTabView) {
        setCenterView(centerTabView, centerTabView.getLayoutParams());
    }

    public void setCenterView(View centerView, ViewGroup.LayoutParams layoutParams) {
        removeCenterView();
        LayoutParams params;
        if (layoutParams == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        } else {
            params = generateLayoutParams(layoutParams);
        }
        centerViewLayoutParams = params;
        this.centerView = centerView;
        int index = getChildCount() / 2;
        addView(centerView, index, params);
    }

    public View getCenterView() {
        return centerView;
    }

    private int getTabCountInLayout() {
        if (centerView != null) {
            return getChildCount() - 1;
        }
        return getChildCount();
    }

    public void removeCenterView() {
        if (centerView != null) {
            removeView(centerView);
            centerView = null;
        }
        centerViewLayoutParams = null;
    }

    @Override
    public OnItemSelectedListener getOnItemSelectListener() {
        return onItemSelectedListener;
    }

    public void setOnIndicatorItemClickListener(OnIndicatorItemClickListener onIndicatorItemClickListener) {
        this.onIndicatorItemClickListener = onIndicatorItemClickListener;
    }

    public OnIndicatorItemClickListener getOnIndicatorItemClickListener() {
        return onIndicatorItemClickListener;
    }

    @Override
    public OnTransitionListener getOnTransitionListener() {
        return onTransitionListener;
    }

    @Override
    public int getPreSelectItem() {
        return mPreSelectedTabIndex;
    }

}
