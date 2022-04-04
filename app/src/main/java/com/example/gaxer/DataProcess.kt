package com.example.gaxer

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf

class DataProcess{
    fun getData(url: String): String? {
        val client = OkHttpClient()
        val responseStr:String?
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        responseStr = response.body()?.string()
        /*
        if (responseStr != null){
            Log.d("OKHttp", "Success")
        }
        else{
            Log.d("OKHttp", "Error")
        }

         */
        return responseStr
    }

    fun parseDataTime(responseArray:String?): ArrayList<String> {
        val responseJson = JSONArray(responseArray)
        var gasName = ""
        val gasTime = ArrayList<String>()
        for(i in responseJson.length()-1 downTo 0){//倒著跑迴圈
            val tmp = JSONObject(responseJson.getString(i))
            for(keyName in tmp.keys()){
                gasName = keyName
                break
            }
            val gas = JSONObject(tmp.getString(gasName))
            val dataBody = JSONObject(gas.getString("data"))
            val time = dataBody.getString("time")
            //timeStamp to dateTime
            val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.TAIWAN)
            gasTime.add(simpleDateFormat.format(Date(time.toLong()*1000)))

        }
        return gasTime
    }

    fun parseDataRemaining(responseArray:String?): ArrayList<String>{
        val responseJson = JSONArray(responseArray)
        var gasName = ""
        val gasRemaining = ArrayList<String>()
        for(i in responseJson.length()-1 downTo 0){
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
        return gasRemaining
    }
}