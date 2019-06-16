package com.ahao.myapplication.banner;

import android.graphics.PointF;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class BannerLayoutManager extends RecyclerView.LayoutManager implements RecyclerView.SmoothScroller.ScrollVectorProvider {

    private final OrientationHelper mOrientationHelper;

    private float heightScale = 0.9f;
    private float widthScale = 0.9f;

    private boolean infinite = true;  //默认无限循环

    private int itemWidth;

    private int smoothScrollTime = 500;

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
        if ( infinite && lastChild != null && getDecoratedRight(lastChild) > mOrientationHelper.getTotalSpace()) {
            layoutLeftItem(recycler);
        }
        scaleItem();
    }

    private void layoutLeftItem(RecyclerView.Recycler recycler) {
        View firstChild = getChildAt(0);
        if (firstChild != null) {
            View viewForPosition = recycler.getViewForPosition(getItemCount() - 1);
            addView(viewForPosition, 0);
            measureChildWithMargins(viewForPosition, 0, 0);
            int top = getItemTop(viewForPosition);
            int left = getDecoratedLeft(firstChild) - itemWidth;
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
        return offsetDx(dx, recycler);
    }

    private int offsetDx(int dx, RecyclerView.Recycler recycler) {
        int realScroll = dx;
        // 向左
        if (dx > 0) {
            realScroll = scrollToLeft(dx, recycler, realScroll);
        }
        // 向右
        if (dx < 0) {
            realScroll = scrollToRight(dx, recycler, realScroll);
        }
        scaleItem();

        return realScroll;
    }

    private void scaleItem() {
        if (heightScale >= 1 || widthScale >= 1) {
            return;
        }

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float itemMiddle = (getDecoratedRight(child) + getDecoratedLeft(child)) / 2.0f;
            float screenMiddle = mOrientationHelper.getTotalSpace() / 2.0f;
            float interval = Math.abs(screenMiddle - itemMiddle) * 1.0f;

            float ratio = 1 - (1 - heightScale) * (interval / itemWidth);
            float ratioWidth = 1 - (1 - widthScale) * (interval / itemWidth);
            child.setScaleX(ratioWidth);
            child.setScaleY(ratio);
        }
    }

    private int scrollToRight(int dx, RecyclerView.Recycler recycler, int realScroll) {
        while (true) {
            View leftChild = getChildAt(0);
            int left = getDecoratedLeft(leftChild);
            if (left + Math.abs(dx) > getPaddingLeft()) {
                int position = getPosition(leftChild);
                if (!infinite && position == 0) {
                    break;
                }

                int addPosition = infinite ? (position - 1 + getItemCount()) % getItemCount() : position - 1;
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

    private int scrollToLeft(int dx, RecyclerView.Recycler recycler, int realScroll) {
        while (true) {
            // 将需要添加的view添加到RecyclerView中
            View rightView = getChildAt(getChildCount() - 1);
            int decoratedRight = getDecoratedRight(rightView);
            if (decoratedRight - dx < mOrientationHelper.getTotalSpace()) {
                int position = getPosition(rightView);
                if (!infinite && position == getItemCount() - 1) {
                    break;
                }

                int addPosition = infinite ? (position + 1) % getItemCount() : position + 1;
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
        if (!infinite && (targetPosition < 0 || targetPosition > getItemCount() - 1)) {
            return;
        }
        if (infinite || getItemCount() > 0) {
            targetPosition = (targetPosition % getItemCount() + getItemCount()) % getItemCount();
        }

        int offset;
        recyclerView.requestFocus();
        int currentPosition = getCurrentPosition();
        if (currentPosition == getItemCount() - 1 && targetPosition == 0 && infinite) {
            offset = itemWidth;
        } else {
            offset = (targetPosition - currentPosition) * itemWidth;
        }

        try {
            Field field = RecyclerView.class.getDeclaredField("mViewFlinger");
            field.setAccessible(true);
            Object obj = field.get(recyclerView);
            Method method = obj.getClass().getDeclaredMethod("smoothScrollBy", Integer.TYPE, Integer.TYPE, Integer.TYPE);
            method.invoke(obj, offset, 0, smoothScrollTime);
        } catch (Exception ignore) {
        }
    }

    private int getTotalHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    private int getItemTop(View item) {
        return (getTotalHeight() - getDecoratedMeasuredHeight(item)) / 2 + getPaddingTop();
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
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