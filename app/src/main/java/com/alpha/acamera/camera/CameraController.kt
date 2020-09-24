package com.alpha.acamera.camera

import com.example.android.camera.utils.AutoFitSurfaceView

interface CameraController {
    /**
     * 打开指定id相机并开启预览
     */
    fun startCamera(cameraId: Int)

    /**
     * 打开指定id相机
     */
    fun openCamera(cameraId: Int)

    /**
     * 打开视频预览
     */
    fun startPreview(surfaceView: AutoFitSurfaceView?)

    /**
     * 停止预览
     */
    fun stopPreview()

    /**
     * 关闭相机并停止预览
     */
    fun closeCamera()

    /**
     * 是否开启相机
     */
    val isOpen: Boolean

    /**
     * 是否正在开启相机
     */
    val isOpening: Boolean

    /**
     * 拍照
     */
    fun takePicture(callback: PictureCallback?)

    /**
     * 相机错误监听
     */
    fun setErrListener(mErrListener: ErrorListener?)

    /**
     * 相机帧回调
     */
    fun onPreviewFrame(callback: PreviewCallback?)
}