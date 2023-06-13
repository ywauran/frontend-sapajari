package com.ywauran.sapajari.ui.challenge

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.ywauran.sapajari.R
import com.ywauran.sapajari.databinding.ActivityChallengeBinding
import com.ywauran.sapajari.ml.SsdMobilenetV11Metadata1
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding
    private lateinit var labels: List<String>
    private val colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    )
    private val paint = Paint()
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var bitmap: Bitmap
    private lateinit var cameraDevice: CameraDevice
    private lateinit var handler: Handler
    private lateinit var cameraManager: CameraManager
    private lateinit var model: SsdMobilenetV11Metadata1
    private lateinit var outputTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getPermission()

        labels = FileUtil.loadLabels(this, "labels.txt")
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = SsdMobilenetV11Metadata1.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        outputTextView = findViewById(R.id.tv_output)

        binding.tvTexture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }

            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = binding.tvTexture.bitmap!!
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)

                val outputs = model.process(image)
                val locations = outputs.locationsAsTensorBuffer.floatArray
                val classes = outputs.classesAsTensorBuffer.floatArray
                val scores = outputs.scoresAsTensorBuffer.floatArray
                val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                val h = mutable.height
                val w = mutable.width
                paint.textSize = h / 15f
                paint.strokeWidth = h / 85f
                var x = 0
                scores.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if (fl > 0.5) {
                        paint.setColor(colors.get(index))
                        paint.style = Paint.Style.STROKE
                        canvas.drawRect(
                            RectF(
                                locations.get(x + 1) * w,
                                locations.get(x) * h,
                                locations.get(x + 3) * w,
                                locations.get(x + 2) * h
                            ), paint
                        )
                        paint.style = Paint.Style.FILL
                        canvas.drawText(
                            labels.get(classes.get(index).toInt()) + " " + fl.toString(),
                            locations.get(x + 1) * w,
                            locations.get(x) * h,
                            paint
                        )
                    }
                }

                binding.ivPreview.setImageBitmap(mutable)
                val outputText = buildOutputText(classes, scores)
                outputTextView.text = outputText
            }
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    @SuppressLint("MissingPermission")
    private fun openCamera() {
        val cameraId = getFrontCameraId()
        cameraManager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                val surfaceTexture = binding.tvTexture.surfaceTexture
                val surface = Surface(surfaceTexture)

                val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }

                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }
        }, handler)
    }

    private fun getFrontCameraId(): String {
        val cameraIds = cameraManager.cameraIdList
        for (cameraId in cameraIds) {
            val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
            val lensFacing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)
            if (lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                return cameraId
            }
        }
        throw RuntimeException("Front camera not found")
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission()
        }
    }

    private fun buildOutputText(classes: FloatArray, scores: FloatArray): String {
        val outputText = StringBuilder()
        for (index in classes.indices) {
            val confidence = scores[index]
            if (confidence > 0.5) {
                val className = labels[classes[index].toInt()]
                val detectionInfo = "$className: ${confidence.toString()}\n"
                outputText.append(detectionInfo)
            }
        }
        return outputText.toString()
    }
}
