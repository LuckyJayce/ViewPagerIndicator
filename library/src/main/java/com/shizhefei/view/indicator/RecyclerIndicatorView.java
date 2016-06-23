package com.shizhefei.view.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shizhefei.view.indicator.slidebar.ScrollBar;

/**
 * Created by LuckyJayce on 2016/6/14.
 */
public class RecyclerIndicatorView extends RecyclerView implements Indicator {

    private IndicatorAdapter adapter;
    private ProxyAdapter proxyAdapter;
    private LinearLayoutManager linearLayoutManager;
    private float positionOffset;
    private int positionOffsetPixels;

    public RecyclerIndicatorView(Context context) {
        super(context);
        init();
    }

    public RecyclerIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecyclerIndicatorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private OnItemSelectedListener onItemSelectedListener;

    @Override
    public void setAdapter(IndicatorAdapter adapter) {
        this.adapter = adapter;
        proxyAdapter = new ProxyAdapter(adapter);
        setAdapter(proxyAdapter);
    }

    @Override
    public void setOnItemSelectListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public OnItemSelectedListener getOnItemSelectListener() {
        return onItemSelectedListener;
    }

    @Override
    public void setOnTransitionListener(OnTransitionListener onTransitionListener) {
        this.onTransitionListener = onTransitionListener;
        if (proxyAdapter != null) {
            proxyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public OnTransitionListener getOnTransitionListener() {
        return null;
    }

    @Override
    public void setScrollBar(ScrollBar scrollBar) {
        this.scrollBar = scrollBar;
    }

    private ScrollBar scrollBar;

    private OnTransitionListener onTransitionListener;

    @Override
    public IndicatorAdapter getIndicatorAdapter() {
        return adapter;
    }

    @Override
    public void setCurrentItem(int item) {
        setCurrentItem(item, true);
    }

    @Override
    public void setCurrentItem(int item, boolean anim) {
        this.preSelectItem = this.selectItem;
        this.selectItem = item;
        if (pageScrollState == ViewPager.SCROLL_STATE_IDLE) {
            scrollToTab(item, 0);
            updateSelectTab(item);
        } else {

        }
        if (onItemSelectedListener != null) {
            onItemSelectedListener.onItemSelected(getItemView(item), selectItem, preSelectItem);
        }
    }


    private void updateSelectTab(int selectItem) {
        View preview = getItemView(preSelectItem);
        if (preview != null) {
            preview.setSelected(false);
        }
        View selectView = getItemView(selectItem);
        if (selectView != null) {
            selectView.setSelected(true);
        }
    }


    protected void scrollToTab(int position, float positionOffset) {
        int scrollOffset = 0;
        View selectedView = linearLayoutManager.findViewByPosition(position);
        View nextView = linearLayoutManager.findViewByPosition(position + 1);

        if (selectedView != null) {
            int width = getMeasuredWidth();
            float scroll1 = width / 2.f - selectedView.getMeasuredWidth() / 2.f;
            if (nextView != null) {
                float scroll2 = width / 2.f - nextView.getMeasuredWidth() / 2.f;
                float scroll = scroll1 + (selectedView.getMeasuredWidth() - scroll2);
                float dx = scroll * positionOffset;
                scrollOffset = (int) (scroll1 - dx);
            } else {
                scrollOffset = (int) scroll1;
            }
        }
        linearLayoutManager.scrollToPositionWithOffset(position, scrollOffset);

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

            View view = getItemView(position);
            if (view != null) {
                onTransitionListener.onTransition(view, position, 1 - positionOffset);
            }
            view = getItemView(position + 1);
            if (view != null) {
                onTransitionListener.onTransition(view, position + 1, positionOffset);
            }
        }
    }

    private int[] prePositions = {-1, -1};

    @Override
    public View getItemView(int item) {
        LinearLayout linearLayout = (LinearLayout) linearLayoutManager.findViewByPosition(item);
        if (linearLayout != null) {
            return linearLayout.getChildAt(0);
        }
        return linearLayout;
    }

    @Override
    public int getCurrentItem() {
        return selectItem;
    }

    @Override
    public int getPreSelectItem() {
        return preSelectItem;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.positionOffsetPixels = positionOffsetPixels;
        this.pageScrollPosition = position;
        this.positionOffset = positionOffset;
        if (scrollBar != null) {
            scrollBar.onPageScrolled(pageScrollPosition, positionOffset, positionOffsetPixels);
        }
        scrollToTab(position, positionOffset);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        pageScrollState = state;
    }

    private int pageScrollState;
    private int pageScrollPosition;

    private class ProxyAdapter extends Adapter<ViewHolder> {
        private IndicatorAdapter adapter;

        public ProxyAdapter(IndicatorAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout linearLayout = new LinearLayout(parent.getContext());
            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            return new ViewHolder(linearLayout) {
            };
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            LinearLayout linearLayout = (LinearLayout) holder.itemView;
            View itemView = linearLayout.getChildAt(0);
            linearLayout.removeAllViews();
            itemView = adapter.getView(position, itemView, linearLayout);
            itemView.setSelected(selectItem == position);
            if (onTransitionListener != null) {
                onTransitionListener.onTransition(itemView, position, selectItem == position ? 1 : 0);
            }
            linearLayout.addView(itemView);
            linearLayout.setTag(position);
            linearLayout.setOnClickListener(onClickListener);
        }

        @Override
        public int getItemCount() {
            return adapter.getCount();
        }

        private OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                setCurrentItem(position, true);
            }
        };
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (scrollBar != null && scrollBar.getGravity() == ScrollBar.Gravity.CENTENT_BACKGROUND) {
            drawSlideBar(canvas);
        }
        super.dispatchDraw(canvas);
        if (scrollBar != null && scrollBar.getGravity() != ScrollBar.Gravity.CENTENT_BACKGROUND) {
            drawSlideBar(canvas);
        }
    }

    private void drawSlideBar(Canvas canvas) {
        if (proxyAdapter == null || scrollBar == null) {
//            inRun.stop();
            return;
        }
        final int count = proxyAdapter.getItemCount();
        if (count == 0) {
//            inRun.stop();
            return;
        }
//        if (getCurrentItem() >= count) {
//            setCurrentItem(count - 1);
////            inRun.stop();
//            return;
//        }
        float offsetX = 0;
        int offsetY = 0;
        int tabWidth;
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
        if (pageScrollState == ViewPager.SCROLL_STATE_IDLE) {
            View view = linearLayoutManager.findViewByPosition(selectItem);
            tabWidth = measureScrollBar(selectItem, 0, true);
            if (view != null) {
                offsetX = view.getLeft();
            } else {
                return;
            }
        } else {
            View view = linearLayoutManager.findViewByPosition(pageScrollPosition);
            tabWidth = measureScrollBar(pageScrollPosition, positionOffset, true);
            if (view != null) {
                offsetX = view.getLeft() + view.getMeasuredWidth() * positionOffset;
            } else {
                return;
            }
        }
        int width = scrollBar.getSlideView().getWidth();
        offsetX += (tabWidth - width) / 2;
        int saveCount = canvas.save();
        canvas.translate(offsetX, offsetY);
        canvas.clipRect(0, 0, width, scrollBar.getSlideView().getHeight()); // needed
        scrollBar.getSlideView().draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private int measureScrollBar(int position, float selectPercent, boolean needChange) {
        if (scrollBar == null)
            return 0;
        View view = scrollBar.getSlideView();
        if (view.isLayoutRequested() || needChange) {
            View selectV = linearLayoutManager.findViewByPosition(position);
            View unSelectV = linearLayoutManager.findViewByPosition(position + 1);
            Log.d("cccc", "position:" + position + "selectPercent:" + selectPercent);
            if (selectV != null) {
                int tabWidth = (int) (selectV.getWidth() * (1 - selectPercent) + (unSelectV == null ? 0 : unSelectV.getWidth() * selectPercent));
                int width = scrollBar.getWidth(tabWidth);
                int height = scrollBar.getHeight(getHeight());
                view.measure(width, height);
                view.layout(0, 0, width, height);

                Log.d("cccc", "width:" + width + " selectV:" + (selectV != null) + " unSelectV:" + (unSelectV != null));
                return tabWidth;
            }
        }
        return scrollBar.getSlideView().getWidth();
    }

    private int selectItem;
    private int preSelectItem;

}
