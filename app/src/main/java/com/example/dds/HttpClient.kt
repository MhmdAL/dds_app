package com.example.dds

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpClient {
    companion object {
        lateinit var ddsApi : DdsApi

        fun init() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://localhost:3001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            ddsApi = retrofit.create(DdsApi::class.java)
        }
    }
}
