package com.example.ecarrier

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HttpClient {
    companion object {
        lateinit var eCarrierApi : ECarrierApi

        fun init() {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.234:3001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            eCarrierApi = retrofit.create(ECarrierApi::class.java)
        }
    }
}
