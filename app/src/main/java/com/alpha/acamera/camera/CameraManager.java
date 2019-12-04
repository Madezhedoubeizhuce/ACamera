package com.alpha.acamera.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView;

import java.util.List;

/**
 * 相机管理类
 */
public class CameraManager {
    private static final String TAG = "CameraManager";

    static public int HEAD_CAMERA = 0;//前置摄像
    static public int BACK_CAMERA = 1;//后置摄像

    private ResizeAbleSurfaceView mSurfaceView;
    private View mAdjustView;
    private Camera mCamera;
    private boolean isOpen = false;

    private PreviewCallback mPreviewCallback;
    private SurfaceListener mListener;

    private boolean mClose = false;

    private CameraManager() {
    }

    private static class SingleInstance {
        private static final CameraManager INSTANCE = new CameraManager();
    }

    public static CameraManager getInstance() {
        return SingleInstance.INSTANCE;
    }

    /**
     * 设置相机的大小
     *
     * @param width  相机宽度
     * @param height 相机高度
     */
    public void setCameraSize(int width, int height) {
        CameraParam.width = width;
        CameraParam.height = height;
    }

    /**
     * 打开相机并启动预览
     *
     * @param surfaceView 使用SurfaceView显示摄像头画面
     */
    public void openCamera(int cameraId, ResizeAbleSurfaceView surfaceView, View adjustView) {
        mClose = false;
        mSurfaceView = surfaceView;
        mAdjustView = adjustView;

        getCameraInfo(cameraId);

        if (!openCamera(cameraId)) {
            throw new RuntimeException("failed to open Camera");
        }

        isOpen = true;

        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(mListener = new SurfaceListener(mSurfaceView, mCamera,
                (byte[] data, Camera camera) -> {
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    if (mPreviewCallback != null) {
                        mPreviewCallback.onPreviewFrame(data, size.width, size.height);
                    }
                }));
    }

    /**
     * 设置预览帧监听，获取相机预览帧画面
     *
     * @param callback 预览帧回调，用于获取摄像头的预览帧画面
     */
    public void onPreviewFrame(PreviewCallback callback) {
        mPreviewCallback = callback;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    private boolean openCamera(int cameraType) {
        int index = cameraType;

        Camera c = null;
        try {
            if (index != -1) {
                c = Camera.open(index); // attempt to get a Camera instance
            } else {
                c = Camera.open();
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "failed to open Camera");
            e.printStackTrace();
        }
        mCamera = c;

        return mCamera != null; // returns null if camera is unavailable
    }

    public Camera.CameraInfo getCameraInfo(int id) {
        // 选择前置像头
        int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();

        Camera.getCameraInfo(id, info);

        Log.d(TAG, "getCameraInfo: camera orientation " + info.orientation);
        Log.d(TAG, "getCameraInfo: camera canDisableShutterSound " + info.canDisableShutterSound);
        Log.d(TAG, "getCameraInfo: camera facing " + info.facing);

        return info;
    }

    /**
     * 拍照，获取人脸图片
     *
     * @param callback 拍照回调，用于获取拍的照片
     */
    public void takePicture(PictureCallback callback) {
        if (mCamera == null) {
            if (!openCamera(BACK_CAMERA)) {
                throw new RuntimeException("failed to open Camera");
            }
        }

        mSurfaceView.getHolder().addCallback(new SurfaceListener(mSurfaceView, mCamera, null));

        mCamera.takePicture(null, null, (byte[] data, Camera camera) -> {
            Camera.Size size = camera.getParameters().getPreviewSize();
            callback.onPictureTaken(data, size.width, size.height);
        });
    }

    public boolean isOpen() {
        return isOpen;
    }

    void setCameraSize() {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
        if (supportedSizes != null) {
            Log.d(TAG, "setCameraSize: supportedSizeList size: " + supportedSizes.size());
            for (Camera.Size size : supportedSizes) {
                Log.d(TAG, "setCameraSize: supportedSize: " + size.width + ", " + size.height);
            }
        }

        int cameraWidth = CameraParam.width;
        int cameraHeight = CameraParam.height;
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        parameters.setPreviewSize(cameraWidth, cameraHeight);

        adjustView(cameraWidth, cameraHeight);
        mCamera.setParameters(parameters);
    }

    public void adjustView(int cameraWidth, int cameraHeight) {
        float cameraRatio = cameraWidth * 1.0f / cameraHeight;

        // 设置屏幕方向
        int rotation = ((WindowManager) mSurfaceView.getContext().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getRotation();
        Log.d(TAG, "adjustView: rotation: " + rotation);

        switch (rotation) {
            case Surface.ROTATION_0:
                cameraRatio = cameraHeight * 1.0f / cameraWidth;
                mCamera.setDisplayOrientation(90);
                break;
            case Surface.ROTATION_90:
                cameraRatio = cameraWidth * 1.0f / cameraHeight;
                break;
            case Surface.ROTATION_180:
                cameraRatio = cameraHeight * 1.0f / cameraWidth;
                mCamera.setDisplayOrientation(270);
                break;
            case Surface.ROTATION_270:
                cameraRatio = cameraWidth * 1.0f / cameraHeight;
                mCamera.setDisplayOrientation(180);
                break;
        }

        Log.d(TAG, "adjustSize: cameraRatio " + cameraRatio);
        ViewGroup.LayoutParams params = mAdjustView.getLayoutParams();
        float width = mAdjustView.getWidth();
        float height = mAdjustView.getHeight();
        Log.d(TAG, "adjustSize: before width " + width + ", height " + height);

        float ratio = width / height;
        if (ratio < cameraRatio) {
            height = width / cameraRatio;
        } else {
            width = height * cameraRatio;
        }

        Log.d(TAG, "adjustSize: after width " + width + ", height " + height);

        mSurfaceView.resize((int) width, (int) height);

        params.width = (int) width;
        params.height = (int) height;

        mAdjustView.setLayoutParams(params);

    }

    /**
     * 关闭相机，释放相机资源
     */
    public void closeCamera() {
        if (mCamera != null && !mClose) {
            mSurfaceView.getHolder().removeCallback(mListener);
            mListener = null;
            mClose = true;
            isOpen = false;
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mPreviewCallback = null;

            mSurfaceView = null;
            mAdjustView = null;
        }
    }
}
