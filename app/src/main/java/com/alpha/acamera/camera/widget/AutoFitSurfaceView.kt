/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alpha.acamera.camera.widget

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import com.alpha.acamera.R
import kotlin.math.roundToInt

/**
 * A [SurfaceView] that can be adjusted to a specified aspect ratio and
 * performs center-crop transformation of input frames.
 */
class AutoFitSurfaceView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : SurfaceView(context, attrs, defStyle) {
    private var aspectWidth = 0
    private var aspectHeight = 0
    private var aspectRatio = 0f
    private var needMove = false
    private var originW = 0
    private var originH = 0

    // 是否铺满view，视频需要全屏显示预览时设为true
    private var fillView = false

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoFitSurfaceView)

            fillView = typedArray.getBoolean(R.styleable.AutoFitSurfaceView_fillView, false)

            typedArray.recycle()
        }
    }

    fun setFillView(fill: Boolean) {
        fillView = fill
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be
     * measured based on the ratio calculated from the parameters.
     *
     * @param width  Camera resolution horizontal size
     * @param height Camera resolution vertical size
     */
    fun setAspectRatio(width: Int, height: Int) {
        require(width > 0 && height > 0) { "Size cannot be negative" }

        aspectRatio = width.toFloat() / height.toFloat()
        calcAspectSize(aspectRatio)

        needMove = true
        holder.setFixedSize(width, height)
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (aspectRatio == 0f) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            originW = measuredWidth
            originH = measuredHeight
        } else {
            setMeasuredDimension(aspectWidth, aspectHeight)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (needMove) {
            needMove = false

            val exceedH = measuredHeight - originH
            val exceedW = measuredWidth - originW

            val newLeft = left - exceedW / 2
            val newRight = right - exceedW / 2
            val newTop = top - exceedH / 2
            val newBottom = bottom - exceedH / 2

            if (exceedW != 0 || exceedH != 0) {
//            Log.d(TAG, "onLayout: $left $top, $right $bottom")
//            Log.d(TAG, "onLayout: $newLeft $newTop, $newRight $newBottom")
                layout(newLeft, newTop, newRight, newBottom)
            }
        }
    }

    private fun calcAspectSize(aspectRatio: Float) {
        val newWidth: Int
        val newHeight: Int
        val actualRatio = if (this.width > this.height) aspectRatio else 1f / aspectRatio

        if (fillView) {
            // 视频铺满view
            if (this.width < this.height * actualRatio) {
                // view宽高比小于视频宽高比时，增加view的宽度以适应视频
                newHeight = this.height
                newWidth = (this.height * actualRatio).roundToInt()
            } else if (this.width > this.height * actualRatio) {
                // view的宽高比大于视频宽高比时，增大view的高度以适应视频
                newWidth = this.width
                newHeight = (this.width / actualRatio).roundToInt()
            } else {
                newWidth = this.width
                newHeight = this.height
            }
        } else {
            // 视频不铺满view
            if (this.width < this.height * actualRatio) {
                // view宽高比小于视频宽高比时，减小view的高度以适应视频
                newWidth = this.width
                newHeight = (this.width / actualRatio).roundToInt()
            } else if (this.width > this.height * actualRatio) {
                // view的宽高比大于视频宽高比时，减小view的宽度以适应视频
                newHeight = this.height
                newWidth = (this.height * actualRatio).roundToInt()
            } else {
                newWidth = this.width
                newHeight = this.height
            }
        }

        aspectWidth = newWidth
        aspectHeight = newHeight
    }

    companion object {
        private val TAG = AutoFitSurfaceView::class.java.simpleName
    }
}
