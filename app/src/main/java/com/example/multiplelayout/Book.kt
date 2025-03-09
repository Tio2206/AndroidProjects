package com.example.multiplelayout

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: Int = 0,
    val name: String,
    val nickname: String,
    val email: String,
    val address: String,
    val phone: String,
    val birthdate: String,
    val imagePath: String?
) : Parcelable

