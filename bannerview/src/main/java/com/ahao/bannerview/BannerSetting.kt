package com.ahao.wanandroid.view.banner

class BannerSetting(var loop : Boolean = false, // 循坏
                    var slideByTouch : Boolean = true, // 是否允许手动滑动
                    var autoSlide : Boolean = true, // 是否允许自动滑动
                    var slideTimeGap : Long = 2000,  // 自动滑动时间间隔
                    var autoSlideSpeed : Int = 500

) // 自动滑动