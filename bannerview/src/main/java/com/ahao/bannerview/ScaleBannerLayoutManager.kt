package com.ahao.bannerview

import kotlin.math.abs

class ScaleBannerLayoutManager : BannerLayoutManager() {

    private var heightScale: Float = 0.9f
    private var widthScale: Float = 0.9f

    override fun doWithItem() {
        if (heightScale >= 1 || widthScale >= 1) {
            return
        }

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val itemMiddle = (getDecoratedRight(child!!) + getDecoratedLeft(child)) / 2.0f
            val screenMiddle = mOrientationHelper.totalSpace / 2.0f
            val interval = abs(screenMiddle - itemMiddle) * 1.0f
            if (interval - 0f < 0.0001) {
                continue
            }
            val ratio = 1 - (1 - heightScale) * (interval / itemWidth)
            val ratioWidth = 1 - (1 - widthScale) * (interval / itemWidth)
            child.scaleX = ratioWidth
            child.scaleY = ratio
        }
    }

}