package com.ywauran.sapajari.ui.challenge

import ApiConfig
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.ywauran.sapajari.data.remote.response.PredictResponse
import com.ywauran.sapajari.databinding.ActivityChallengeBinding
import com.ywauran.sapajari.model.ChallengeModel
import com.ywauran.sapajari.ui.adapter.ChallengeAdapter
import com.ywauran.sapajari.utils.tmpFile
import com.ywauran.sapajari.utils.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChallengeBinding
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imgCapture: ImageCapture? = null
    private lateinit var challengeQuestion: ArrayList<ChallengeModel>
    private var quizState = 0

    private val adapter by lazy { ChallengeAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        val view = binding.root
        supportActionBar?.hide()
        setContentView(view)
        getPermission()
        val getData = intent.getParcelableArrayListExtra<ChallengeModel>(KEY_CHALLANGE)
        if (getData != null) {
            challengeQuestion = getData
            adapter.setItems(challengeQuestion)
        }
        binding.rvQuestion.adapter = adapter
        binding.buttonCapture.setOnClickListener {
            captureImage()
        }
        binding.cardBack.setOnClickListener {
            finish()
        }
        openCamera()
    }


    private fun captureImage() {
        val toCapture = imgCapture ?: return
        val file = tmpFile(this@ChallengeActivity)
        val outputOption = ImageCapture.OutputFileOptions.Builder(file).build()

        toCapture.takePicture(outputOption,
            ContextCompat.getMainExecutor(this@ChallengeActivity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    binding.buttonCapture.isEnabled = false

                    //bisa handle disini kalo camera gagal capture mau diapain
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    //logic loading
                    binding.buttonCapture.visibility = View.INVISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                    output.savedUri?.let {
                        val file = it.uriToFile(this@ChallengeActivity)
                        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        val imageMultipart: MultipartBody.Part = requestImageFile.let { it1 ->
                            MultipartBody.Part.createFormData(
                                "image",
                                file.name,
                                it1
                            )
                        }
                        //call api
                        val service = ApiConfig.getPredictApiService()
                        val predict = service.predict(imageMultipart)
                        predict.enqueue(object : Callback<PredictResponse> {
                            override fun onResponse(
                                call: Call<PredictResponse>,
                                response: Response<PredictResponse>
                            ) {
                                binding.buttonCapture.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                if (response.isSuccessful) {
                                    val res = response.body()
                                    if (this@ChallengeActivity::challengeQuestion.isInitialized && res != null) {
                                        if (res.status) {
                                            val currentQuestion =
                                                challengeQuestion[quizState].char.toString().lowercase()
                                            val isAnswerCorrect =
                                                (res.data?.lowercase() ?: "") == currentQuestion

                                            if (isAnswerCorrect) {
                                                if (quizState < challengeQuestion.size-1) {
                                                    // Handle ke soal berikutnya
                                                    challengeQuestion[quizState].isSelected = false
                                                    challengeQuestion[quizState].isCorrect = true
                                                    if(challengeQuestion.size-1 > quizState){
                                                        challengeQuestion[quizState+1].isSelected = true
                                                    }
                                                    adapter.setItems(challengeQuestion)
                                                    quizState++
                                                    Toast.makeText(this@ChallengeActivity, "Benar!!! ^_^", Toast.LENGTH_SHORT)
                                                        .show()
                                                } else {
                                                    // Handle kalo semua elemen quiz beres
                                                    Toast.makeText(
                                                        this@ChallengeActivity,
                                                        "Selamat! Kamu telah menyelesaikan challenge",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    finish()
                                                }
                                            } else {
                                                // Handle kalo jari kedetek tapi ga sesuai dengan quiz state sekarang
                                                Toast.makeText(
                                                    this@ChallengeActivity,
                                                    "Oops! Bahasa isyarat salah, itu adalah ${res.data}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            // Handle kalo jari tidak kedeteksi sama sekali
                                            Toast.makeText(
                                                this@ChallengeActivity,
                                                "Oops, tidak ada jari yang terdeteksi!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                } else {
                                    // Handle kalo network call tidak berhasil
                                    Toast.makeText(
                                        this@ChallengeActivity,
                                        "Error!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                                binding.buttonCapture.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                                //handle kalo network call gak sucess
                                Toast.makeText(
                                    this@ChallengeActivity,
                                    "Network Error!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                    }
                }
            }
        )
    }

    private fun openCamera() {
        val futureProvider = ProcessCameraProvider.getInstance(this)
        futureProvider.addListener({
            imgCapture = ImageCapture.Builder().build()

            val provider = futureProvider.get()
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build().apply {
                    setSurfaceProvider(binding.cameraView.surfaceProvider)
                }

            try {
                provider.apply {
                    binding.buttonCapture.visibility = View.VISIBLE
                    unbindAll()
                    bindToLifecycle(this@ChallengeActivity, cameraSelector, preview, imgCapture)
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
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

    companion object {

        const val KEY_CHALLANGE = "challengeList"
        fun start(ctx: Context, data: List<ChallengeModel>) {
            val i = Intent(ctx, ChallengeActivity::class.java)
            i.putParcelableArrayListExtra(KEY_CHALLANGE, ArrayList(data))
            ctx.startActivity(i)
        }
    }
}
