package com.alpha.acamera.camera;

import android.view.View;

import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView;

public abstract class CameraControl {
    public abstract void openCamera(int cameraId);

    public abstract void startPreview(ResizeAbleSurfaceView surfaceView);

    public abstract boolean isOpen();

    public abstract boolean isOpening();

    public abstract void takePicture(PictureCallback callback);

    public abstract void closeCamera();

    public abstract void setErrListener(ErrorListener mErrListener);

    public abstract void onPreviewFrame(PreviewCallback callback);
}
