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
        val editor = pref.edit()
        //editor.clear().apply()
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
                //對SharedPreferences寫入資料
                /*
                editor.putString("account", "test01").apply()
                editor.putString("token", "937E8D5FBB48BD4949536CD65B8D35C426B80D2F830C5C308E2CDEC422AE2244").apply()
                val test:String? = pref.getString("token", "")
                if (test != null) {
                    Log.d("pref", test)
                }
                else{
                    Log.d("pref", "false")
                }
                 */
            }.start()
        }
        else{
            intent.addCategory("android.intent.category.LOGIN")
            startActivity(intent)
        }
    }
}