package com.example.ecarrier

import com.google.firebase.auth.FirebaseUser

class UserState {
    companion object {
        lateinit var currentUser : FirebaseUser
        lateinit var userToken : String
        var userId : Int? = null
    }
}
