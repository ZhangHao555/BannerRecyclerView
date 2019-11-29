package com.ahao.bannerview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.ahao.wanandroid.view.banner.BannerSetting
import kotlinx.android.synthetic.main.view_banner_view.view.*
import kotlin.math.abs

open class BannerView : FrameLayout {

    var setting = BannerSetting()
    var layoutManager: BannerLayoutManager = BannerLayoutManager()
    var snapHelper = BannerPageSnapHelper()
    var adapter: RecyclerView.Adapter<*>? = null
    var indicator: Indicator? = null

    companion object {
        private const val START_SCROLL = 1
        private const val STOP_SCROLL = 2
        private const val CONTINUE_SCROLL = 3
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    private val timerHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message?) {
            if (msg != null) {
                when (msg.what) {
                    START_SCROLL -> {
                        var curPos = layoutManager.getCurrentPosition()
                        banner_recycler_view.smoothScrollToPosition(++curPos)
                        sendEmptyMessageDelayed(CONTINUE_SCROLL, layoutManager.smoothScrollTime.toLong())
                    }
                    CONTINUE_SCROLL -> startAutoSlide()
                    STOP_SCROLL -> removeCallbacksAndMessages(null)
                }
            }
        }
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.view_banner_view, this, true)
    }

    private var actionDownX: Float = 0.toFloat()
    private var actionDownY: Float = 0.toFloat()

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) {
            return super.dispatchTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                requestDisallowInterceptTouchEvent(true)
                stopAutoSlide()
            }
            MotionEvent.ACTION_MOVE -> {
                val moveX = ev.x
                val moveY = ev.y
                if (abs(actionDownX - moveX) > abs(actionDownY - moveY)) {
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }

                if (!setting.slideByTouch) {
                    return false
                }
            }

            MotionEvent.ACTION_UP -> {
                if (setting.autoSlide) {
                    startAutoSlide()
                }
            }
        }

        return super.dispatchTouchEvent(ev)
    }

    fun startAutoSlide() {
        timerHandler.sendEmptyMessageDelayed(START_SCROLL, setting.slideTimeGap)

        banner_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                indicator?.onScrolled(dx,dx.toFloat() / layoutManager.itemWidth)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    indicator?.onViewSelected(layoutManager.getCurrentPosition())
                }
            }

        })
    }

    fun stopAutoSlide() {
        timerHandler.sendEmptyMessage(STOP_SCROLL)
    }

    fun setUp(setting: BannerSetting, adapter: RecyclerView.Adapter<*>) {
        this.setting = setting
        this.adapter = adapter

        layoutManager.apply {
            loop = setting.loop
            smoothScrollTime = setting.autoSlideSpeed

        }

        banner_recycler_view.adapter = adapter
        banner_recycler_view.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(banner_recycler_view)
        snapHelper.loop = setting.loop

        if (setting.autoSlide) {
            startAutoSlide()
        }
    }

}