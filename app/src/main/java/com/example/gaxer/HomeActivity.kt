package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.github.mikephil.charting.data.Entry
import org.json.JSONArray
import org.json.JSONObject
import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "ResourceType", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val getData = DataProcess()
        //val token:String? = intent.getStringExtra("token")
        val pref = getSharedPreferences("info", 0)
        val token:String? = pref.getString("token", null)
        val editor = pref.edit()

        val alertBack = Intent(this, AlertService()::class.java)

        val intent = Intent("android.intent.action.MAIN")

        val bottomNav = findViewById<BottomNavigationView>(R.id.navigationbottomView)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    intent.addCategory("android.intent.category.ALL")
                    intent.putExtra("token", token)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.addDev ->{

                    return@setOnItemSelectedListener true
                }
                R.id.setting->{
                    intent.addCategory("android.intent.category.ADDGROUP")
                    intent.putExtra("token", token)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
        //loadFragment(HomeFragment())

        val horizontalScroll = findViewById<LinearLayout>(R.id.HorizontalLinear)
        val groupScroll = findViewById<LinearLayout>(R.id.ScrollLinear)
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
                                intent.addCategory("android.intent.category.MAINACTIVITY")
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
                var tmp = ""
                for (i in 0 until devList.length()){
                    val groupCheck:String? = getData.getData("groupcheck?tok=${token}&dev=${devList[i]}")
                    groupCheck?.let { Log.d("groupCheck", it) }
                    if(groupCheck != null && groupCheck != "nan"){
                        if (groupCheck == tmp){
                            continue
                        }
                        else{
                            val prefGroup = getSharedPreferences(groupCheck, 0)
                            val devGroupList = prefGroup.getString("group", null)
                                ?.replace("[", "")
                                ?.replace("]", "")
                                ?.replace(" ", "")
                                ?.split(",")
                            runOnUiThread {
                                //宣告群組名稱
                                val groupText = TextView(this)
                                groupText.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                val textParams = groupText.layoutParams as LinearLayout.LayoutParams
                                textParams.topMargin = 20
                                groupText.layoutParams = textParams
                                groupText.text = groupCheck.replace("group", "")
                                groupText.textSize = 20F
                                //把文字加進CardView
                                groupScroll.addView(groupText)

                                //宣告CardView
                                val groupCard = CardView(this)
                                groupCard.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350)
                                val cardParams = groupCard.layoutParams as LinearLayout.LayoutParams
                                cardParams.topMargin = 40
                                groupCard.layoutParams = cardParams
                                groupCard.setBackgroundResource(R.drawable.horizontal_scroll)
                                //宣告按鈕
                                if (devGroupList != null) {
                                    var leftMargin = 10
                                    for(dev in devGroupList){
                                        val devButton = ImageButton(this)
                                        devButton.setImageResource(R.drawable.gasbottle)
                                        devButton.scaleType = ImageView.ScaleType.FIT_CENTER
                                        devButton.layoutParams = LinearLayout.LayoutParams(200, 200)
                                        val layoutParams = devButton.layoutParams as LinearLayout.LayoutParams
                                        layoutParams.topMargin = 50
                                        layoutParams.marginStart = leftMargin
                                        devButton.layoutParams = layoutParams
                                        devButton.setOnClickListener {
                                            val xAxisData = ArrayList<Entry>()
                                            xAxisData.clear()
                                            val url = "data?tok=${token}&record=4&dev=${dev}"
                                            Thread{
                                                response = getData.getData(url)
                                                val xLabel: ArrayList<String> = getData.parseDataTime(response, dev)
                                                val remain: ArrayList<String> = getData.parseDataRemaining(response, dev)
                                                for ((xAxis, j) in remain.withIndex()){//(xAxis, j)>>(index, value)
                                                    xAxisData.add(Entry(xAxis.toFloat(), j.toFloat()))
                                                }
                                                intent.addCategory("android.intent.category.MAINACTIVITY")
                                                //使用intent傳遞資料
                                                intent.putExtra("xAxisData", xAxisData)
                                                intent.putExtra("xLabel", xLabel)
                                                intent.putExtra("token", token)
                                                Log.d("devName", dev)
                                                intent.putExtra("devName", dev)
                                                startActivity(intent)
                                            }.start()
                                        }
                                        groupCard.addView(devButton)
                                        leftMargin += 240
                                    }
                                }
                                //把CardView加進去ScrollView
                                groupScroll.addView(groupCard)
                            }
                            devGroupList?.get(0)?.let { Log.d("devGroupList", it) }
                            tmp = groupCheck
                        }
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