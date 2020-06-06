package com.alpha.acamera;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.alpha.acamera.camera.CameraControl;
import com.alpha.acamera.camera.CameraInfo;
import com.alpha.acamera.camera.camera1.Camera1Control;
import com.alpha.acamera.databinding.ActivityCameraBinding;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";

    private ActivityCameraBinding mBinding;
    private CameraControl mCameraControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        configureLayout();
        initCamera();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     *
     * @param activity
     */
    public void hideNavigationBar(Activity activity) {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = activity.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = activity.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        mCameraControl.closeCamera();
    }

    @Override
    protected void onResume() {
        hideNavigationBar(this);

        super.onResume();

        if (!mCameraControl.isOpen() && !mCameraControl.isOpening()) {
            initCamera();
        }
    }

    private void configureLayout() {
        mBinding.btnSwitchCamera.setOnClickListener((View view) -> {
            mCameraControl.closeCamera();
            mCameraId = mCameraId == CameraInfo.BACK_CAMERA ?
                    CameraInfo.HEAD_CAMERA : CameraInfo.BACK_CAMERA;
            mCameraControl.openCamera(mCameraId);
            mCameraControl.startPreview(mBinding.svCamera);
        });
    }

    private int mCameraId = CameraInfo.BACK_CAMERA;

    private void initCamera() {
        mCameraControl = new Camera1Control();

        mCameraControl.openCamera(mCameraId);
        mCameraControl.startPreview(mBinding.svCamera);
    }
}
