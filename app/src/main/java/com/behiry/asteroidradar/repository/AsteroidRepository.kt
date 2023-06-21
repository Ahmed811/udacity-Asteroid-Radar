package com.behiry.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.behiry.asteroidradar.Constants.TAG
import com.behiry.asteroidradar.database.AsteroidDatabase
import com.behiry.asteroidradar.database.asDatabaseModel
import com.behiry.asteroidradar.database.asDomainModel
import com.behiry.asteroidradar.model.Asteroid
import com.behiry.asteroidradar.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao().getAsteroidsOfToday(tomorrow(), sevenDaysAfter())
        ) { it.asDomainModel() }

    val pictureOfDay: LiveData<PictureOfDay> =
        Transformations.map(
            database.pictureOfDayDao().getPictureOfDay()
        ) { it?.asDomainModel() }

    val todayOnlyAsteroids: LiveData<List<Asteroid>> =
        Transformations.map(
            database.asteroidDao().getAsteroidsOfToday(today(), today())
        ) { it.asDomainModel() }


    suspend fun refreshAsteroids(startDate: String = tomorrow(), endDate: String = sevenDaysAfter()) {
        var asteroidList: ArrayList<Asteroid>

        withContext(Dispatchers.IO) {
            try {
                val response =
                    Network.asteroid.getAllAsteroidsAsync(startDate, endDate)
                val jsonObject = JSONObject(response)
                asteroidList = parseAsteroidsJsonResult(jsonObject)
                database.asteroidDao().deletePreviousDay(today())
                database.asteroidDao().insertAll(*asteroidList.asDatabaseModel())
            } catch (e: Exception) {
                Log.d(TAG, "Error: ${e.localizedMessage}")
            }
        }
    }
    suspend fun getAsteroidsForToday(startDate: String = today(), endDate: String = today()) {
        var asteroidList: ArrayList<Asteroid>

        withContext(Dispatchers.IO) {
            try {
                val response =
                    Network.asteroid.getAllAsteroidsAsync(startDate, endDate)
                val jsonObject = JSONObject(response)
                asteroidList = parseAsteroidsJsonResult(jsonObject)
                database.asteroidDao().deletePreviousDay(today())
                database.asteroidDao().insertAll(*asteroidList.asDatabaseModel())
            } catch (e: Exception) {
                Log.d(TAG, "Error: ${e.localizedMessage}")
            }
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val pictureOfDay = Network.asteroid.getPictureOfDayAsync().await()
                if (pictureOfDay.mediaType == "image") {
                    database.pictureOfDayDao().insertImage(pictureOfDay.asDatabaseModel())
                } else return@withContext
            } catch (e: Exception) {
                Log.d(TAG, "error: ${e.localizedMessage}")
            }
        }
    }

}