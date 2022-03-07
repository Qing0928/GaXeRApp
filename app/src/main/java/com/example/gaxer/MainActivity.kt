package com.example.gaxer

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val responseView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener(){
            Toast.makeText(this, "hello toast", Toast.LENGTH_SHORT).show()
        }
        val buttonGetRequest = findViewById<Button>(R.id.btn_get)
        buttonGetRequest.setOnClickListener(){
            Thread(){
                val client = OkHttpClient()
                val request = Request.Builder().url("https://gaxer.ddns.net:443/test").build()
                val response = client.newCall(request).execute()
                val responseStr = response.body()?.string()
                runOnUiThread{responseView.text = "Success"}
                if (responseStr != null) {
                    Log.d("OkHttpGET", responseStr)
                }
                val strArray:String = responseStr.toString()
                val jsonArray = JSONArray(strArray)
                val test:String = jsonArray.getString(0,)
                Log.d("json", test)
                val json:String = test
                val jsonObject = JSONObject(json)
                val test2:String = jsonObject.getString("gas1")
                Log.d("json", test2)
            }.start()
        }
    }
    /*
    override fun onClick(view: View?){
        if (view is Button){
            when(view.id){
                R.id.button->
                    Toast.makeText(this, "hello world", Toast.LENGTH_SHORT).show()
                R.id.btn_get->
                    Toast.makeText(this, "get succes", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}
