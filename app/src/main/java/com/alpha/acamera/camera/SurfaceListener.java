package com.alpha.acamera.camera;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView;

import java.io.IOException;

class SurfaceListener implements SurfaceHolder.Callback {
    private static final String TAG = "SurfaceListener";

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;

    SurfaceListener(ResizeAbleSurfaceView surfaceView, Camera camera, Camera.PreviewCallback previewCallback) {
        mHolder = surfaceView.getHolder();
        mCamera = camera;
        mPreviewCallback = previewCallback;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        if (mCamera == null) {
            return;
        }

        try {
            CameraManager manager = CameraManager.getInstance();
            manager.setCameraSize();

            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null || mCamera == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
            e.printStackTrace();
        }

        CameraManager manager = CameraManager.getInstance();
        manager.setCameraSize();

        // start preview with new settings
        try {
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            // Call stopPreview() to stop updating the preview surface.
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
        }
    }
}
