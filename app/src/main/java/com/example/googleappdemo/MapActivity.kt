package com.example.googleappdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_map.*

const val FINE_LOCATION: String = Manifest.permission.ACCESS_FINE_LOCATION
const val COARSE_LOCATION: String = Manifest.permission.ACCESS_COARSE_LOCATION
const val REQUEST_LOCATION_PERMISSION: Int = 1001

const val TAG_MapActivity: String = "MapActivity"

class MapActivity : AppCompatActivity() {
    private var isPermissionGranted = false
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        getLocationPermission()
    }

    private fun initMap() {
        (map as SupportMapFragment).getMapAsync(object : OnMapReadyCallback {
            @SuppressLint("MissingPermission")
            override fun onMapReady(p0: GoogleMap?) {
                googleMap = p0
                Log.i(TAG_MapActivity, "========== map is ready here =========== ankit")
                Toast.makeText(this@MapActivity, "Map is Ready", Toast.LENGTH_LONG).show()

                if (isPermissionGranted) {
                    getDeviceLocation()

                    googleMap?.isMyLocationEnabled = true 
                }
            }
        })
    }

    private fun getLocationPermission() {
        Log.i(TAG_MapActivity, "========== getting location permision =========== ankit")
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ContextCompat.checkSelfPermission(applicationContext,
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(applicationContext,
                    FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted
                Log.i(TAG_MapActivity,
                    "========== location permision already granted=========== ankit")
                isPermissionGranted = true
                initMap()
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION)
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            if (isPermissionGranted) {
                val location: Task<Location>? = fusedLocationProviderClient.lastLocation
                location?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i(TAG_MapActivity,
                            "========== found current location =========== ankit")
                        val location = it.result
                        showCamera(LatLng(location.latitude, location.longitude), 15f)
                    } else {
                        Log.i(TAG_MapActivity,
                            "========== unable to found current location =========== ankit")
                    }
                }
            }
        } catch (exception: Exception) {
        }

    }

    private fun showCamera(latlong: LatLng, zoom: Float) {
        Log.i(TAG_MapActivity,
            "========== showCamera ========= latitude:${latlong.latitude} ==== longitude:{${latlong.longitude}}")
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latlong, zoom))
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        Log.i(TAG_MapActivity,
            "========== onRequestPermissionsResult location permision =========== ankit")
        when (requestCode) {

            REQUEST_LOCATION_PERMISSION -> if (grantResults.isNotEmpty()) {
                grantResults.forEach {
                    if (it != PackageManager.PERMISSION_GRANTED) {
                        isPermissionGranted = false
                        return
                    }
                }
                isPermissionGranted = true
                initMap()
            }
        }
    }
}