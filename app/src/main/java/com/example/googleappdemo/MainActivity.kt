package com.example.googleappdemo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_main.*


const val TAG: String = "MainActivity"
const val ERROR_DIALOG_REQUEST: Int = 9001

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isGoogleServicesOk()) {
            btn_map.setOnClickListener {
                val mapActivity = Intent(this, MapActivity::class.java)
                startActivity(mapActivity)
//                Toast.makeText(this, "Everything is working", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Check if google api is available on phone
    private fun isGoogleServicesOk(): Boolean {
        val available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (available == ConnectionResult.SUCCESS) {
            Log.i(TAG, "========== google service is available =========== ankit")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.i(TAG,
                "========== google service an error occured but we can fixed it  =========== ankit")
            val dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this, available, ERROR_DIALOG_REQUEST)
            dialog.show()
        } else {
            Log.i(TAG, "========== google service you cant make request =========== ankit")
        }
        return false
    }


}