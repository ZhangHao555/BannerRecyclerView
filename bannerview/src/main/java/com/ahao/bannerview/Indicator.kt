package com.ahao.bannerview

interface Indicator {
    fun onViewSelected(position: Int)
    fun onScrolled(dx: Int,ratio : Float)
}