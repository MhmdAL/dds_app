package com.example.dds

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MissionStatusUpdaterWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    companion object {
        lateinit var data : MutableLiveData<Mission>
    }

    override fun doWork(): Result {

        val mission = HttpClient.ddsApi.getActiveMission()?.execute()?.body()

        return Result.success()
    }
}
