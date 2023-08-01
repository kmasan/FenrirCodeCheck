package com.kmasan.fenrircodecheck.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

// GPSから現在地を取得
class GPSLogger(private val activity: Activity): LocationListener {
    private val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    // 最後に取得できた情報
    var location: Location? = null
    var lat: Double = 0.0
    var log: Double = 0.0

    // GPSの取得開始
    fun start(){
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                100L,
                100F,
                this
            )
        }
    }

    // 取得停止
    fun stop(){
        locationManager.removeUpdates(this)
    }

    // 最後に確認された位置情報を取得
    fun lastLocation() {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(
                "LocationSensor",
                "disable permission: ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION"
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            location = it
        }
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        lat = location.latitude
        log = location.longitude
    }
}