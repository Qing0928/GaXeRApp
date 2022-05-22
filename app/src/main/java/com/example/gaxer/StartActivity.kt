package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi

class StartActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits", "SdCardPath")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        //val getData = DataProcess()
        //val xAxisData = ArrayList<Entry>()
        val pref = getSharedPreferences("info", 0)
        /*
        val devPref = getSharedPreferences("dev", 0)
        val devEditor = devPref.edit()
        devEditor.putString("gas1", "24:0A:C4:59:A5:44").apply()
        devEditor.putString("gas2", "34:94:54:24:85:0C").apply()

         */

        //讀取看是否有使用者資料
        val token:String? = pref.getString("token", null)
        val intent = Intent("android.intent.action.MAIN")
        if (token != null){
            intent.addCategory("android.intent.category.ALL")
            intent.putExtra("token", token)
            /*刪除檔案
            val path = Paths.get("/data/data/com.example.gaxer/shared_prefs/grouptest01.xml")
            try {
                val result = Files.deleteIfExists(path)
                if (result){
                    Log.d("file delete", "success")
                }
                else{
                    Log.d("file delete", "fail")
                }
            }
            catch(e:IOException){
                Log.d("file delete", "fail & exception")
                e.printStackTrace()
            }

             */
            startActivity(intent)
        }
        else{
            intent.addCategory("android.intent.category.LOGIN")
            startActivity(intent)
        }
    }
}