package com.example.gaxer

import android.util.Log
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataProcess{
    companion object{
        const val serverURL = "https://gaxer.ddns.net/"
    }
    fun getData(api: String): String? {
        val client = OkHttpClient()
        val responseStr:String?
        val request = Request.Builder().url(serverURL+api).build()
        val response = client.newCall(request).execute()
        responseStr = response.body()?.string()

        return responseStr
    }
    fun postData(api: String, postBody: FormBody):String?{
        val client = OkHttpClient()
        val responseStr: String?
        val request = Request.Builder().url(serverURL+api).post(postBody).build()
        val response = client.newCall(request).execute()
        responseStr = response.body()?.string()
        return responseStr
    }

    fun parseDataTime(responseArray:String?, devName:String): ArrayList<String> {
        Log.d("response", responseArray.toString())
        val responseJson = JSONArray(responseArray)
        val gasTime = ArrayList<String>()
        for(i in responseJson.length()-1 downTo 0){//倒著跑迴圈
            val tmp = JSONObject(responseJson.getString(i))
            val gas = JSONObject(tmp.getString(devName))
            val dataBody = JSONObject(gas.getString("data"))
            val time = dataBody.getString("time")
            //timeStamp to dateTime
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.TAIWAN)
            gasTime.add(simpleDateFormat.format(Date(time.toLong()*1000)))

        }
        return gasTime
    }

    fun parseDataRemaining(responseArray:String?, devName:String): ArrayList<String>{
        val responseJson = JSONArray(responseArray)
        val gasRemaining = ArrayList<String>()
        for(i in responseJson.length()-1 downTo 0){
            val tmp = JSONObject(responseJson.getString(i))
            val gas = JSONObject(tmp.getString(devName))
            val dataBody = JSONObject(gas.getString("data"))
            val remaining = dataBody.getString("remaining")
            val remainingInt = remaining.toFloat() -6000
            Log.d("remain", remainingInt.toString())
            gasRemaining.add(remainingInt.toString())
        }
        return gasRemaining
    }
}