package com.alpha.acamera.camera

import android.hardware.Camera

/**
 * 相机信息类
 */
object CameraInfo {
    private const val TAG = "CameraManager"
    var HEAD_CAMERA = Camera.CameraInfo.CAMERA_FACING_FRONT //前置摄像
    var BACK_CAMERA = Camera.CameraInfo.CAMERA_FACING_BACK //后置摄像
}