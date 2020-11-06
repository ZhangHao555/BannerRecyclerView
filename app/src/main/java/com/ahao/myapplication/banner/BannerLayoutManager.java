package com.ahao.myapplication.banner;

import android.graphics.PointF;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

public class BannerLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    protected final OrientationHelper mOrientationHelper;

    private float heightScale = 0.9f;
    private float widthScale = 0.9f;

    private boolean loop = true;  //默认无限循环

    protected int itemWidth;

    private int smoothScrollTime = 500;

    private boolean hasLayout;

    public BannerLayoutManager() {
        mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        layoutChildren(recycler, state);
    }

    private void layoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 || state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        if (hasLayout) {
            return;
        }

        detachAndScrapAttachedViews(recycler);

        View scrap = recycler.getViewForPosition(0);
        measureChildWithMargins(scrap, 0, 0);
        itemWidth = getDecoratedMeasuredWidth(scrap);
        int offsetX = (mOrientationHelper.getTotalSpace() - mOrientationHelper.getDecoratedMeasurement(scrap)) / 2;
        for (int i = 0; i < getItemCount(); i++) {
            if (offsetX > mOrientationHelper.getTotalSpace()) {
                break;
            }
            View viewForPosition = recycler.getViewForPosition(i);
            addView(viewForPosition);
            measureChildWithMargins(viewForPosition, 0, 0);
            offsetX += layoutItem(viewForPosition, offsetX);
        }


        View lastChild = getChildAt(getChildCount() - 1);
        // 如果是循环布局，并且最后一个view已超出父布局，则添加最左边的view
        if (loop && lastChild != null && getDecoratedRight(lastChild) > mOrientationHelper.getTotalSpace()) {
            layoutLeftItem(recycler);
        }
        doWithItem();
        hasLayout = true;
    }

    private void layoutLeftItem(RecyclerView.Recycler recycler) {
        View childCenter = getChildAt(getChildCount() - 2);
        if (childCenter != null) {
            View viewForPosition = recycler.getViewForPosition(getItemCount() - 1);
            addView(viewForPosition, 0);
            measureChildWithMargins(viewForPosition, 0, 0);
            int top = getItemTop(viewForPosition);
            int left = getDecoratedLeft(childCenter) - itemWidth;
            int right = left + itemWidth;
            layoutDecoratedWithMargins(viewForPosition, left, top, right, top + getDecoratedMeasuredHeight(viewForPosition));
        }
    }

    private int layoutItem(View viewForPosition, int offsetX) {
        layoutDecoratedWithMargins(viewForPosition, offsetX, getItemTop(viewForPosition), offsetX + itemWidth, getItemTop(viewForPosition) + getDecoratedMeasuredHeight(viewForPosition));
        return itemWidth;
    }

    @Override
    public boolean canScrollHorizontally() {
        return getItemCount() > 1;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return recycler == null ? 0 : offsetDx(dx, recycler);
    }

    private int offsetDx(int dx, RecyclerView.Recycler recycler) {
        int realScroll = dx;
        // 向左
        if (dx > 0) {
            realScroll = scrollToLeft(dx, recycler);
        }
        // 向右
        if (dx < 0) {
            realScroll = scrollToRight(dx, recycler);
        }
        doWithItem();

        return realScroll;
    }

    protected void doWithItem() {
    }

    private int scrollToRight(int dx, RecyclerView.Recycler recycler) {
        int realScroll = dx;
        while (true) {
            View leftChild = getChildAt(0);
            int left = getDecoratedLeft(leftChild);
            if (left + Math.abs(dx) > getPaddingLeft()) {
                int position = getPosition(leftChild);
                if (!loop && position == 0) {
                    break;
                }

                int addPosition = loop ? (position - 1 + getItemCount()) % getItemCount() : position - 1;
                View addView = recycler.getViewForPosition(addPosition);
                addView(addView, 0);
                measureChildWithMargins(addView, 0, 0);
                layoutDecoratedWithMargins(addView, left - getDecoratedMeasuredWidth(addView), getItemTop(addView), left, getItemTop(addView) + getDecoratedMeasuredHeight(addView));

            } else {
                break;
            }
        }

        View firstChild = getChildAt(0);

        int right = getDecoratedRight(firstChild);
        if (getPosition(firstChild) == 0) {
            if (right + Math.abs(dx) > mOrientationHelper.getTotalSpace()) {
                realScroll = -(mOrientationHelper.getTotalSpace() - right);
            }
        }

        offsetChildrenHorizontal(-realScroll);

        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int decoratedLeft = getDecoratedLeft(childAt);
            if (decoratedLeft > mOrientationHelper.getTotalSpace()) {
                removeAndRecycleView(childAt, recycler);
            }
        }
        return realScroll;
    }

    private int scrollToLeft(int dx, RecyclerView.Recycler recycler) {
        int realScroll = dx;
        while (true) {
            // 将需要添加的view添加到RecyclerView中
            View rightView = getChildAt(getChildCount() - 1);
            int decoratedRight = getDecoratedRight(rightView);
            if (decoratedRight - dx < mOrientationHelper.getTotalSpace()) {
                int position = getPosition(rightView);
                if (!loop && position == getItemCount() - 1) {
                    break;
                }

                int addPosition = loop ? (position + 1) % getItemCount() : position + 1;
                View lastViewAdd = recycler.getViewForPosition(addPosition);
                addView(lastViewAdd);
                measureChildWithMargins(lastViewAdd, 0, 0);
                int left = decoratedRight;
                layoutDecoratedWithMargins(lastViewAdd, left, getItemTop(lastViewAdd), left + getDecoratedMeasuredWidth(lastViewAdd), getItemTop(lastViewAdd) + getDecoratedMeasuredHeight(lastViewAdd));
            } else {
                break;
            }
        }

        // 处理滑动
        View lastChild = getChildAt(getChildCount() - 1);
        int left = getDecoratedLeft(lastChild);
        if (getPosition(lastChild) == getItemCount() - 1) {
            // 最后一个view已经到底了，计算实际可以滑动的距离
            if (left - dx < 0) {
                realScroll = left;
            }
        }
        offsetChildrenHorizontal(-realScroll);

        // 回收滑出父布局的view
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int decoratedRight = getDecoratedRight(child);
            if (decoratedRight < 0) {
                removeAndRecycleView(child, recycler);
            }
        }
        return realScroll;
    }

    public int getCurrentPosition() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (getDecoratedLeft(childAt) >= 0 && getDecoratedRight(childAt) <= mOrientationHelper.getTotalSpace()) {
                return getPosition(childAt);
            }
        }
        return -1;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int targetPosition) {
        if (!loop && (targetPosition < 0 || targetPosition > getItemCount() - 1)) {
            return;
        }
        if (loop || getItemCount() > 0) {
            targetPosition = (targetPosition % getItemCount() + getItemCount()) % getItemCount();
        }

        int offset;
        recyclerView.requestFocus();
        int currentPosition = getCurrentPosition();
        if (currentPosition == getItemCount() - 1 && targetPosition == 0 && loop) {
            offset = itemWidth;
        } else {
            offset = (targetPosition - currentPosition) * itemWidth;
        }
        recyclerView.smoothScrollBy(offset, 0, null, smoothScrollTime);
    }

    private int getTotalHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getItemTop(View item) {
        return (getTotalHeight() - getDecoratedMeasuredHeight(item)) / 2 + getPaddingTop();
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }


    @Override
    public PointF computeScrollVectorForPosition(int targetPosition) {
        return null;
    }

    public int getSmoothScrollTime() {
        return smoothScrollTime;
    }

    public void setSmoothScrollTime(int smoothScrollTime) {
        this.smoothScrollTime = smoothScrollTime;
    }

    public float getHeightScale() {
        return heightScale;
    }

    public void setHeightScale(float heightScale) {
        this.heightScale = heightScale;
    }

    public float getWidthScale() {
        return widthScale;
    }

    public void setWidthScale(float widthScale) {
        this.widthScale = widthScale;
    }
}