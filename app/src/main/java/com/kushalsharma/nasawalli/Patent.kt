package com.kushalsharma.nasawalli

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Patent(
    val count: Int,
    val page: Int,
    val perpage: Int,
    val results: List<List<String>>,
    val total: Int
) : Parcelable