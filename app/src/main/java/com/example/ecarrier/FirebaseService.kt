package com.example.ecarrier

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        Log.e("TAG", "Refreshed token: $token")
    }
}