package com.alpha.camera.camera.camera2

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
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.alpha.camera.camera.*
import com.alpha.camera.camera.widget.AutoFitSurfaceView
import kotlinx.coroutines.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class Camera2Controller(private val context: Context) : CameraController {
    companion object {
        private const val TAG = "Camera2Control"
    }

    override var isOpen = false
        private set
    override var isOpening = false
        private set

    private var width = CameraParam.width
    private var height = CameraParam.height
    private var targetFpsRange: Range<Int>? = null

    private val cameraManager: CameraManager by lazy {
        val appContext = context.applicationContext
        appContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }
    private var mImageReader: ImageReader? = null
    private val previewCallback: PreviewCallback? = null
    private var mSensorOrientation: Int? = null
    private var cameraId = "0"

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private var mCameraThread = HandlerThread("CameraThread").apply { start() }
    private var mCameraHandler = Handler(mCameraThread.looper)
    private var coroutineScope = CoroutineScope(Job() + Dispatchers.Main)

    private var mCameraDevice: CameraDevice? = null
    private var mSurfaceView: AutoFitSurfaceView? = null

    private var surfaceCreated = false
    private var needCreateCaptureSession = false

    /**
     * [CaptureRequest.Builder] for the camera preview
     */
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null
    private var mCaptureSession: CameraCaptureSession? = null
    override fun startCamera(cameraId: Int) {
//        TODO("Not yet implemented")
    }

    override fun openCamera(cameraId: Int) {
        this.cameraId = cameraId.toString()
        isOpening = true
        coroutineScope.launch {
            try {// config camera
                readParams(cameraManager)
                configureImageReader()

                mCameraDevice = openCamera(cameraManager, cameraId.toString(), mCameraHandler)
                isOpening = false
                isOpen = true

                if (surfaceCreated) {
                    setSurfaceRatio(width, height)
                    mCaptureSession = createCaptureSession()
                    mSurfaceView?.let {
                        mCaptureSession?.setRepeatingRequest(mPreviewRequestBuilder!!.build(),
                                object : CaptureCallback() {}, mCameraHandler)
                    }
                } else {
                    needCreateCaptureSession = true
                }
            } catch (e: Exception) {
                Log.e(TAG, "openCamera: ", e)
                isOpen = false
            }
        }
    }

    override fun startPreview(surfaceView: AutoFitSurfaceView?) {
        mSurfaceView = surfaceView

        mSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                surfaceCreated = true
                if (needCreateCaptureSession) {
                    coroutineScope.launch {
                        setSurfaceRatio(width, height)
                        mCaptureSession = createCaptureSession()
                        mSurfaceView?.let {
                            mCaptureSession?.setRepeatingRequest(mPreviewRequestBuilder!!.build(),
                                object : CaptureCallback() {}, mCameraHandler)
                        }
                    }
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {

            }

        })
    }

    override fun stopPreview() {
//        TODO("Not yet implemented")
    }

    override fun takePicture(callback: PictureCallback?) {
//        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun closeCamera() {
        width = CameraParam.width
        height = CameraParam.height
        mCameraDevice?.close()
        isOpen = false
        mCaptureSession = null
        mCameraDevice = null
    }

    override fun setErrListener(mErrListener: ErrorListener?) {
//        TODO("Not yet implemented")
    }

    override fun onPreviewFrame(callback: PreviewCallback?) {
//        TODO("Not yet implemented")
    }

    private fun readParams(cameraManager: CameraManager) {
        val characteristics = cameraManager.getCameraCharacteristics(this.cameraId)
        mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)

        Log.d(TAG, "readParams: SENSOR_ORIENTATION $mSensorOrientation")

        val streamConfigurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val sizeArr = streamConfigurationMap?.getOutputSizes(ImageFormat.YUV_420_888)
        var maxW = width
        var maxH = height
        for (size in sizeArr ?: emptyArray()) {
//            Log.d(TAG, "getRange: size $size")
            if (size.width > maxW && size.height > maxH) {
                maxW = size.width
                maxH = size.height
            }
        }
        width = maxW
        height = maxH
        Log.d(TAG, "readParams MaxSize: $width x $height")

        val ranges = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES)!!
        for (range in ranges) {
//            Log.d(TAG, "getRange: [" + range.lower + ", " + range.upper + "]")
            //帧率不能太低，大于10
//            if (range.getLower() < 10)
//                continue;
            if (targetFpsRange == null)
                targetFpsRange = range
            else if (range.lower <= 15
                    && range.upper - range.lower > targetFpsRange!!.upper - targetFpsRange!!.lower)
                targetFpsRange = range
        }
    }

    private fun configureImageReader() {
        mImageReader = ImageReader.newInstance(width, height,
                ImageFormat.YUV_420_888, 2)
        mImageReader?.setOnImageAvailableListener(OnImageAvailableListenerImpl(), mCameraHandler)
    }

    private suspend fun openCamera(
            cameraManager: CameraManager,
            cameraID: String,
            handler: Handler): CameraDevice? = suspendCancellableCoroutine { cont ->
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cont.resumeWithException(RuntimeException("need camera permission"))
            return@suspendCancellableCoroutine
        }

        cameraManager.openCamera(cameraID, object : CameraDevice.StateCallback() {
            override fun onOpened(@NonNull cameraDevice: CameraDevice) {
                cont.resume(cameraDevice)
            }

            override fun onDisconnected(@NonNull cameraDevice: CameraDevice) {
                Log.d(TAG, "onDisconnected: $cameraDevice")
//                cont.resumeWithException(RuntimeException("camera disconnected"))
            }

            override fun onError(@NonNull cameraDevice: CameraDevice, error: Int) {
                cont.resumeWithException(RuntimeException("open camera error $error"))
            }
        }, handler)
    }

    private suspend fun setSurfaceRatio(width: Int, height: Int) = suspendCancellableCoroutine<Unit> { cont ->
        mSurfaceView?.setAspectRatio(width, height)
        // To ensure that size is set, initialize camera preview
        mSurfaceView?.post {
            cont.resume(Unit)
        }
    }

    private suspend fun createCaptureSession(): CameraCaptureSession = suspendCancellableCoroutine { cont ->
        try {
            val surface: Surface = mSurfaceView!!.holder.surface

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            //设置自动曝光帧率范围
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, targetFpsRange)
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF)
            mPreviewRequestBuilder?.addTarget(surface)
            mPreviewRequestBuilder?.addTarget(mImageReader!!.surface)

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice!!.createCaptureSession(listOf(surface, mImageReader!!.surface),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(@NonNull cameraCaptureSession: CameraCaptureSession) {
                            cont.resume(cameraCaptureSession)
                        }

                        override fun onConfigureFailed(
                                @NonNull cameraCaptureSession: CameraCaptureSession) {
                            cont.resumeWithException(RuntimeException("configure capture session failed!"))
                        }
                    }, mCameraHandler)
        } catch (e: CameraAccessException) {
            cont.resumeWithException(e)
        }
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