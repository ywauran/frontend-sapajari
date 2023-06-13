package com.ywauran.sapajari.data.remote.api

import com.ywauran.sapajari.data.remote.response.CategoryChallengeResponse
import com.ywauran.sapajari.data.remote.response.LetterResponse
import com.ywauran.sapajari.data.remote.response.NumberResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("category-challenges")
    fun getCategoryChallenges(): Call<List<CategoryChallengeResponse>>

    @GET("numbers")
    fun getNumbers(): Call<List<NumberResponse>>

    @GET("letters")
    fun getLetters(): Call<List<LetterResponse>>
}