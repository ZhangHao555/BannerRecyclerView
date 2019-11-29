package com.ahao.bannerview

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class BannerIndicator(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), Indicator {

    var unselectedRect = mutableListOf<Rect>()
    var selectedRect = mutableListOf<Rect>()

    var adapter: Adapter? = null
        set(value) {
            field = value
            if (value != null && value.getItemCount() > 0) {
                removeAllViews()
                val itemCount = value.getItemCount()
                for (i in 0 until itemCount) {
                    addView(value.getUnselectedView(context))
                }
                addView(value.getSelectedView(context))
                selectedOffset = 0
                selectedPosition = 0
                requestLayout()
            }
        }

    var selectedPosition: Int = 0
    var selectedOffset: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (adapter == null || adapter!!.getItemCount() + 1 != childCount) {
            return
        }
        if (unselectedRect.size <= 0) {
            initRects()
        }
        layoutBackground()
        layoutSelectedView()
    }

    private fun initRects() {
        val itemCount = adapter!!.getItemCount()
        val selectedView = getChildAt(itemCount)

        val unSelectedSumWidth = (0 until itemCount).map {
            getChildAt(it).measuredWidth
        }.sum()
        val unSelectedItemGap = ((measuredWidth - unSelectedSumWidth).toFloat() / (itemCount - 1)).toInt()
        val selectedItemGap = ((measuredWidth - selectedView.measuredWidth * itemCount).toFloat() / (itemCount - 1)).toInt()

        var offset = 0
        unselectedRect.clear()
        selectedRect.clear()
        (0 until itemCount).forEach {
            val child = getChildAt(it)
            val left = offset
            val top = (measuredHeight - child.measuredHeight) / 2
            val right = left + child.measuredWidth
            val bottom = top + child.measuredHeight
            unselectedRect.add(Rect(left, top, right, bottom))

            offset += child.measuredWidth + unSelectedItemGap
        }

        offset = 0

        if (itemCount < childCount) {
            (0 until itemCount).forEach { _ ->
                val child = getChildAt(itemCount)
                val left = offset
                val top = (measuredHeight - child.measuredHeight) / 2
                val right = left + child.measuredWidth
                val bottom = top + child.measuredHeight
                selectedRect.add(Rect(left, top, right, bottom))

                offset += child.measuredWidth + selectedItemGap
            }
        }
    }

    private fun layoutSelectedView() {
        if (adapter == null) {
            return
        }
        val rect = selectedRect[selectedPosition]
        val selectedView = getChildAt(adapter!!.getItemCount())
        val left = rect.left + selectedOffset
        selectedView.layout(left, rect.top, rect.left + selectedView.measuredWidth, rect.bottom)

    }

    private fun layoutBackground() {
        (0 until unselectedRect.size).forEach {
            val rect = unselectedRect[it]
            getChildAt(it).layout(rect.left, rect.top, rect.right, rect.bottom)
        }
    }


    abstract class Adapter {
        abstract fun getItemCount(): Int

        abstract fun getUnselectedView(context: Context): View

        abstract fun getSelectedView(context: Context): View
    }

    override fun onViewSelected(position: Int) {
        selectedPosition = position
        selectedOffset = 0
        requestLayout()
    }

    override fun onScrolled(dx: Int, ratio: Float) {

    }

}