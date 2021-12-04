package com.geohack.example.activity

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.geohack.example.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            startGraphActivity()
        }

        findViewById<Button>(R.id.btnStartWithDetector).setOnClickListener {
            startGraphActivity(true)
        }
    }

    private fun startGraphActivity(detectorSteps: Boolean = false) {
        val mapIntent = Intent(this, MapActivity::class.java)

        if (detectorSteps) {
            mapIntent.putExtra("step_detector", false)
        }

        startActivity(mapIntent)
    }
}
