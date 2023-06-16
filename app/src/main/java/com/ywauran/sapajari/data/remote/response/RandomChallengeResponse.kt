package com.ywauran.sapajari.data.remote.response

import com.google.gson.annotations.SerializedName

data class RandomChallengeResponse(

    @field:SerializedName("createdAt")
    val createdAt: String? = null,

    @field:SerializedName("categoryChallengeId")
    val categoryChallengeId: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("uuid")
    val uuid: String? = null,

    @field:SerializedName("updatedAt")
    val updatedAt: String? = null
)