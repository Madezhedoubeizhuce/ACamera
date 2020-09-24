package com.alpha.acamera.camera.camera2

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.alpha.acamera.camera.*
import com.alpha.acamera.camera.widget.ResizeAbleSurfaceView
import java.util.*
import java.util.concurrent.locks.ReentrantLock

class Camera2Control(private val context: Context) : CameraControl {
    companion object {
        private const val TAG = "Camera2Control"
    }

    override var isOpen = false
        private set
    override var isOpening = false
        private set

    private var mImageReader: ImageReader? = null
    private val previewCallback: PreviewCallback? = null
    private var mBackgroundHandler: Handler? = null
    private var mSensorOrientation: Int? = null
    private var cameraId = "0"

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mBackgroundThread: HandlerThread? = null
    private var mCameraDevice: CameraDevice? = null
    private var mResizeAbleSurfaceView: ResizeAbleSurfaceView? = null
    private var surfaceCreated = false
    private var needCreateCaptureSession = false

    /**
     * [CaptureRequest.Builder] for the camera preview
     */
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

    private val mDeviceStateCallback: CameraDevice.StateCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : CameraDevice.StateCallback() {
        override fun onOpened(@NonNull cameraDevice: CameraDevice) {
            Log.i(TAG, "onOpened: ")
            mCameraDevice = cameraDevice
            // This method is called when the camera is opened.  We start camera preview here.
            if (surfaceCreated) {
                createCameraPreviewSession()
            } else {
                needCreateCaptureSession = true
            }
        }

        override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
            Log.i(TAG, "onDisconnected: ")
        }

        override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
            Log.i(TAG, "onError: ")
        }
    }

    private var mCaptureSession: CameraCaptureSession? = null
    private val mCaptureStateCallback: CameraCaptureSession.StateCallback = @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
            Log.i(TAG, "onConfigured: ")
            // The camera is already closed
            if (null == mCameraDevice) {
                return
            }

            // When the session is ready, we start displaying the preview.
            mCaptureSession = cameraCaptureSession

            try {
                mResizeAbleSurfaceView?.let {
                    mCaptureSession?.setRepeatingRequest(mPreviewRequestBuilder!!.build(),
                            object : CaptureCallback() {}, mBackgroundHandler)
                }
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onConfigureFailed(
                @NonNull cameraCaptureSession: CameraCaptureSession) {
            Log.e(TAG, "onConfigureFailed: $cameraCaptureSession")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun openCamera(cameraId: Int) {
        this.cameraId = cameraId.toString()

        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        startBackgroundThread()
        configureCamera(cameraManager, this.cameraId)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        cameraManager.openCamera(cameraId.toString(), mDeviceStateCallback, mBackgroundHandler)
    }

    private fun startBackgroundThread() {
        mBackgroundThread = HandlerThread("CameraBackground")
        mBackgroundThread?.apply {
            start()
            mBackgroundHandler = Handler(looper)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun configureCamera(cameraManager: CameraManager, cameraId: String) {
        val characteristics: CameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
        mImageReader = ImageReader.newInstance(CameraParam.width, CameraParam.height,
                ImageFormat.YUV_420_888, 2)
        mImageReader?.setOnImageAvailableListener(OnImageAvailableListenerImpl(), mBackgroundHandler)

        mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)

        // 设置屏幕方向
        val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.rotation

//        Log.d(TAG, "adjustView: rotation: $rotation")
//        when (rotation) {
//            Surface.ROTATION_0 -> {
//                characteristics.
//                mCamera?.setDisplayOrientation(90)
//            }
//            Surface.ROTATION_180 -> {
//                mCamera?.setDisplayOrientation(270)
//            }
//            Surface.ROTATION_270 -> {
//                mCamera?.setDisplayOrientation(180)
//            }
//        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createCameraPreviewSession() {
        try {
            adjustSize(CameraParam.width, CameraParam.height)
            val surface: Surface = mResizeAbleSurfaceView!!.holder.surface

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            //设置自动曝光帧率范围
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, getRange())
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)
            mPreviewRequestBuilder?.addTarget(surface)
            mPreviewRequestBuilder?.addTarget(mImageReader!!.surface)

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice!!.createCaptureSession(Arrays.asList(surface, mImageReader!!.surface),
                    mCaptureStateCallback, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getRange(): Range<Int>? {
        val mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        var chars: CameraCharacteristics? = null
        try {
            chars = mCameraManager.getCameraCharacteristics(cameraId)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        val ranges = chars!!.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)!!
        var result: Range<Int>? = null
        for (range in ranges) {
            Log.d(TAG, "getRange: [" + range.lower + ", " + range.upper + "]")
            //帧率不能太低，大于10
//            if (range.getLower() < 10)
//                continue;
            if (result == null) result = range else if (range.lower <= 15 && range.upper - range.lower > result.upper - result.lower) result = range
        }
        return result
    }

    private fun adjustSize(cameraWidth: Int, cameraHeight: Int) {
        var cameraRatio = cameraWidth * 1.0f / cameraHeight

        // 设置屏幕方向
        val rotation = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay.rotation
        Log.d(TAG, "adjustView: rotation: $rotation")
        when (rotation) {
            Surface.ROTATION_0 -> {
                cameraRatio = cameraHeight * 1.0f / cameraWidth
            }
            Surface.ROTATION_90 -> cameraRatio = cameraWidth * 1.0f / cameraHeight
            Surface.ROTATION_180 -> {
                cameraRatio = cameraHeight * 1.0f / cameraWidth
            }
            Surface.ROTATION_270 -> {
                cameraRatio = cameraWidth * 1.0f / cameraHeight
            }
        }

        Log.d(TAG, "adjustSize: cameraRatio $cameraRatio")
        var width = mResizeAbleSurfaceView!!.width.toFloat()
        var height = mResizeAbleSurfaceView!!.height.toFloat()
        Log.d(TAG, "adjustSize: before width $width, height $height")
        val ratio = width / height
        if (ratio < cameraRatio) {
            height = width / cameraRatio
        } else {
            width = height * cameraRatio
        }
        Log.d(TAG, "adjustSize: after width $width, height $height")
        mResizeAbleSurfaceView!!.resize(width.toInt(), height.toInt())
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun startPreview(surfaceView: ResizeAbleSurfaceView?) {
        mResizeAbleSurfaceView = surfaceView

        mResizeAbleSurfaceView?.holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        mResizeAbleSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
//                TODO("Not yet implemented")
                surfaceCreated = true
                if (needCreateCaptureSession) {
                    createCameraPreviewSession()
                }
            }

        })

        try {
            mCaptureSession?.setRepeatingRequest(mPreviewRequestBuilder!!.build(),
                    object : CaptureCallback() {}, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            Log.e(TAG, "startPreview: ", e)
        }
    }

    override fun takePicture(callback: PictureCallback?) {
//        TODO("Not yet implemented")
    }

    override fun closeCamera() {
//        TODO("Not yet implemented")
        mCameraDevice?.close()
    }

    override fun setErrListener(mErrListener: ErrorListener?) {
//        TODO("Not yet implemented")
    }

    override fun onPreviewFrame(callback: PreviewCallback?) {
//        TODO("Not yet implemented")
    }

    private inner class OnImageAvailableListenerImpl : OnImageAvailableListener {
        private var y: ByteArray? = null
        private lateinit var u: ByteArray
        private lateinit var v: ByteArray
        private val lock = ReentrantLock()

        override fun onImageAvailable(reader: ImageReader) {
            val image = reader.acquireNextImage()
            // Y:U:V == 4:2:2
            if (previewCallback != null && image.format == ImageFormat.YUV_420_888) {
                val planes = image.planes
                // 加锁确保y、u、v来源于同一个Image
                lock.lock()
                // 重复使用同一批byte数组，减少gc频率
                if (y == null) {
                    y = ByteArray(planes[0].buffer.limit() - planes[0].buffer.position())
                    u = ByteArray(planes[1].buffer.limit() - planes[1].buffer.position())
                    v = ByteArray(planes[2].buffer.limit() - planes[2].buffer.position())
                }
                if (image.planes[0].buffer.remaining() == y!!.size) {
                    planes[0].buffer[y]
                    planes[1].buffer[u]
                    planes[2].buffer[v]
                }
                val uvSize = y!!.size + 1 shr 1
                val halfUVSize = uvSize + 1 shr 1
                val nv21 = ByteArray(y!!.size + uvSize)
                val uvPixelStride = planes[1].pixelStride

//                byte[] i420 = new byte[y.length + uvSize];
//                System.arraycopy(y, 0, i420, 0, y.length);

                System.arraycopy(y!!, 0, nv21, 0, y!!.size)
                var nv21Index = y!!.size
                //                int i420Index = y.length;
                var i = 0
                while (i < u.size) {
                    nv21[nv21Index++] = v[i]
                    nv21[nv21Index++] = u[i]
                    i += uvPixelStride
                }

//                try {
//                    ImageUtil.writeImageFile(nv21, "/sdcard/test",
//                            "img_" + image.getWidth() + "_" + image.getHeight() + ".nv21");
//                    ImageUtil.writeImageFile(i420, "/sdcard/test",
//                            "img_" + image.getWidth() + "_" + image.getHeight() + ".i420");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                previewCallback.onPreviewFrame(nv21, image.width, image.height)
                lock.unlock()
            }
            image.close()
        }
    }
}