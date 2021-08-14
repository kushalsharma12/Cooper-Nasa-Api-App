package com.kushalsharma.nasawalli.Models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
 data class Patent(
    val count: Int,
    val page: Int,
    val perpage: Int,
    val results: List<List<String>>,
    val total: Int
) : Parcelable