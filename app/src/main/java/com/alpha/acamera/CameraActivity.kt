package com.alpha.acamera

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.alpha.acamera.camera.CameraControl
import com.alpha.acamera.camera.CameraInfo
import com.alpha.acamera.camera.camera2.Camera2Control
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var mCameraControl: CameraControl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        configureLayout()
        initCamera()
    }

    /**
     * 隐藏虚拟按键，并且全屏
     *
     * @param activity
     */
    private fun hideNavigationBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = activity.window.decorView
            val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            decorView.systemUiVisibility = uiOptions
        }
    }

    override fun onStop() {
        super.onStop()
        mCameraControl.closeCamera()
    }

    override fun onResume() {
        hideNavigationBar(this)
        super.onResume()
        if (!mCameraControl.isOpen && !mCameraControl.isOpening) {
            initCamera()
        }
    }

    private fun configureLayout() {
        btnSwitchCamera.setOnClickListener {
            mCameraControl.closeCamera()
            mCameraId = if (mCameraId == CameraInfo.BACK_CAMERA) CameraInfo.HEAD_CAMERA else CameraInfo.BACK_CAMERA
            mCameraControl.openCamera(mCameraId)
            mCameraControl.startPreview(svCamera)
        }
    }

    private var mCameraId = CameraInfo.BACK_CAMERA

    private fun initCamera() {
        mCameraControl = Camera2Control(this)
        mCameraControl.openCamera(mCameraId)
        mCameraControl.startPreview(svCamera)
    }
}