package com.ahao.bannerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView : View {

    var paint = Paint().apply {
        isAntiAlias = true
    }
    var color = 0
        set(value) {
            field = value
            paint.color = color
        }

    var radius = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(measuredWidth / 2.0f, measuredHeight / 2.0f, measuredWidth / 2.0f, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(radius * 2, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(radius * 2, MeasureSpec.EXACTLY))
    }

}