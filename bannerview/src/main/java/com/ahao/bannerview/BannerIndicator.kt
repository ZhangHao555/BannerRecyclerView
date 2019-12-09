package com.ahao.bannerview

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import kotlin.math.max

open class BannerIndicator(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), Indicator {
    private var selectedPosition = 0
    private var selectedOffset = 0
    private val itemMargin: Int
    private val DEFAULT_ITEM_MARGIN = 10f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerIndicator)
        itemMargin = typedArray.getDimension(R.styleable.BannerIndicator_itemMargin, DEFAULT_ITEM_MARGIN).toInt()
        typedArray.recycle()
    }


    var adapter: Adapter? = null
        set(value) {
            field?.viewGroup = this
            field = value
            if (value != null && value.getItemCount() > 0) {
                removeAllViews()
                val itemCount = value.getItemCount()
                for (i in 0 until itemCount) {
                    adapter?.addUnselectedView(this)
                }
                adapter?.addSelectedView(this)
                selectedOffset = 0
                selectedPosition = 0
                requestLayout()
            }

        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxHeight = 0
        var width = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val lp = child.layoutParams
            child.measure(MeasureSpec.makeMeasureSpec(lp.width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(lp.height, MeasureSpec.EXACTLY))
            if (i != childCount - 1) {
                width += child.measuredWidth
            }
            maxHeight = max(child.measuredHeight, maxHeight + paddingTop + paddingBottom)
        }
        setMeasuredDimension(width + (childCount - 1 - 1) * itemMargin + paddingLeft + paddingRight, maxHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount == 0 || adapter == null || adapter!!.getItemCount() + 1 != childCount) {
            return
        }
        layoutBackground()
        layoutSelectedView()
    }


    private fun layoutSelectedView() {
        val childAt = getChildAt(selectedPosition)
        val selectedView = getChildAt(childCount - 1)
        if (childAt == null || selectedView == null) {
            return
        }
        val xOffset = (selectedView.measuredWidth - childAt.measuredWidth) / 2
        val yOffset = (selectedView.measuredHeight - childAt.measuredHeight) / 2

        val left = childAt.left - xOffset
        val top = childAt.top - yOffset
        selectedView.layout(left, top, left + selectedView.measuredWidth, top + selectedView.measuredHeight)
    }

    private fun layoutBackground() {
        if (adapter == null) {
            return
        }
        val childCount = childCount
        var offset = paddingLeft
        for (i in 0 until childCount - 1) {
            val child = getChildAt(i)
            val top = (measuredHeight - child.measuredHeight) / 2 + paddingTop
            child.layout(offset, top, offset + child.measuredWidth, top + child.measuredHeight)
            offset += child.measuredWidth + itemMargin
        }
    }

     abstract class Adapter {
         var viewGroup : BannerIndicator? = null

        abstract fun getItemCount(): Int

        abstract fun addUnselectedView(parent: BannerIndicator)

        abstract fun addSelectedView(parent: BannerIndicator)

        fun notifyDataSetChanged() {
            if(viewGroup == null || getItemCount() <= 0){
                return
            }

            if (getItemCount() > 0) {
                viewGroup?.removeAllViews()
                for (i in 0 until getItemCount()) {
                    addUnselectedView(viewGroup!!)
                }
                addSelectedView(viewGroup!!)
                viewGroup?.selectedOffset = 0
                viewGroup?.selectedPosition = 0
                viewGroup?.requestLayout()
            }
        }
    }

    override fun onViewSelected(position: Int) {
        selectedPosition = position
        selectedOffset = 0
        requestLayout()
    }

    override fun onScrolled(dx: Int, ratio: Float) {

    }

}