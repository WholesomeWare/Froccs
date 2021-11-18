package com.csakitheone.froccs.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GlassholderView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    var radius = 300f
    var color = Color.GREEN
    var paint = Paint().apply {
        this.color = Color.GREEN
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawCircle(measuredWidth / 2f, measuredHeight / 2f, radius, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = radius.toInt() + paddingLeft + paddingRight
        val desiredHeight = radius.toInt() + paddingTop + paddingBottom
        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec))
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    fun updateColor(newColor: Int) {
        color = newColor
        paint = Paint().apply {
            color = newColor
        }
        invalidate()
    }
}