package com.example.gaxer

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Time
import java.util.*
import kotlin.concurrent.timer

class AlertService: Service() {
    private var token:String? = null
    private val getData = DataProcess()

    @SuppressLint("CommitPrefEdits")
    override fun onCreate() {
        super.onCreate()
        val pref = getSharedPreferences("info", 0)
        token = pref.getString("token", null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManger = getSystemService(NotificationManager::class.java)
            val alterChannel = NotificationChannel("01", "測試服務", NotificationManager.IMPORTANCE_MIN)
            alterChannel.enableVibration(false)
            alterChannel.enableLights(false)
            notificationManger.createNotificationChannel(alterChannel)
            val notification = NotificationCompat.Builder(this, "01")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("GaXeR")
                .setContentText("背景監控中")
                //.setColor(getColor(R.color.black))
                .build()
            startForeground(1, notification)
            //createNotification("自動新增通知", "成功", "02", "自動新增通知")

        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timer().schedule(object :TimerTask(){
            var count = 0
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                val pref = getSharedPreferences("info", 0)
                val editor = pref.edit()
                Thread{
                    val response = getData.getData("alert?tok=${token}")
                    //取得出問題的裝置清單
                    val alertDevList = JSONObject(response).getString("alert")
                    //轉換成jsonArray
                    val alertDev = JSONArray(alertDevList)
                    for (i in 0 until alertDev.length()){
                        Log.d("detail", alertDev[i].toString())
                        val tmp = alertDev[i].toString()
                            .replace("}", "")
                            .replace("{", "")
                            .replace("\"", "")
                            .split(":")
                        val devName = tmp[0]
                        val warningCode = tmp[1]
                        if (warningCode != "0000" && pref.getString(devName, null) == "0"){
                            createNotification("${devName}發生問題",warningCode, "03", devName+"發生問題")
                            editor.putString(devName, "1").apply()
                            if(i == alertDev.length()-1){
                                Timer().cancel()
                                break
                            }
                            Log.d("tmp", tmp.toString())
                        }
                    }
                }.start()
                count += 1
                Log.d("Timer", count.toString())
            }
        },0, 1000)
        return START_REDELIVER_INTENT
        //return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(notificationTitle:String, notificationText:String, channelId:String, channelName:String){
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setAutoCancel(false)
        val notification:Notification = builder.build()
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        val id = (Math.random()*1000).toInt()+1
        manager.notify(id, notification)
    }
}