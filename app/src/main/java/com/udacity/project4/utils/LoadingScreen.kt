package com.udacity.project4.utils

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.udacity.project4.R

class LoadingScreen  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var viewBackgroundColor = 0
    private var progressBackgroundColor = 0

    init {

        context.withStyledAttributes(attrs, R.styleable.LoadingView) {
            viewBackgroundColor = getColor(R.styleable.LoadingView_viewBackgroundColor, 0)
            progressBackgroundColor = getColor(R.styleable.LoadingView_progressBackgroundColor, 0)
        }.also {
            
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}