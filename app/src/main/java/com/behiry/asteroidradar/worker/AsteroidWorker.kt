package com.behiry.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.behiry.asteroidradar.database.getDatabase
import com.behiry.asteroidradar.network.sevenDaysAfter
import com.behiry.asteroidradar.network.today
import com.behiry.asteroidradar.network.tomorrow
import com.behiry.asteroidradar.repository.AsteroidRepository
import retrofit2.HttpException

class AsteroidWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroids(tomorrow(), sevenDaysAfter())
            repository.refreshPictureOfDay()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}