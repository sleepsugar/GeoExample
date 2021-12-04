package com.example.geohack.graph

import android.content.Context
import android.graphics.Color
import android.view.View
import org.achartengine.model.XYSeries
import org.achartengine.renderer.XYSeriesRenderer
import org.achartengine.model.XYMultipleSeriesDataset
import org.achartengine.renderer.XYMultipleSeriesRenderer
import org.achartengine.chart.PointStyle
import org.achartengine.ChartFactory
import java.util.ArrayList

class DrawPlot(private val seriesName: String, private val color: String) {
    private val xList: ArrayList<Double>
    private val yList: ArrayList<Double>
    fun getGraphView(context: Context): View {
        val mySeries: XYSeries
        val myRenderer: XYSeriesRenderer
        val myMultiSeries: XYMultipleSeriesDataset
        val myMultiRenderer: XYMultipleSeriesRenderer

        val xSet = DoubleArray(xList.size)
        for (i in xList.indices) xSet[i] = xList[i]

        val ySet = DoubleArray(yList.size)
        for (i in yList.indices) ySet[i] = yList[i]

        mySeries = XYSeries(seriesName)
        for (i in xSet.indices) mySeries.add(xSet[i], ySet[i])

        myRenderer = XYSeriesRenderer()
        myRenderer.isFillPoints = true
        myRenderer.pointStyle = PointStyle.CIRCLE
        myRenderer.color = Color.parseColor("#ff0000")
        myMultiSeries = XYMultipleSeriesDataset()
        myMultiSeries.addSeries(mySeries)
        myMultiRenderer = XYMultipleSeriesRenderer()
        myMultiRenderer.addSeriesRenderer(myRenderer)

        myMultiRenderer.pointSize = 6f
        myMultiRenderer.isShowLegend = false

        val chartMargins = intArrayOf(100, 100, 25, 100)
        myMultiRenderer.margins = chartMargins
        myMultiRenderer.yLabelsPadding = 50f
        myMultiRenderer.xLabelsPadding = 10f

        val bound = maxBound
        myMultiRenderer.xAxisMin = -bound
        myMultiRenderer.xAxisMax = bound
        myMultiRenderer.yAxisMin = -bound
        myMultiRenderer.yAxisMax = bound

        return ChartFactory.getScatterChartView(context, myMultiSeries, myMultiRenderer)
    }

    fun addPoint(x: Double, y: Double) {
        xList.add(x)
        yList.add(y)
    }

    val lastXPoint: Float
        get() {
            val x = xList[xList.size - 1]
            return x.toFloat()
        }
    val lastYPoint: Float
        get() {
            val y = yList[yList.size - 1]
            return y.toFloat()
        }

    fun clearSet() {
        xList.clear()
        yList.clear()
    }

    private val maxBound: Double
        private get() {
            var max = 0.0
            for (num in xList) if (max < Math.abs(num)) max = num
            for (num in yList) if (max < Math.abs(num)) max = num
            return Math.abs(max) / 100 * 100 + 100 //rounding up to the nearest hundred
        }

    init {
        xList = ArrayList()
        yList = ArrayList()
    }
}