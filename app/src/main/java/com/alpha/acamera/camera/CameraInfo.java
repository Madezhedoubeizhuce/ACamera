package com.alpha.acamera.camera;

import android.hardware.Camera;

/**
 * 相机信息类
 */
public class CameraInfo {
    private static final String TAG = "CameraManager";

    static public int HEAD_CAMERA = Camera.CameraInfo.CAMERA_FACING_FRONT;//前置摄像
    static public int BACK_CAMERA = Camera.CameraInfo.CAMERA_FACING_BACK;//后置摄像
}
