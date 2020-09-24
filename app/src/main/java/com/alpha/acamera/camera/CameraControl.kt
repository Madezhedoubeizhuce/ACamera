package com.alpha.acamera.camera

import com.example.android.camera.utils.AutoFitSurfaceView

interface CameraControl {
    fun openCamera(cameraId: Int)
    fun startPreview(surfaceView: AutoFitSurfaceView?)
    val isOpen: Boolean
    val isOpening: Boolean
    fun takePicture(callback: PictureCallback?)
    fun closeCamera()
    fun setErrListener(mErrListener: ErrorListener?)
    fun onPreviewFrame(callback: PreviewCallback?)
}