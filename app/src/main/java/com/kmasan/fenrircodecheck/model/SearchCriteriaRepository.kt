package com.kmasan.fenrircodecheck.model

import android.location.Location

class SearchCriteriaRepository(
    private val gpsLogger: GPSLogger
) {
    // GPS取得開始
    fun startGPS() = gpsLogger.start()

    // 取得停止
    fun stopGPS() = gpsLogger.stop()

    // 位置情報を取得
    fun getLocation(): Location? = gpsLogger.location

    // 最後に確認された位置情報をセットしておく
    fun setLastLocation() = gpsLogger.lastLocation()
}