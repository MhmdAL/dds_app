package com.example.dds

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import okhttp3.OkHttpClient

class HttpClient constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: HttpClient? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: HttpClient(context).also {
                    INSTANCE = it
                }
            }
    }

    private val client = OkHttpClient()

    fun get(url: String) : String {
        val req = okhttp3.Request.Builder()
            .url(url)
            .build()

        try {
            val res = client.newCall(req).execute()
            return res.body!!.string()
        } catch (e: Exception) {
            Log.e("ERROR", "some error")
            Log.e("ERROR", e.stackTraceToString())
            e.message?.let { it1 -> Log.e("ERORR", it1) }
        }

        return "nope"
    }
}
