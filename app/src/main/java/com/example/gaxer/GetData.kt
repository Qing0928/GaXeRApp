package com.example.gaxer

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class HttpGet(){
    fun getData(url: String): String? {
        val client = OkHttpClient()
        val responseStr:String?
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        responseStr = response.body()?.string()
        if (responseStr != null){
            Log.d("OKHttp", responseStr)
        }
        else{
            Log.d("OKHttp", "Error")
        }
        return responseStr
    }
    fun parseData(responseArray:String?): String? {
        val responseJson = JSONArray(responseArray)
        val gas1:String = responseJson.getString(0)
        Log.d("Json", gas1)
        return null
    }
}