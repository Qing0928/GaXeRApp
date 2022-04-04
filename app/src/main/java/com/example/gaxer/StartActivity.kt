package com.example.gaxer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.Entry

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val getData = DataProcess()
        Thread{
            val xAxisData = ArrayList<Entry>()
            xAxisData.clear()
            val url = "https://gaxer.ddns.net/data/?tok=123456abcd&record=4"
            val response:String? = getData.getData(url)
            val xLabel: ArrayList<String> = getData.parseDataTime(response)
            val remain: ArrayList<String> = getData.parseDataRemaining(response)
            for ((xAxis, i) in remain.withIndex()){//(xAxis, i)>>(index, value)
                xAxisData.add(Entry(xAxis.toFloat(), i.toFloat()))
            }
            //使用intent傳遞資料
            val intent = Intent("android.intent.action.MAIN")
            intent.addCategory("android.intent.category.MAINACTIVITY")
            intent.putExtra("xAxisData", xAxisData)
            intent.putExtra("xLabel", xLabel)
            startActivity(intent)
        }.start()
    }
}