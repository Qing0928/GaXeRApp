package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import org.json.JSONArray
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "ResourceType", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val getData = DataProcess()
        val token:String? = intent.getStringExtra("token")
        val pref = getSharedPreferences("info", 0)
        val editor = pref.edit()
        val alertBack = Intent(this, AlertService()::class.java)
        //var alertList:JSONArray

        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.MAINACTIVITY")

        BottomNavigation(findViewById(R.id.navigationbottomView))

        //loadFragment(HomeFragment())
        val horizontalScroll = findViewById<LinearLayout>(R.id.HorizontalLinear)
        var devList:JSONArray
        stopService(alertBack)

        Thread{
            var response:String? = getData.getData("devlist?tok=${token}")
            if (response!=null){
                val devListObj = JSONObject(response).getString("devList")
                devList = JSONArray(devListObj)
                //val devList = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
                //var leftPosition = 0
                for(i in 0 until devList.length()){
                    //取得裝置清單，自動在畫面上新增按鈕
                    runOnUiThread{
                        val devButton = ImageButton(this)
                        devButton.setImageResource(R.drawable.gasbottle)
                        devButton.scaleType = ImageView.ScaleType.FIT_CENTER
                        devButton.layoutParams = LinearLayout.LayoutParams(200, 200)
                        val layoutParams = devButton.layoutParams as LinearLayout.LayoutParams
                        layoutParams.topMargin = 50
                        //layoutParams.marginStart = leftPosition
                        devButton.layoutParams = layoutParams
                        //devButton.id = i
                        devButton.setOnClickListener {
                            val xAxisData = ArrayList<Entry>()
                            xAxisData.clear()
                            val url = "data?tok=${token}&record=4&dev=${devList[i]}"
                            Thread{
                                response = getData.getData(url)
                                val xLabel: ArrayList<String> = getData.parseDataTime(response, devList[i].toString())
                                val remain: ArrayList<String> = getData.parseDataRemaining(response, devList[i].toString())
                                for ((xAxis, j) in remain.withIndex()){//(xAxis, j)>>(index, value)
                                    xAxisData.add(Entry(xAxis.toFloat(), j.toFloat()))
                                }
                                //使用intent傳遞資料
                                intent.putExtra("xAxisData", xAxisData)
                                intent.putExtra("xLabel", xLabel)
                                intent.putExtra("token", token)
                                intent.putExtra("devName", devList[i].toString())
                                startActivity(intent)
                            }.start()
                        }
                        horizontalScroll.addView(devButton)
                        //leftPosition += 50
                    }
                    if(pref.getString(devList[i].toString(), null) == null){
                        editor.putString(devList[i].toString(), "0").apply()
                    }
                    else{
                        editor.putString(devList[i].toString(), "0").apply()
                    }
                }
            }
            if (token != null){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    startForegroundService(alertBack)
                }
            }
            else{
                Log.d("Service", "Fail to Start Service")
            }
        }.start()
        /*
        Thread{
            val response:String? = getData.getData("devlist?tok=${token}")
            if(response != null){
                val alertListObj = JSONObject(response).getString("devList")
                alertList = JSONArray(alertListObj)
                for (i in 0 until alertList.length()){
                    if(pref.getString(alertList[i].toString(), null) == null){
                        editor.putString(alertList[i].toString(), "0").apply()
                    }
                    else{
                        editor.putString(alertList[i].toString(), "0").apply()
                    }
                }
            }
            //啟動Service
            if (token != null){
                val alertBack = Intent(this, AlertService()::class.java)
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    startForegroundService(alertBack)
                }
            }
            else{
                Log.d("Service", "Fail to Start Service")
            }
        }.start()

         */
    }
    //切換fragment
    /*
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
     */
}