package com.behiry.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.behiry.asteroidradar.Constants.TAG
import com.behiry.asteroidradar.database.getDatabase
import com.behiry.asteroidradar.model.Asteroid
import com.behiry.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val repository = AsteroidRepository(database)

    private val _detailedAsteroid = MutableLiveData<Asteroid?>()
    val detailedAsteroid: LiveData<Asteroid?> get() = _detailedAsteroid

    init {
        viewModelScope.launch {
            try {
                repository.refreshPictureOfDay()
                repository.refreshAsteroids()
                repository.getAsteroidsForToday()
            } catch (e: Exception) {
                Log.d(TAG, "e:${e.message}")
            }
        }
    }

    var allAsteroids = repository.asteroids
    var pictureOfDay = repository.pictureOfDay
    var todayOnlyAsteroids = repository.todayOnlyAsteroids  ////////////////
//    var todayOnlyAsteroids = viewModelScope.launch {
//      try {
//          repository.refreshPictureOfDay()
//          repository.refreshAsteroids()
//          repository.getAsteroidsForToday()
//      } catch (e: Exception) {
//          Log.d(TAG, "e:${e.message}")
//      }
//  }  ////////////////


    fun onAsteroidClicked(asteroid: Asteroid) {
        _detailedAsteroid.value = asteroid
    }

    fun doneNavigating() {
        _detailedAsteroid.value = null
    }

}
