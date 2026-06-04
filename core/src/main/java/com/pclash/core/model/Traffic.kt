package com.pclash.core.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class Traffic constructor(val upload: Long, val download: Long) : Parcelable