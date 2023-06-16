package com.ywauran.sapajari.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ChallengeModel(
    val char: Char,
    var isCorrect: Boolean,
    var isSelected: Boolean
) : Parcelable
