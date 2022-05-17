package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import com.github.mikephil.charting.data.Entry
import android.util.Log

class StartActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //val getData = DataProcess()
        //val xAxisData = ArrayList<Entry>()
        val pref = getSharedPreferences("info", 0)
        //讀取看是否有使用者資料
        val token:String? = pref.getString("token", null)
        val intent = Intent("android.intent.action.MAIN")

        if (token != null){
            intent.addCategory("android.intent.category.ALL")
            intent.putExtra("token", token)
            startActivity(intent)
        }
        else{
            intent.addCategory("android.intent.category.LOGIN")
            startActivity(intent)
        }
    }
}