package com.alpha.acamera.camera;

/**
 * 拍照回调，获取照片
 */
public interface PictureCallback {
    /**
     * 拍照完成后调用此方法
     * @param data 摄像头获取的预览帧图像
     * @param width 图像宽度
     * @param height 图像高度
     */
    void onPictureTaken(byte[] data, int width, int height);
}
