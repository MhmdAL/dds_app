package com.example.ecarrier

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.work.Worker
import androidx.work.WorkerParameters

class MissionStatusUpdaterWorker(val appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {
    companion object {
        lateinit var data : MutableLiveData<Mission>
    }

    override fun doWork(): Result {

        return Result.success()
    }
}
