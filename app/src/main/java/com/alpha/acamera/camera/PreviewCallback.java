package com.alpha.acamera.camera;

/**
 * 预览回调，通过此接口获取摄像头实时图像帧
 */
public interface PreviewCallback {
    /**
     * 获取图像帧时，调用此方法
     * @param data 摄像头获取的预览帧图像
     * @param width 图像宽度
     * @param height 图像高度
     */
    void onPreviewFrame(byte[] data, int width, int height);
}
