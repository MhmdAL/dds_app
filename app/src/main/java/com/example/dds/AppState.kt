package com.example.dds

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AppState {
    companion object {
        lateinit var activeMission : MutableLiveData<Mission>
    }
}
