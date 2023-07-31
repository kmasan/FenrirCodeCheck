package com.kmasan.fenrircodecheck.model

import android.location.Location

class SearchCriteriaRepository(
    private val gpsLogger: GPSLogger
) {
    fun startGPS() = gpsLogger.start()
    fun stopGPS() = gpsLogger.stop()
    fun getLocation(): Location? = gpsLogger.location
    fun setLastLocation() = gpsLogger.lastLocation()
}