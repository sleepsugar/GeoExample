package com.example.geohack.utils

import com.example.geohack.utils.Functions.nsToSec

class GyroscopeDeltaOrientation() {
    private var isFirstRun: Boolean
    private var sensitivity: Float = 0f
    private var lastTimestamp = 0f
    private var gyroBias: FloatArray

    constructor(sensitivity: Float, gyroBias: FloatArray) : this() {
        this.sensitivity = sensitivity
        this.gyroBias = gyroBias
    }

    fun calcDeltaOrientation(timestamp: Long, rawGyroValues: FloatArray): FloatArray {
        //get the first timestamp
        if (isFirstRun) {
            isFirstRun = false
            lastTimestamp = nsToSec(timestamp.toFloat())
            return FloatArray(3)
        }
        val unbiasedGyroValues = removeBias(rawGyroValues)

        return integrateValues(timestamp, unbiasedGyroValues)
    }

    fun setBias(gyroBias: FloatArray) {
        this.gyroBias = gyroBias
    }

    private fun removeBias(rawGyroValues: FloatArray): FloatArray {
        val unbiasedGyroValues = FloatArray(3)
        for (i in 0..2) {
            unbiasedGyroValues[i] = rawGyroValues[i] - gyroBias[i]

            if (Math.abs(unbiasedGyroValues[i]) > sensitivity) {
                unbiasedGyroValues[i] = unbiasedGyroValues[i]
            } else {
                unbiasedGyroValues[i] = 0f
            }
        }

        return unbiasedGyroValues
    }

    private fun integrateValues(timestamp: Long, gyroValues: FloatArray): FloatArray {
        val currentTime = nsToSec(timestamp.toFloat()).toDouble()
        val deltaTime = currentTime - lastTimestamp
        val deltaOrientation = FloatArray(3)

        for (i in 0..2) {
            deltaOrientation[i] = gyroValues[i] * deltaTime.toFloat()
        }

        lastTimestamp = currentTime.toFloat()
        return deltaOrientation
    }

    init {
        gyroBias = FloatArray(3)
        sensitivity = 0.0025f
        isFirstRun = true
    }
}
