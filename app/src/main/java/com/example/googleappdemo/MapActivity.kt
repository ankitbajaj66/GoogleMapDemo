package com.example.googleappdemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
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

    private fun initViews() {
        input_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.action == KeyEvent.ACTION_DOWN || event.action == KeyEvent.KEYCODE_ENTER) {
                geoLocate()
            }

            return@setOnEditorActionListener false
        }
    }

    private fun geoLocate() {
        Log.i(TAG_MapActivity, "geoLocate..........")

        val searchText = input_search.text.toString()
        val geocoder = Geocoder(this)

        var list: List<Address>? = null

        try {
            list = geocoder.getFromLocationName(searchText, 1)

        } catch (exception: Exception) {
            Log.i(TAG_MapActivity, "geoLocate..........exception")
        }

        list?.let { list ->
            if (list.isNotEmpty()) {
                val address = list.get(0)
                Log.i(TAG_MapActivity, "==========location is : ${address.toString()}")
                val latLong = LatLng(address.latitude, address.longitude)
                showCamera(latLong, 15f)
            }
        }
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

                    googleMap?.let {
                        it.isMyLocationEnabled = true
                        it.uiSettings.isMyLocationButtonEnabled = false

                        initViews()
                    }

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
        } catch (securityException: SecurityException) {
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