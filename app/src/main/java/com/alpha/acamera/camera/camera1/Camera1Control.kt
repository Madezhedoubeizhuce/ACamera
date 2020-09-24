package com.alpha.acamera.camera.camera1

import android.content.Context
import android.hardware.Camera
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.alpha.acamera.camera.*
import com.alpha.acamera.util.ThreadPoolUtil
import com.example.android.camera.utils.AutoFitSurfaceView
import java.util.concurrent.ExecutorService

class Camera1Control : CameraControl {
    private var mSurfaceView: AutoFitSurfaceView? = null
    private var mCamera: Camera? = null
    private val mCameraId = 0
    override var isOpen = false
        private set
    override var isOpening = false
        private set
    private var mPreviewCallback: PreviewCallback? = null
    private var mListener: SurfaceListener? = null
    private var mErrListener: ErrorListener? = null
    private val mExec: ExecutorService = ThreadPoolUtil.newSingleThreadPool()
    private val mHandler: Handler = Handler(Looper.getMainLooper())
    private var mClose = false

    override fun openCamera(cameraId: Int) {
        isOpening = true
        mExec.submit {
            mClose = false

            Log.d(TAG, "openCamera: at " + Thread.currentThread().name)
            Log.d(TAG, "opening: at " + Thread.currentThread().name)
            Log.d(TAG, "openCamera: start")

            if (!openCameraReal(cameraId)) {
                mErrListener?.onError(RuntimeException("failed to open Camera"))
                return@submit
            }
            isOpen = true
            isOpening = false
            mCamera?.setErrorCallback { error: Int, camera: Camera? ->
                mErrListener?.onError(Exception("camera happen, error code is $error"))
            }

            Log.d(TAG, "openCamera: success")
        }
    }

    override fun startPreview(surfaceView: AutoFitSurfaceView?) {
        Log.d(TAG, "startPreview: ")

        if (mCamera != null) {
            setSurfaceListener(surfaceView)
            return
        }

        mExec.submit {
            Log.d(TAG, "startPreview: at " + Thread.currentThread().name)

            if (mCamera == null) {
                throw RuntimeException("camera not opened")
            }

            Log.d(TAG, "starting: at " + Thread.currentThread().name)

            mHandler.post {
                Log.d(TAG, "startPreview: start")

                setSurfaceListener(surfaceView)
                mListener?.startPreview()

                Log.d(TAG, "startPreview: success")
            }
        }
    }

    private fun setSurfaceListener(surfaceView: AutoFitSurfaceView?) {
        Log.d(TAG, "setSurfaceListener")

        mSurfaceView = surfaceView
        mSurfaceView?.holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mSurfaceView?.holder?.addCallback(SurfaceListener(mSurfaceView, Camera.PreviewCallback { data: ByteArray?, camera: Camera ->
            val size = camera.parameters.previewSize
            mPreviewCallback?.onPreviewFrame(data, size.width, size.height)
        }).also { mListener = it })
    }

    override fun takePicture(callback: PictureCallback?) {
        if (mCamera == null) {
            throw RuntimeException("camera has not opened")
        }

        mSurfaceView?.holder?.addCallback(SurfaceListener(mSurfaceView, null))
        mCamera?.takePicture(null, null, Camera.PictureCallback { data: ByteArray?, camera: Camera ->
            val size = camera.parameters.previewSize
            callback?.onPictureTaken(data, size.width, size.height)
        })
    }

    override fun closeCamera() {
        if (mCamera != null && !mClose) {
            mSurfaceView!!.holder.removeCallback(mListener)
            mListener = null
            mClose = true
            isOpen = false
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
            mPreviewCallback = null
            mSurfaceView = null
        }
    }

    override fun setErrListener(mErrListener: ErrorListener?) {
        this.mErrListener = mErrListener
    }

    override fun onPreviewFrame(callback: PreviewCallback?) {
        mPreviewCallback = callback
    }

    private fun openCameraReal(cameraId: Int): Boolean {
        var c: Camera? = null
        try {
            c = if (cameraId != -1) {
                Camera.open(cameraId) // attempt to get a Camera instance
            } else {
                Camera.open()
            }
        } catch (e: Exception) {
            // Camera is not available (in use or does not exist)
            Log.e(TAG, "failed to open Camera:", e)
        }
        mCamera = c
        return mCamera != null // returns null if camera is unavailable
    }

    private fun setCameraSize() {
        val parameters = mCamera!!.parameters
        val cameraWidth = CameraParam.width
        val cameraHeight = CameraParam.height
        parameters.setPreviewSize(cameraWidth, cameraHeight)
        adjustView(cameraWidth, cameraHeight)
        mCamera?.parameters = parameters
    }

    private fun adjustView(cameraWidth: Int, cameraHeight: Int) {
        // 设置屏幕方向
        val rotation = (mSurfaceView?.context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.rotation
        Log.d(TAG, "adjustView: rotation: $rotation")
        when (rotation) {
            Surface.ROTATION_0 -> {
                mCamera?.setDisplayOrientation(90)
            }
            Surface.ROTATION_90 -> {
            }
            Surface.ROTATION_180 -> {
                mCamera?.setDisplayOrientation(270)
            }
            Surface.ROTATION_270 -> {
                mCamera?.setDisplayOrientation(180)
            }
        }
        mSurfaceView?.setAspectRatio(cameraWidth, cameraHeight)
    }

    private inner class SurfaceListener internal constructor(surfaceView: AutoFitSurfaceView?, private val mPreviewCallback: Camera.PreviewCallback?) : SurfaceHolder.Callback {
        private val mHolder: SurfaceHolder = surfaceView!!.holder
        override fun surfaceCreated(holder: SurfaceHolder) {}
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            startPreview()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
        fun startPreview() {
            if (mHolder.surface == null) {
                // preview surface does not exist
                return
            }
            if (mCamera == null) {
                return
            }

            // stop preview before making changes
            try {
                mCamera!!.stopPreview()
            } catch (e: Exception) {
                // ignore: tried to stop a non-existent preview
                e.printStackTrace()
            }
            setCameraSize()

            // start preview with new settings
            try {
                mCamera!!.setPreviewCallback(mPreviewCallback)
                mCamera!!.setPreviewDisplay(mHolder)
                mCamera!!.startPreview()
            } catch (e: Exception) {
                Log.d(TAG, "Error starting camera preview: " + e.message)
            }
        }
    }

    companion object {
        private const val TAG = "Camera1Control"
    }

}