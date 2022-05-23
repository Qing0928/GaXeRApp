package com.example.gaxer

import android.graphics.Color
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartSetting(private val lineChart:LineChart, dataSet:ArrayList<Entry>?) {
    init {
        initChart()
        if (dataSet != null) {
            initData(dataSet)
        }
    }
    //update data
    fun updateData(dataSet: ArrayList<Entry>, xLabel: ArrayList<String> = ArrayList()){
        setyAxis()
        setyAxis()
        initData(dataSet)
        val xAxis = this.lineChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xLabel)
    }
    //init data
    private fun initData(values:ArrayList<Entry>){
        val dataset = LineDataSet(values, "Remain Gas")
        dataset.mode = LineDataSet.Mode.LINEAR
        dataset.color = Color.rgb(203, 64, 66) //color of line
        dataset.lineWidth = 6f// width of line
        dataset.setDrawCircles(true)
        dataset.setDrawValues(true)
        dataset.valueTextSize = 14f
        dataset.circleRadius = 7f
        dataset.setCircleColor(Color.rgb(86, 63, 46))
        val data = LineData(dataset)
        this.lineChart.data = data
        //this.lineChart.setBackgroundColor(Color.WHITE)
        this.lineChart.invalidate()
    }
    //set xAxis
    private fun setxAxis(){
        val xAxis = this.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 14f
        //xAxis.labelRotationAngle = 10F
        xAxis.textColor = Color.BLACK
        //xAxis.gridLineWidth = 2F
        xAxis.setDrawGridLines(true)
        xAxis.setDrawAxisLine(false)
        xAxis.setLabelCount(4, true)
    }
    //set yAxis
    private fun setyAxis(){
        lineChart.axisRight.isEnabled = false
        val leftAxis = lineChart.axisLeft
        leftAxis.textSize = 14f
        //leftAxis.axisLineWidth = 4f
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 10000f
        //leftAxis.gridLineWidth = 2F
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(true)
        leftAxis.setLabelCount(6, true)
    }
    //initChart
    private fun initChart(){
        setxAxis()
        setyAxis()
        val description:Description = lineChart.description
        description.isEnabled = false

        val legend = lineChart.legend
        legend.isEnabled = false
        //lineChart.extraRightOffset = 10f
        lineChart.setExtraOffsets(0f, 0f, 25f, 15f)
        lineChart.setDrawBorders(true)
        lineChart.setBorderWidth(2F)
        lineChart.setBorderColor(Color.BLACK)
        lineChart.setNoDataText("Nothing")
        lineChart.setNoDataTextColor(Color.RED)

        lineChart.setPinchZoom(true)//xAxis and yAxis auto zoom at the same time
    }
}