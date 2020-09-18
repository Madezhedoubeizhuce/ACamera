package com.alpha.acamera.camera.widget

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView

class ResizeAbleSurfaceView : SurfaceView {
    private var mWidth = -1
    private var mHeight = -1

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (-1 == mWidth || -1 == mHeight) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            setMeasuredDimension(mWidth, mHeight)
        }
    }

    fun resize(width: Int, height: Int) {
        mWidth = width
        mHeight = height
        holder.setFixedSize(width, height)
        requestLayout()
        invalidate()
    }
}