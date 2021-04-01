package com.example.jokerway.api

import android.util.Log
import com.example.jokerway.api.data.ApiData
import com.example.jokerway.api.data.PushClickApiData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiService {
    fun postData(apiData: ApiData, onResult: (ApiData?) -> Unit) {
        val retrofit = ServiceBuilder.buildService(Api::class.java)
        retrofit.postApiData(apiData).enqueue(
            object : Callback<ApiData> {
                override fun onFailure(call: Call<ApiData>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<ApiData>, response: Response<ApiData>) {
                    val apiDataResponse = response.body()
                    Log.d("ApiService", response.code().toString())
                    onResult(apiDataResponse)
                }
            }
        )
    }

    fun postPushClickData(apiData: PushClickApiData, onResult: (PushClickApiData?) -> Unit) {
        val retrofit = ServiceBuilder.buildService(Api::class.java)
        retrofit.postPushClickApiData(apiData).enqueue(
            object : Callback<PushClickApiData> {
                override fun onFailure(call: Call<PushClickApiData>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(
                    call: Call<PushClickApiData>,
                    response: Response<PushClickApiData>
                ) {
                    val apiDataResponse = response.body()
                    onResult(apiDataResponse)
                }
            }
        )
    }
}