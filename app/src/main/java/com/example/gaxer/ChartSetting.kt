package com.example.gaxer

import android.graphics.Color
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartSetting(val lineChart:LineChart, val dataSet:ArrayList<Entry>?) {
    init {
        initChart()
        if (dataSet != null) {
            initData(dataSet)
        }
    }
    //init data
    private fun initData(values:ArrayList<Entry>){
        val dataset = LineDataSet(values, "Remain Gas")
        dataset.mode = LineDataSet.Mode.LINEAR
        dataset.color = Color.BLUE //color of line
        dataset.lineWidth = 3f// width of line
        dataset.setDrawCircles(false)
        dataset.setDrawValues(false)
        val data = LineData(dataset)
        this.lineChart.data = data
        this.lineChart.invalidate()
    }
    //update data
    fun updateData(dataSet: ArrayList<Entry>){
        setyAxis()
        setyAxis()
        initData(dataSet)
    }
    //set xAxis
    private fun setxAxis(){
        val xAxis = this.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 12f
        xAxis.textColor = Color.BLACK
        xAxis.setLabelCount(6, true)
        val xLable:MutableList<String> = ArrayList()
        xLable.add("Jan")
        xLable.add("Feb")
        xLable.add("Mar")
        xLable.add("Apr")
        xLable.add("May")
        xLable.add("June")
        xAxis.valueFormatter = IndexAxisValueFormatter(xLable)
    }
    //set yAxis
    private  fun  setyAxis(){
        lineChart.axisRight.isEnabled = false
        val leftAxis = lineChart.axisLeft
        leftAxis.textSize = 16f
        leftAxis.textColor = Color.RED
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 100f
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

        lineChart.setDrawBorders(true)
        lineChart.setBorderColor(Color.GREEN)

        lineChart.setNoDataText("Nothing")
        lineChart.setNoDataTextColor(Color.RED)

        lineChart.setPinchZoom(true)//xAxis and yAxis auto zoom at the same time
    }
}