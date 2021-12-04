package com.example.geohack

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.widget.LinearLayout
import com.example.geohack.graph.DrawPlot
import com.example.geohack.utils.*

class GeoHackSDK {

    private var gDeltaOrientation: GyroscopeDeltaOrientation
    private var gEulerOrientation: GyroscopeEulerOrientation

    private var stepCounter: DynamicStepCounter

    private var gBias: FloatArray

    private var magneticBias: FloatArray

    private lateinit var currentGravity: FloatArray

    private lateinit var currentMagnetic: FloatArray

    private var usingDefaultCounter = false
    private var areFilesCreated = false
    private var strideLength = 0f
    private var gHeading = 0f
    private var mHeading = 0f

    private var startTime: Long = 0
    private var firstRun = false
    private var initialHeading = 0f

    private var isRunning = false

    private var collector: Collector

    private val context: Context

    constructor(context: Context) {
        this.context = context

        firstRun = true
        strideLength = 0f
        mHeading = 0f
        gHeading = mHeading
        initialHeading = gHeading
        startTime = 0

        areFilesCreated = false
        usingDefaultCounter = areFilesCreated

        strideLength = 2.5f

        gBias = FloatArray(3)
        magneticBias = FloatArray(3)

        stepCounter = DynamicStepCounter(1.0)

        gDeltaOrientation = GyroscopeDeltaOrientation(0.0025f, gBias)
        gEulerOrientation = GyroscopeEulerOrientation(Functions.IDENTITY_MATRIX)

        collector = Collector("http")
    }

    fun isRunning(): Boolean = isRunning

    fun start() {
        isRunning = true

        initialHeading = MagneticFieldOrientation.getHeading(currentGravity, currentMagnetic, magneticBias)
    }

    fun stop() {
        firstRun = true
        isRunning = false
    }

    fun lengthOfStep(overlaySensors: DrawPlot, ll: LinearLayout) {
        val compHeading =
            Functions.calcCompHeading(mHeading.toDouble(), gHeading.toDouble())

        var oPointX = overlaySensors.lastYPoint
        var oPointY = -overlaySensors.lastXPoint

        oPointX += Functions.getXFromPolar(strideLength.toDouble(), compHeading.toDouble())
        oPointY += Functions.getYFromPolar(strideLength.toDouble(), compHeading.toDouble())

        val rPointX = -oPointY
        val rPointY = oPointX
        overlaySensors.addPoint(rPointX.toDouble(), rPointY.toDouble())
        ll.removeAllViews()
        ll.addView(overlaySensors.getGraphView(context))
    }

    fun onSensorChanged(event: SensorEvent, overlaySensors: DrawPlot, ll: LinearLayout) {
        if (firstRun) {
            startTime = event.timestamp
            firstRun = false
        }

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            currentGravity = event.values

        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) {
            currentMagnetic = event.values
        }

        if (isRunning) {
            if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                val dataValues = Functions.arrayToList(event.values)
                dataValues.add(0, (event.timestamp - startTime).toFloat())

                collector.collectToFile("Gravity", dataValues)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.type ==
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED
            ) {
                mHeading = MagneticFieldOrientation.getHeading(currentGravity, currentMagnetic, magneticBias)

                val dataValues = Functions.createList(
                    event.values[0], event.values[1], event.values[2],
                    magneticBias[0], magneticBias[1], magneticBias[2]
                )
                dataValues.add(0, (event.timestamp - startTime).toFloat())
                dataValues.add(mHeading)

                collector.collectToFile("Magnetic", dataValues)
            } else if (event.sensor.type == Sensor.TYPE_GYROSCOPE ||
                event.sensor.type == Sensor.TYPE_GYROSCOPE_UNCALIBRATED
            ) {
                val deltaOrientation =
                    gDeltaOrientation.calcDeltaOrientation(event.timestamp, event.values)
                gHeading = gEulerOrientation.getHeading(deltaOrientation)
                gHeading += initialHeading

                val dataValues = Functions.createList(
                    event.values[0], event.values[1], event.values[2],
                    gBias[0], gBias[1], gBias[2]
                )
                dataValues.add(0, (event.timestamp - startTime).toFloat())
                dataValues.add(gHeading)

                collector.collectToFile("Gyroscope", dataValues)
            } else if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
                val norm = Functions.calcNorm(
                    (event.values[0] + event.values[1] + event.values[2]).toDouble()
                )

                val stepFound = stepCounter.findStep(norm.toDouble())
                if (stepFound) {
                    val dataValues = Functions.arrayToList(event.values)
                    dataValues.add(0, (event.timestamp - startTime).toFloat())
                    dataValues.add(1f)

                    collector.collectToFile("Linear", dataValues)

                    var oPointX = overlaySensors.lastYPoint
                    var oPointY = -overlaySensors.lastXPoint

                    oPointX += Functions.getXFromPolar(
                        strideLength.toDouble(),
                        gHeading.toDouble()
                    )
                    oPointY += Functions.getYFromPolar(
                        strideLength.toDouble(),
                        gHeading.toDouble()
                    )

                    val rPointX = -oPointY
                    val rPointY = oPointX
                    overlaySensors.addPoint(rPointX.toDouble(), rPointY.toDouble())

                    collector.collectToFile(
                        "Set",
                        (event.timestamp - startTime).toFloat(),
                        strideLength,
                        mHeading,
                        gHeading,
                        oPointX,
                        oPointY,
                        rPointX,
                        rPointY
                    )

                    ll.removeAllViews()
                    ll.addView(overlaySensors.getGraphView(context))
                } else {
                    val dataValues = Functions.arrayToList(event.values)
                    dataValues.add(0, event.timestamp.toFloat())
                    dataValues.add(0f)
                    collector.collectToFile("LinearA", dataValues)
                }
            } else if (event.sensor.type == Sensor.TYPE_STEP_DETECTOR) {
                val stepFound = event.values[0] == 1f
                if (stepFound) {
                    var oPointX = overlaySensors.lastYPoint
                    var oPointY = -overlaySensors.lastXPoint

                    oPointX += Functions.getXFromPolar(
                        strideLength.toDouble(),
                        gHeading.toDouble()
                    )
                    oPointY += Functions.getYFromPolar(
                        strideLength.toDouble(),
                        gHeading.toDouble()
                    )

                    val rPointX = -oPointY
                    val rPointY = oPointX
                    overlaySensors.addPoint(rPointX.toDouble(), rPointY.toDouble())

                    collector.collectToFile(
                        "Set",
                        (event.timestamp - startTime).toFloat(),
                        strideLength,
                        mHeading,
                        gHeading,
                        oPointX,
                        oPointY,
                        rPointX,
                        rPointY
                    )

                    ll.removeAllViews()
                    ll.addView(overlaySensors.getGraphView(context))
                }
            }
        }
    }
}