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
        originW = this.width
        originH = this.height

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

            if (exceedW > 0 || exceedH > 0) {
                layout(newLeft, newTop, newRight, newBottom)
            }
        }
    }

    private fun calcAspectSize(aspectRatio: Float) {
        val newWidth: Int
        val newHeight: Int
        val actualRatio = if (this.width > this.height) aspectRatio else 1f / aspectRatio
        if (this.width < this.height * actualRatio) {
            newHeight = this.height
            newWidth = (this.height * actualRatio).roundToInt()
        } else {
            newWidth = this.width
            newHeight = (this.width / actualRatio).roundToInt()
        }

        aspectWidth = newWidth
        aspectHeight = newHeight
    }

    companion object {
        private val TAG = AutoFitSurfaceView::class.java.simpleName
    }
}
