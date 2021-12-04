package com.example.geohack.utils

import com.example.geohack.utils.Functions.multiplyMatrices
import com.example.geohack.utils.Functions.calcNorm
import com.example.geohack.utils.Functions.scaleMatrix
import com.example.geohack.utils.Functions.addMatrices
import com.example.geohack.utils.Functions.factorial

class GyroscopeEulerOrientation() {
    private var C: Array<FloatArray>

    constructor(initialOrientation: Array<FloatArray>) : this() {
        C = initialOrientation.clone()
    }

    fun getOrientationMatrix(gyroValues: FloatArray): Array<FloatArray> {
        val wX = gyroValues[1]
        val wY = gyroValues[0]
        val wZ = -gyroValues[2]
        val A = calcMatrixA(wX, wY, wZ)
        calcMatrixC(A)
        return C.clone()
    }

    fun getHeading(gyroValue: FloatArray): Float {
        getOrientationMatrix(gyroValue)
        return Math.atan2(C[1][0].toDouble(), C[0][0].toDouble()).toFloat()
    }

    private fun calcMatrixA(wX: Float, wY: Float, wZ: Float): Array<FloatArray> {
        var A: Array<FloatArray>

        var B = calcMatrixB(wX, wY, wZ)
        var B_sq: Array<FloatArray> = multiplyMatrices(B, B)
        val norm = calcNorm(wX.toDouble(), wY.toDouble(), wZ.toDouble())
        val B_scaleFactor = calcBScaleFactor(norm)
        val B_sq_scaleFactor = calcBSqScaleFactor(norm)
        B = scaleMatrix(B, B_scaleFactor)
        B_sq = scaleMatrix(B_sq, B_sq_scaleFactor)
        A = addMatrices(B, B_sq)
        A = addMatrices(A, Functions.IDENTITY_MATRIX)
        return A
    }

    private fun calcMatrixB(wX: Float, wY: Float, wZ: Float): Array<FloatArray> {
        return arrayOf(
            floatArrayOf(0f, wZ, -wY),
            floatArrayOf(-wZ, 0f, wX),
            floatArrayOf(wY, -wX, 0f)
        )
    }

    private fun calcBScaleFactor(sigma: Float): Float {
        val sigmaSqOverThreeFactorial = Math.pow(sigma.toDouble(), 2.0).toFloat() / factorial(3)
        val sigmaToForthOverFiveFactorial = Math.pow(sigma.toDouble(), 4.0)
            .toFloat() / factorial(5)
        return (1.0 - sigmaSqOverThreeFactorial + sigmaToForthOverFiveFactorial).toFloat()
    }

    private fun calcBSqScaleFactor(sigma: Float): Float {
        val sigmaSqOverFourFactorial = Math.pow(sigma.toDouble(), 2.0).toFloat() / factorial(4)
        val sigmaToForthOverSixFactorial = Math.pow(sigma.toDouble(), 4.0)
            .toFloat() / factorial(6)
        return (0.5 - sigmaSqOverFourFactorial + sigmaToForthOverSixFactorial).toFloat()
    }

    private fun calcMatrixC(A: Array<FloatArray>) {
        C = multiplyMatrices(C, A)
    }

    init {
        C = Functions.IDENTITY_MATRIX.clone()
    }
}
