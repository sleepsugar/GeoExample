package com.example.geohack.utils

class DynamicStepCounter() {
    var stepCount = 0
        private set
    var sensitivity = 1.0
        private set

    private var upperThreshold = 10.8
    private var lowerThreshold = 8.8
    private var firstRun = true
    private var peakFound = false
    private var upperCount: Int
    private var lowerCount = 0
    private var sumUpperAcc: Double
    private var sumLowerAcc: Double
    private var sumAcc: Double
    private var avgAcc: Double
    private var runCount: Int

    constructor(sensitivity: Double) : this() {
        this.sensitivity = sensitivity
    }

    fun findStep(acc: Double): Boolean {

        setThresholdsContinuous(acc)

        if (acc > upperThreshold) {
            if (!peakFound) {
                stepCount++
                peakFound = true
                return true
            }
        } else if (acc < lowerThreshold) {
            if (peakFound) {
                peakFound = false
            }
        }
        return false
    }

    private fun setThresholdsContinuous(acc: Double) {
        runCount++
        if (firstRun) {
            upperThreshold = acc + sensitivity
            lowerThreshold = acc - sensitivity
            avgAcc = acc
            firstRun = false
            return
        }

        avgAcc =
            avgAcc * ((runCount.toDouble() - 1.0) / runCount.toDouble()) + acc / runCount.toDouble()
        upperThreshold = avgAcc + sensitivity
        lowerThreshold = avgAcc - sensitivity
    }

    companion object {
        const val REQUIRED_HZ = 500
    }

    init {
        upperCount = lowerCount
        sumLowerAcc = 0.0
        sumUpperAcc = sumLowerAcc
        avgAcc = 0.0
        sumAcc = avgAcc
        runCount = 0
    }
}