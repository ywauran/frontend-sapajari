package com.ywauran.sapajari.data.remote.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: String? = null
)