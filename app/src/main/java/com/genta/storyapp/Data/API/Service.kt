package com.genta.storyapp.Data.API

import com.genta.storyapp.Data.Response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Service {
    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email : String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email : String,
        @Field("password") password: String
    ): Call<ResponseLogin>

    @GET("stories")
    fun getStory(
        @Header("Authorization") token: String
    ) : Call<MsgGetStory>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<MsgGetStory>
}