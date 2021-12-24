package com.example.dds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    fun updateLocation() = lifecycleScope.launch(Dispatchers.IO) {
        val res = async {
            getMissionData()
        }.await()

        launch (Dispatchers.Main){
            val currentLatTV = findViewById<TextView>(R.id.current_lat)
            val currentLngTV = findViewById<TextView>(R.id.current_lng)

            val jsonObject = JSONObject(res)

            var currentLat = jsonObject.getDouble("current_lat")
            var currentLng = jsonObject.getDouble("current_lng")

            currentLatTV.text = currentLat.toString()
            currentLngTV.text = currentLng.toString()
        }
    }

    fun getMissionData() : String {
        val client = OkHttpClient()
        val req = okhttp3.Request.Builder()
            .url("http://localhost:3001/mission")
            .build()

        try {
            val res = client.newCall(req).execute()
            return res.body!!.string()
        } catch (e: Exception) {
            Log.e("ERRORR", "some error")
            Log.e("ERRORR", e.stackTraceToString())
            e.message?.let { it1 -> Log.e("ERRORR", it1) }
        }

        return "nope"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val update_btn = findViewById<Button>(R.id.update_btn)
        update_btn.setOnClickListener {
            updateLocation()
        }

    }
}