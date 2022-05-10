package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.Entry
import android.util.Log

class StartActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val getData = DataProcess()
        val xAxisData = ArrayList<Entry>()
        val pref = getSharedPreferences("info", 0)
        //讀取看是否有使用者資料
        val token:String? = pref.getString("token", null)
        Log.d("token", token.toString())
        val intent = Intent("android.intent.action.MAIN")

        if (token != null){
            Thread{
                xAxisData.clear()
                val url = "data?tok=${token}&record=4"
                Log.d("url", url)
                val response:String? = getData.getData(url)
                val xLabel: ArrayList<String> = getData.parseDataTime(response)
                val remain: ArrayList<String> = getData.parseDataRemaining(response)
                for ((xAxis, i) in remain.withIndex()){//(xAxis, i)>>(index, value)
                    xAxisData.add(Entry(xAxis.toFloat(), i.toFloat()))
                }
                //使用intent傳遞資料
                intent.addCategory("android.intent.category.MAINACTIVITY")
                intent.putExtra("xAxisData", xAxisData)
                intent.putExtra("xLabel", xLabel)
                intent.putExtra("token", token)
                startActivity(intent)
            }.start()
        }
        else{
            intent.addCategory("android.intent.category.LOGIN")
            startActivity(intent)
        }
    }
}