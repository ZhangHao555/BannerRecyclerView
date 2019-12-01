package com.ahao.bannerview

import android.graphics.PointF
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import kotlin.math.abs

open class BannerLayoutManager : RecyclerView.LayoutManager(), RecyclerView.SmoothScroller.ScrollVectorProvider {

    var mOrientationHelper: OrientationHelper = OrientationHelper.createHorizontalHelper(this)
    var itemWidth: Int = 0

    private var hasLayout = false
    var smoothScrollTime = 500
    var loop: Boolean = true

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        recycler!!
        state!!
        layoutChildren(recycler, state)
    }

    private fun layoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount == 0 || state.isPreLayout) {
            removeAndRecycleAllViews(recycler)
            return
        }
        if (hasLayout) {
            return
        }
        detachAndScrapAttachedViews(recycler)

        val scrap = recycler.getViewForPosition(0)
        measureChildWithMargins(scrap, 0, 0)
        itemWidth = getDecoratedMeasuredWidth(scrap)
        var offsetX = (mOrientationHelper.totalSpace - mOrientationHelper.getDecoratedMeasurement(scrap)) / 2

        for (i in 0 until itemCount) {
            if (offsetX > mOrientationHelper.totalSpace) {
                break
            }
            val viewForPosition = recycler.getViewForPosition(i)
            addView(viewForPosition)
            measureChildWithMargins(viewForPosition, 0, 0)
            offsetX += layoutItem(viewForPosition, offsetX)
        }
        if (itemCount >= 3 && loop) {
            layoutLeftItem(recycler)
        }
        doWithItem()

        hasLayout = true
    }

    private fun layoutItem(viewForPosition: View, offsetX: Int): Int {
        layoutDecoratedWithMargins(viewForPosition, offsetX, getItemTop(viewForPosition), offsetX + itemWidth, getItemTop(viewForPosition) + getDecoratedMeasuredHeight(viewForPosition))
        return itemWidth
    }

    private fun layoutLeftItem(recycler: RecyclerView.Recycler) {
        val childCenter = getChildAt(childCount - 2)
        if (childCenter != null) {
            val viewForPosition = recycler.getViewForPosition(itemCount - 1)
            addView(viewForPosition, 0)
            measureChildWithMargins(viewForPosition, 0, 0)
            val top = getItemTop(viewForPosition)
            val left = getDecoratedLeft(childCenter) - itemWidth
            val right = left + itemWidth
            layoutDecoratedWithMargins(viewForPosition, left, top, right, top + getDecoratedMeasuredHeight(viewForPosition))
        }
    }

    override fun canScrollHorizontally(): Boolean {
        return itemCount > 1
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {
        return if (recycler == null) 0 else offsetDx(dx, recycler)
    }

    private fun offsetDx(dx: Int, recycler: RecyclerView.Recycler): Int {
        var realScroll = dx
        // 向左
        if (dx > 0) {
            realScroll = scrollToLeft(dx, recycler)
        }
        // 向右
        if (dx < 0) {
            realScroll = scrollToRight(dx, recycler)
        }
        doWithItem()

        return realScroll
    }

    open fun doWithItem() {}

    private fun scrollToRight(dx: Int, recycler: RecyclerView.Recycler): Int {
        var realScroll = dx
        while (true) {
            val leftChild = getChildAt(0)
            val left = getDecoratedLeft(leftChild!!)
            if (left + abs(dx) > paddingLeft) {
                val position = getPosition(leftChild)
                if (!loop && position == 0) {
                    break
                }

                val addPosition = if (loop) (position - 1 + itemCount) % itemCount else position - 1
                val addView = recycler.getViewForPosition(addPosition)
                addView(addView, 0)
                measureChildWithMargins(addView, 0, 0)
                layoutDecoratedWithMargins(addView, left - getDecoratedMeasuredWidth(addView), getItemTop(addView), left, getItemTop(addView) + getDecoratedMeasuredHeight(addView))
            } else {
                break
            }
        }

        val firstChild = getChildAt(0)
        val right = getDecoratedRight(firstChild!!)
        if (getPosition(firstChild) == 0) {
            if (right + abs(dx) > mOrientationHelper.totalSpace) {
                realScroll = -(mOrientationHelper.totalSpace - right)
            }
        }

        offsetChildrenHorizontal(-realScroll)

        (0..childCount).mapNotNull {
            getChildAt(it)
        }.forEach {
            if (getDecoratedLeft(it) > mOrientationHelper.totalSpace) {
                removeAndRecycleView(it, recycler)
            }
        }
        return realScroll
    }

    private fun scrollToLeft(dx: Int, recycler: RecyclerView.Recycler): Int {
        var realScroll = dx
        while (true) {
            val rightView = getChildAt(childCount - 1)
            val decoratedRight = getDecoratedRight(rightView!!)
            if (decoratedRight - dx < mOrientationHelper.totalSpace) {
                val position = getPosition(rightView)
                if (!loop && position == itemCount - 1) {
                    break
                }

                val addPosition = if (loop) (position + 1) % itemCount else position + 1
                val lastViewAdd = recycler.getViewForPosition(addPosition)
                addView(lastViewAdd)
                measureChildWithMargins(lastViewAdd, 0, 0)
                layoutDecoratedWithMargins(lastViewAdd, decoratedRight, getItemTop(lastViewAdd), decoratedRight + getDecoratedMeasuredWidth(lastViewAdd), getItemTop(lastViewAdd) + getDecoratedMeasuredHeight(lastViewAdd))
            } else {
                break
            }
        }

        val lastChild = getChildAt(childCount - 1)
        val left = getDecoratedLeft(lastChild!!)
        if (getPosition(lastChild) == itemCount - 1) {
            if (left - dx < 0) {
                realScroll = left
            }
        }
        offsetChildrenHorizontal(-realScroll)

        (0..childCount).mapNotNull {
            getChildAt(it)
        }.forEach {
            val decoratedRight = getDecoratedRight(it)
            if (decoratedRight < 0) {
                removeAndRecycleView(it, recycler)
            }
        }
        return realScroll
    }

    fun getCurrentPosition(): Int {
        for (i in 0 until childCount) {
            val childAt = getChildAt(i) ?: continue
            if (getDecoratedLeft(childAt) >= 0 && getDecoratedRight(childAt) <= mOrientationHelper.totalSpace) {
                return getPosition(childAt)
            }
        }
        return -1
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        var targetPosition = position
        if (!loop && (targetPosition < 0 || targetPosition > itemCount - 1)) {
            return
        }
        if (loop || itemCount > 0) {
            targetPosition = (targetPosition % itemCount + itemCount) % itemCount
        }

        recyclerView!!.requestFocus()
        val currentPosition = getCurrentPosition()
        val offset = if (currentPosition == itemCount - 1 && targetPosition == 0 && loop) {
            itemWidth
        } else {
            (targetPosition - currentPosition) * itemWidth
        }

        recyclerView.smoothScrollBy(offset, 0, null, smoothScrollTime)

    }

    private fun getTotalHeight(): Int {
        return height - paddingTop - paddingBottom
    }

    private fun getItemTop(item: View): Int {
        return (getTotalHeight() - getDecoratedMeasuredHeight(item)) / 2 + paddingTop
    }

    override fun computeScrollVectorForPosition(target: Int): PointF? = null
}