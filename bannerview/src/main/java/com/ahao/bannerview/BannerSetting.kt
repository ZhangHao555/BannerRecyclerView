package com.ahao.bannerview

class BannerSetting(var loop: Boolean = false, // 循坏
                    var canSlideByTouch: Boolean = true, // 是否允许手动滑动
                    var canAutoSlide: Boolean = true, // 是否允许自动滑动
                    var slideTimeGap: Long = 2000,  // 自动滑动时间间隔
                    var autoSlideSpeed: Int = 500 // 自动滑动一次的时长

)