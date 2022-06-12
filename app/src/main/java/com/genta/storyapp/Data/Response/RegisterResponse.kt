package com.genta.storyapp.Data.Response

import com.google.gson.annotations.SerializedName
import java.lang.Error

data class RegisterResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
