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
            Log.d("OKHttp", "Success")
        }
        else{
            Log.d("OKHttp", "Error")
        }
        return responseStr
    }
    fun parseDataTime(responseArray:String?): MutableList<String> {
        val responseJson = JSONArray(responseArray)
        var gasName = ""
        val gasTime = mutableListOf<String>()
        for(i in 0 until responseJson.length()){
            val tmp = JSONObject(responseJson.getString(i))
            for(keyName in tmp.keys()){
                gasName = keyName
                break
            }
            val gas = JSONObject(tmp.getString(gasName))
            val dataBody = JSONObject(gas.getString("data"))
            val time = dataBody.getString("time")
            gasTime.add(time)
        }
        for (i in gasTime){
            Log.d("time", i)
        }
        return gasTime
    }
    fun parseDataRemaining(responseArray:String?): MutableList<String>{
        val responseJson = JSONArray(responseArray)
        var gasName = ""
        val gasRemaining = mutableListOf<String>()
        for(i in 0 until responseJson.length()){
            val tmp = JSONObject(responseJson.getString(i))
            for(keyName in tmp.keys()){
                gasName = keyName
                break
            }
            val gas = JSONObject(tmp.getString(gasName))
            val dataBody = JSONObject(gas.getString("data"))
            val remaining = dataBody.getString("remaining")

            gasRemaining.add(remaining)
        }
        for (i in gasRemaining){
            Log.d("remaining", i)
        }
        return gasRemaining
    }
}