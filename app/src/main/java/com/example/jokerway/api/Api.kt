package com.example.jokerway.api

import com.example.jokerway.api.data.PushClickApiData
import com.example.jokerway.api.data.ApiData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Api {
    @Headers("Content-type: application/json")
    @POST("loguser")
    fun postApiData(@Body apiData: ApiData): Call<ApiData>

    @Headers("Content-type: application/json")
    @POST("logPushClick")
    fun postPushClickApiData(@Body pushClickApiData: PushClickApiData): Call<PushClickApiData>
}
