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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun run() {
                val pref = getSharedPreferences("info", 0)
                val editor = pref.edit()
                Thread{
                    val response:String? = getData.getData("alert?tok=${token}")
                    //取得出問題的裝置清單
                    //Log.d("service", response.toString())
                    val alertDevList = response?.let { JSONObject(it).getString("alert") }
                    //Log.d("alert", response.toString())
                    //轉換成jsonArray
                    val alertDev = JSONArray(alertDevList)
                    for (i in 0 until alertDev.length()){
                        val tmp = alertDev[i].toString()
                            .replace("}", "")
                            .replace("{", "")
                            .replace("\"", "")
                            .split(":")
                        val devName = tmp[0]
                        val warningCode = tmp[1]
                        if (warningCode != "000000" && pref.getString(devName, null) == "0"){
                            var msgError = ""
                            for ((index, value) in warningCode.withIndex()){
                                if (value.toString() != "0"){
                                    when(index.toString()){
                                        "0" ->{msgError += "群組異常\n"}
                                        "1" ->{msgError += "強烈搖晃\n"}
                                        "2" ->{msgError += "火焰異常\n"}
                                        "3" ->{msgError += "電量不足\n"}
                                        "4" ->{msgError += "瓦斯洩漏\n"}
                                        "5" ->{msgError += "溫度過高\n"}
                                    }
                                }
                            }
                            createNotification("裝置$devName 發生問題", msgError, "1", "裝置發生異常")
                            /*
                            when(warningCode){
                                "0001" ->{
                                    createNotification("裝置:${devName} 發生問題","溫度異常", "0001", devName+"溫度異常")
                                }
                                "0010" ->{
                                    createNotification("裝置:${devName} 發生問題","瓦斯異常", "0010", devName+"瓦斯異常")
                                }
                                "0100" ->{
                                    createNotification("裝置:${devName} 發生問題","火焰異常", "0100", devName+"火焰異常")
                                }
                                "1000" ->{
                                    createNotification("裝置:${devName} 發生問題","電量不足", "1000", devName+"電量不足")
                                }
                                "0011" ->{
                                    createNotification("裝置:${devName} 發生問題","異常搖晃", "0011", devName+"異常搖晃")
                                }
                            }

                             */
                            editor.putString(devName, "1").apply()
                            if(i == alertDev.length()-1){
                                Timer().cancel()
                                break
                            }
                        }
                    }
                }.start()
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