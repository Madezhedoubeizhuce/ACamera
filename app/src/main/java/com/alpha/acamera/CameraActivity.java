package com.alpha.acamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.alpha.acamera.camera.CameraManager;
import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "CameraActivity";

    private FrameLayout mFlAdjust;
    private ResizeAbleSurfaceView mSVCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

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

        CameraManager.getInstance().closeCamera();
    }

    @Override
    protected void onResume() {
        hideNavigationBar(this);

        super.onResume();

        if (!CameraManager.getInstance().isOpen()) {
            initCamera();
        }
    }

    private void configureLayout() {
        mSVCamera = findViewById(R.id.sv_camera);
        mFlAdjust = findViewById(R.id.fl_adjust_view);
    }

    private void initCamera() {
        CameraManager.getInstance().openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, mSVCamera, mFlAdjust);
    }
}
