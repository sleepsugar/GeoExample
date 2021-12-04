package com.example.geohack.utils

import android.content.SharedPreferences
import org.ejml.data.DenseMatrix64F
import java.util.*

object Functions {

    @JvmField
    val IDENTITY_MATRIX =
        arrayOf(floatArrayOf(1f, 0f, 0f), floatArrayOf(0f, 1f, 0f), floatArrayOf(0f, 0f, 1f))

    fun getXFromPolar(radius: Double, angle: Double): Float {
        return (radius * Math.cos(angle)).toFloat()
    }

    //calculate y coordinate point given radius and angle
    fun getYFromPolar(radius: Double, angle: Double): Float {
        return (radius * Math.sin(angle)).toFloat()
    }

    @JvmStatic
    fun nsToSec(time: Float): Float {
        return time / 1000000000.0f
    }

    @JvmStatic
    fun factorial(num: Int): Int {
        var factorial = 1
        for (i in 1..num) {
            factorial *= i
        }
        return factorial
    }

    @JvmStatic
    fun multiplyMatrices(a: Array<FloatArray>, b: Array<FloatArray>): Array<FloatArray> {

        //numRows = aRows
        val numRows = a.size

        //numCols = bCols
        val numCols: Int = b[0].size

        //numElements = (aCols == bRows)
        val numElements = b.size
        val c = Array(numRows) { FloatArray(numCols) }
        for (row in 0 until numRows) {
            for (col in 0 until numCols) {
                for (element in 0 until numElements) c[row][col] += a[row][element] * b[element][col]
            }
        }

        //a[][] * b[][] = c[][]
        return c
    }

    @JvmStatic
    fun addMatrices(a: Array<FloatArray>, b: Array<FloatArray>): Array<FloatArray> {
        val numRows = a.size
        val numColumns: Int = a[0].size
        val c = Array(numRows) { FloatArray(numColumns) }
        for (row in 0 until numRows) for (column in 0 until numColumns) c[row][column] =
            a[row][column] + b[row][column]

        //a[][] + b[][] = c[][]
        return c
    }

    @JvmStatic
    fun scaleMatrix(a: Array<FloatArray>, scalar: Float): Array<FloatArray> {
        val numRows = a.size
        val numColumns: Int = a[0].size
        val b = Array(numRows) { FloatArray(numColumns) }
        for (row in 0 until numRows) for (column in 0 until numColumns) b[row][column] =
            a[row][column] * scalar

        //a[][] * c = b[][]
        return b
    }

    fun addArrayToSharedPreferences(
        arrayName: String,
        array: ArrayList<String?>,
        editor: SharedPreferences.Editor
    ) {
        editor.putInt(arrayName + "_size", array.size)
        for (i in array.indices) {
            editor.putString(arrayName + "_" + i, array[i])
        }
        editor.apply()
    }

    fun getArrayFromSharedPreferences(
        arrayName: String,
        prefs: SharedPreferences
    ): ArrayList<String?> {
        val arraySize = prefs.getInt(arrayName + "_size", 0)
        val newArray = ArrayList<String?>()
        for (i in 0 until arraySize) {
            newArray.add(prefs.getString(arrayName + "_" + i, null))
        }
        return newArray
    }

    fun arrayToList(staticArray: FloatArray): ArrayList<Float> {
        val dynamicList = ArrayList<Float>()
        for (staticArrayValue in staticArray) dynamicList.add(staticArrayValue)
        return dynamicList
    }

    @JvmStatic
    fun denseMatrixToArray(matrix: DenseMatrix64F): Array<FloatArray> {
        val array = Array(matrix.getNumRows()) { FloatArray(matrix.getNumCols()) }
        for (row in 0 until matrix.getNumRows()) for (col in 0 until matrix.getNumCols()) array[row][col] =
            matrix[row, col]
                .toFloat()
        return array
    }

    @JvmStatic
    fun vectorToMatrix(array: DoubleArray): Array<DoubleArray> {
        return arrayOf(
            doubleArrayOf(array[0]), doubleArrayOf(array[1]), doubleArrayOf(
                array[2]
            )
        )
    }

    fun createList(vararg args: Float): ArrayList<Float> {
        val list = ArrayList<Float>()
        for (arg in args) list.add(arg)
        return list
    }

    fun radsToDegrees(rads: Double): Float {
        var degrees = if (rads < 0) 2.0 * Math.PI + rads else rads
        degrees *= 180.0 / Math.PI
        return degrees.toFloat()
    }

    fun calcCompHeading(magHeading: Double, gyroHeading: Double): Float {
        //complimentary filter

        //convert -pi/2 < h < pi/2 to 0 < h < 2pi
        var magHeading = magHeading
        var gyroHeading = gyroHeading
        if (magHeading < 0) magHeading = magHeading % (2.0 * Math.PI)
        if (gyroHeading < 0) gyroHeading = gyroHeading % (2.0 * Math.PI)
        var compHeading = 0.02 * magHeading + 0.98 * gyroHeading

        //convert 0 < h < 2pi to -pi/2 < h < pi/2
        if (compHeading > Math.PI) compHeading = compHeading % Math.PI + -Math.PI
        return compHeading.toFloat()
    }

    @JvmStatic
    fun calcNorm(vararg args: Double): Float {
        var sumSq = 0.0
        for (arg in args) sumSq += Math.pow(arg, 2.0)
        return Math.sqrt(sumSq).toFloat()
    }

    @JvmStatic
    fun floatVectorToDoubleVector(floatValues: FloatArray): DoubleArray {
        val doubleValues = DoubleArray(floatValues.size)
        for (i in floatValues.indices) doubleValues[i] = floatValues[i].toDouble()
        return doubleValues
    }
}