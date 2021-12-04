package com.geohack.example.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.geohack.GeoHackSDK
import com.geohack.example.R
import com.example.geohack.graph.DrawPlot
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.collections.ArrayList


class MapActivity : AppCompatActivity(), SensorEventListener, LocationListener, OnMapReadyCallback {
    private var stepDetector: Boolean = false

    private lateinit var overlaySensors: DrawPlot
    private lateinit var fabButton: FloatingActionButton
    private lateinit var ll: LinearLayout
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager

    private var googleMap: GoogleMap? = null
    private var latlngList: ArrayList<LatLng>? = null

    private lateinit var geoHackSDK: GeoHackSDK

    private var weeksGPS = 0f
    private var secondsGPS = 0f

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        geoHackSDK = GeoHackSDK(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        if (checkPermissionOrFinish()) {
            return
        }

        secondsGPS = 0f
        weeksGPS = secondsGPS

        fabButton = findViewById(R.id.fab)
        ll = findViewById(R.id.linearLayoutGraph)

        initOverlaySensors()

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            this@MapActivity
        )

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this@MapActivity,
            sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
            SensorManager.SENSOR_DELAY_FASTEST
        )

        sensorManager.registerListener(
            this@MapActivity,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        sensorManager.registerListener(
            this@MapActivity,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_FASTEST
        )

        stepDetector = intent.getBooleanExtra("step_detector", false)
        if (stepDetector) {
            sensorManager.registerListener(
                this@MapActivity,
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                SensorManager.SENSOR_DELAY_FASTEST
            )
        } else {
            sensorManager.registerListener(
                this@MapActivity,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }

        fabButton.setOnClickListener {
            if (!geoHackSDK.isRunning()) {
                geoHackSDK.start()

                fabButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MapActivity,
                        R.drawable.ic_pause_black_24dp
                    )
                )
            } else {
                geoHackSDK.stop()

                fabButton.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@MapActivity,
                        R.drawable.ic_play_arrow_black_24dp
                    )
                )
            }
        }

        ll.setOnClickListener {
            geoHackSDK.lengthOfStep(overlaySensors, ll)
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager.unregisterListener(this)
        locationManager.removeUpdates(this)
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (geoHackSDK.isRunning()) {
            if (checkPermissionOrFinish()) {
                return
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                this@MapActivity
            )

            sensorManager.registerListener(
                this@MapActivity,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST
            )
            sensorManager.registerListener(
                this@MapActivity,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_FASTEST
            )

            if (stepDetector) {
                sensorManager.registerListener(
                    this@MapActivity,
                    sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR),
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            } else {
                sensorManager.registerListener(
                    this@MapActivity,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_FASTEST
                )
            }

            fabButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MapActivity,
                    R.drawable.ic_pause_black_24dp
                )
            )
        } else {
            fabButton.setImageDrawable(
                ContextCompat.getDrawable(
                    this@MapActivity,
                    R.drawable.ic_play_arrow_black_24dp
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        geoHackSDK.onSensorChanged(event, overlaySensors, ll)
    }

    override fun onLocationChanged(location: Location) {
        val GPSTimeSec = location.time / 1000
        weeksGPS = (GPSTimeSec / GPS_SECONDS_PER_WEEK).toFloat()
        secondsGPS = (GPSTimeSec % GPS_SECONDS_PER_WEEK).toFloat()

        if (googleMap != null && latlngList == null) {
            googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        location.latitude,
                        location.longitude
                    ), 18f
                )
            )
        }
        if (latlngList == null) {
            latlngList = ArrayList()
        }

        latlngList?.add(LatLng(location.latitude, location.longitude))

        if (latlngList != null) {
            val options = PolylineOptions().width(5f).color(Color.BLUE).geodesic(true)
            for (z in 0 until latlngList!!.size) {
                val point: LatLng = latlngList!![z]
                options.add(point)
            }

            googleMap?.addPolyline(options)
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0 -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this@MapActivity,
                    "Thank you for providing permission!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this@MapActivity,
                    "Need location permission to create tour.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    companion object {
        private const val GPS_SECONDS_PER_WEEK = 511200L
    }

    override fun onMapReady(gm: GoogleMap) {
        googleMap = gm
    }

    private fun checkPermissionOrFinish(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MapActivity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 0
            )
            finish()
            return true
        }

        return false
    }

    private fun initOverlaySensors() {
        overlaySensors = DrawPlot("Position", "#ff0000")
        overlaySensors.addPoint(0.0, 0.0)

        val graph = overlaySensors.getGraphView(applicationContext)
        ll.addView(graph)
    }
}