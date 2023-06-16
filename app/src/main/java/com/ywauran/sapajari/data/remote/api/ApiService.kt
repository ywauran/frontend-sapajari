package com.ywauran.sapajari.data.remote.api

import com.ywauran.sapajari.data.remote.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("category-challenges")
    fun getCategoryChallenges(): Call<List<CategoryChallengeResponse>>

    @GET("numbers")
    fun getNumbers(): Call<List<NumberResponse>>

    @GET("letters")
    fun getLetters(): Call<List<LetterResponse>>

    @GET("/random-challenge/{id}")
    fun getRandomChallenge(@Path("id") id: Int): Call<List<RandomChallengeResponse>>

    @Multipart
    @POST("predict")
    fun predict(@Part image: MultipartBody.Part): Call<PredictResponse>
}