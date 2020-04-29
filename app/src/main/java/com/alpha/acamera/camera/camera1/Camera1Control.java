package com.alpha.acamera.camera.camera1;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.alpha.acamera.camera.CameraControl;
import com.alpha.acamera.camera.CameraParam;
import com.alpha.acamera.camera.ErrorListener;
import com.alpha.acamera.camera.PictureCallback;
import com.alpha.acamera.camera.PreviewCallback;
import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView;
import com.alpha.acamera.util.ThreadPoolUtil;

import java.util.concurrent.ExecutorService;

public class Camera1Control extends CameraControl {
    private static final String TAG = "Camera1Control";

    private ResizeAbleSurfaceView mSurfaceView;
    private Camera mCamera;
    private int mCameraId;
    private boolean isOpen = false;
    private boolean isOpening = false;

    private PreviewCallback mPreviewCallback;
    private SurfaceListener mListener;

    private ErrorListener mErrListener;
    private ExecutorService mExec;
    private Handler mHandler;

    private boolean mClose = false;

    public Camera1Control() {
        mHandler = new Handler(Looper.getMainLooper());
        mExec = ThreadPoolUtil.newSingleThreadPool();
    }

    @Override
    public void openCamera(int cameraId) {
        isOpening = true;
        mExec.submit(() -> {
            Log.d(TAG, "openCamera: at " + Thread.currentThread().getName());
            Log.d(TAG, "opening: at " + Thread.currentThread().getName());
            mClose = false;
            Log.d(TAG, "openCamera: start");

            if (!openCameraReal(cameraId)) {
                if (mErrListener != null) {
                    mErrListener.onError(new RuntimeException("failed to open Camera"));
                }
                return;
            }
            isOpen = true;
            isOpening = false;
            mCamera.setErrorCallback((int error, Camera camera) -> {
                if (mErrListener != null) {
                    mErrListener.onError(new Exception("camera happen, error code is " + error));
                }
            });
            Log.d(TAG, "openCamera: success");
        });
    }

    @Override
    public void startPreview(ResizeAbleSurfaceView surfaceView) {
        Log.d(TAG, "startPreview: ");
        if (mCamera != null) {
            setSurfaceListener(surfaceView);
            return;
        }

        mExec.submit(() -> {
            Log.d(TAG, "startPreview: at " + Thread.currentThread().getName());
            if (mCamera == null) {
                throw new RuntimeException("camera not opened");
            }
            Log.d(TAG, "starting: at " + Thread.currentThread().getName());
            mHandler.post(() -> {
                Log.d(TAG, "startPreview: start");
                setSurfaceListener(surfaceView);
                mListener.startPreview();
                Log.d(TAG, "startPreview: success");
            });
        });
    }

    private void setSurfaceListener(ResizeAbleSurfaceView surfaceView) {
        Log.d(TAG, "setSurfaceListener");
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(mListener = new SurfaceListener(mSurfaceView, (byte[] data, Camera camera) -> {
            Camera.Size size = camera.getParameters().getPreviewSize();
            if (mPreviewCallback != null) {
                mPreviewCallback.onPreviewFrame(data, size.width, size.height);
            }
        }));
    }

    @Override
    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public boolean isOpening() {
        return isOpening;
    }

    @Override
    public void takePicture(PictureCallback callback) {
        if (mCamera == null) {
            throw new RuntimeException("camera has not opened");
        }

        mSurfaceView.getHolder().addCallback(new SurfaceListener(mSurfaceView, null));

        mCamera.takePicture(null, null, (byte[] data, Camera camera) -> {
            Camera.Size size = camera.getParameters().getPreviewSize();
            callback.onPictureTaken(data, size.width, size.height);
        });
    }

    @Override
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
        }
    }

    @Override
    public void setErrListener(ErrorListener mErrListener) {
        this.mErrListener = mErrListener;
    }

    @Override
    public void onPreviewFrame(PreviewCallback callback) {
        mPreviewCallback = callback;
    }

    private boolean openCameraReal(int cameraId) {
        int index = cameraId;

        Camera c = null;
        try {
            if (index != -1) {
                c = Camera.open(index); // attempt to get a Camera instance
            } else {
                c = Camera.open();
            }
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "failed to open Camera:", e);
        }
        mCamera = c;

        return mCamera != null; // returns null if camera is unavailable
    }

    private void setCameraSize() {
        Camera.Parameters parameters = mCamera.getParameters();

        int cameraWidth = CameraParam.width;
        int cameraHeight = CameraParam.height;
        parameters.setPreviewSize(cameraWidth, cameraHeight);

        adjustView(cameraWidth, cameraHeight);
        mCamera.setParameters(parameters);
    }

    private void adjustView(int cameraWidth, int cameraHeight) {
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
        float width = mSurfaceView.getWidth();
        float height = mSurfaceView.getHeight();
        Log.d(TAG, "adjustSize: before width " + width + ", height " + height);

        float ratio = width / height;
        if (ratio < cameraRatio) {
            height = width / cameraRatio;
        } else {
            width = height * cameraRatio;
        }

        Log.d(TAG, "adjustSize: after width " + width + ", height " + height);

        mSurfaceView.resize((int) width, (int) height);
    }

    private class SurfaceListener implements SurfaceHolder.Callback {
        private Camera.PreviewCallback mPreviewCallback;
        private SurfaceHolder mHolder;

        SurfaceListener(ResizeAbleSurfaceView surfaceView, Camera.PreviewCallback callback) {
            mPreviewCallback = callback;
            mHolder = surfaceView.getHolder();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

        public void startPreview() {
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            if (mCamera == null) {
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
                e.printStackTrace();
            }

            setCameraSize();

            // start preview with new settings
            try {
                mCamera.setPreviewCallback(mPreviewCallback);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
}
