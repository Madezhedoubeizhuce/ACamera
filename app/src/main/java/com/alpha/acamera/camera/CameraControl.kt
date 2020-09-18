package com.alpha.acamera.camera

import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView

interface CameraControl {
    fun openCamera(cameraId: Int)
    fun startPreview(surfaceView: ResizeAbleSurfaceView?)
    val isOpen: Boolean
    val isOpening: Boolean
    fun takePicture(callback: PictureCallback?)
    fun closeCamera()
    fun setErrListener(mErrListener: ErrorListener?)
    fun onPreviewFrame(callback: PreviewCallback?)
}