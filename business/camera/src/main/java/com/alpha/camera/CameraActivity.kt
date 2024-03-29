package com.alpha.camera

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.alpha.camera.camera.CameraController
import com.alpha.camera.camera.CameraInfo
import com.alpha.camera.camera.camera1.Camera1Controller
import com.alpha.camera.camera.camera2.Camera2Controller
import com.alpha.camera.camera.widget.AutoFitSurfaceView
import kotlinx.android.synthetic.main.activity_camera.*

class CameraActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CameraActivity"
    }

    private lateinit var mCameraController: CameraController

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
        mCameraController.closeCamera()
    }

    override fun onResume() {
        super.onResume()
        if (!mCameraController.isOpen && !mCameraController.isOpening) {
            initCamera()
        }
    }

    private fun configureLayout() {
        btnSwitchCamera.setOnClickListener {
            mCameraController.closeCamera()
            mCameraId = if (mCameraId == CameraInfo.BACK_CAMERA) CameraInfo.HEAD_CAMERA else CameraInfo.BACK_CAMERA
            mCameraController.openCamera(mCameraId)
            mCameraController.startPreview(svCamera as AutoFitSurfaceView)
        }
        btnSwitchCamera.post {
            val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val point = Point()
            wm.defaultDisplay.getSize(point)
            Log.d(TAG, "configureLayout: ${point.x} x ${point.y}")
        }
    }

    private var mCameraId = CameraInfo.BACK_CAMERA

    private fun initCamera() {
        mCameraController = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Camera2Controller(this)
        } else {
            Camera1Controller()
        }
        mCameraController.openCamera(mCameraId)
        mCameraController.startPreview(svCamera as AutoFitSurfaceView)
    }
}