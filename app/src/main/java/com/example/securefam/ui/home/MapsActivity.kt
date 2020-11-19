package com.example.securefam.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.example.securefam.R
import com.example.securefam.app.SecureFamApp.Companion.sharedPrefs
import com.example.securefam.util.AppDialog
import com.example.securefam.util.GlobalUtils
import com.example.securefam.worker.DatabaseUpdateWorker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import mumayank.com.airlocationlibrary.AirLocation
import java.io.IOException
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    override fun onMarkerClick(p0: Marker?) = false

    private lateinit var mMap: GoogleMap
    private lateinit var locationUpdater: AirLocation
    private lateinit var mMapLocationUpdater: AirLocation
    private lateinit var databaseUpdateRequest: PeriodicWorkRequest
    private lateinit var requestConstraints: Constraints

    companion object {
        private val TAG = this::class.java.simpleName
        private val DATABASE_UPDATE_WORKER = DatabaseUpdateWorker::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationUpdater = AirLocation(this, object : AirLocation.Callback {
            override fun onSuccess(locations: ArrayList<Location>) {
                placeMarkerOnMap(LatLng(locations.last().latitude, locations.last().longitude))
            }

            override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                Log.wtf(TAG, locationFailedEnum.name)
            }
        }).apply { start() }

        requestConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        databaseUpdateRequest =
            PeriodicWorkRequest.Builder(DatabaseUpdateWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(requestConstraints).build()

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun placeMarkerOnMap(location: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(location).apply {
            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            title("${sharedPrefs?.userName}\n${getAddress(location)}")
        })
        sharedPrefs?.userLat = location.latitude.toString()
        sharedPrefs?.userLat = location.longitude.toString()
    }

    private fun setUpMap() {
        mMapLocationUpdater = AirLocation(this, object : AirLocation.Callback {
            override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                Log.wtf(TAG, locationFailedEnum.name)
            }

            @SuppressLint("MissingPermission")
            override fun onSuccess(locations: ArrayList<Location>) {
                mMap.isMyLocationEnabled = true
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                val currentLatLng = LatLng(locations.last().latitude, locations.last().longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                startDatabaseUpdateWorker()
            }

        })
    }

    private fun startDatabaseUpdateWorker() {
        if (getStateOfWork(DATABASE_UPDATE_WORKER) != WorkInfo.State.ENQUEUED && getStateOfWork(
                DATABASE_UPDATE_WORKER
            ) != WorkInfo.State.RUNNING
        ) {
            WorkManager.getInstance(application).enqueueUniquePeriodicWork(
                DATABASE_UPDATE_WORKER,
                ExistingPeriodicWorkPolicy.KEEP,
                databaseUpdateRequest
            )
            Log.wtf(TAG, ": Server Started !!")
        } else {
            Log.wtf(TAG, ": Server Already Working !!")
        }
    }

    /*
     * Returns the state of the work performed by work manager using unique work identifier .
     * States of Work Manager : STOPPED, RUNNING, ENQUEUED.
     */
    private fun getStateOfWork(workUniqueName: String): WorkInfo.State {
        return try {
            if (WorkManager.getInstance(application)
                    .getWorkInfosForUniqueWork(workUniqueName)
                    .get().size > 0
            ) {
                WorkManager.getInstance(application)
                    .getWorkInfosForUniqueWork(workUniqueName).get()[0].state
            } else {
                WorkInfo.State.CANCELLED
            }
        } catch (e: ExecutionException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        } catch (e: InterruptedException) {
            e.printStackTrace()
            WorkInfo.State.CANCELLED
        }
    }

    private fun getAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        val address: Address
        val addressText: String? = null

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText.plus(
                        if (i == 0) address.getAddressLine(i) else "\n${
                            address.getAddressLine(
                                i
                            )
                        }"
                    )
                }
            }
        } catch (e: IOException) {
            e.localizedMessage?.let {
                Log.e(TAG, it)
            }
        }

        return addressText!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationUpdater.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationUpdater.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onBackPressed() {
        if (sharedPrefs?.userName != null || FirebaseAuth.getInstance().currentUser != null) {
            val confirmDialog = AppDialog.instance(
                getString(R.string.confirm),
                getString(R.string.clear_current_session),
                object : AppDialog.AppDialogListener {
                    override fun onClickConfirm() {
                        GlobalUtils.logout(applicationContext, null)
                        finish()
                    }

                    override fun onClickCancel() {}
                },
                getString(R.string.okay),
                getString(R.string.cancel)
            )
            confirmDialog.show(supportFragmentManager, confirmDialog.tag)
        } else {
            super.onBackPressed()
        }
    }
}
