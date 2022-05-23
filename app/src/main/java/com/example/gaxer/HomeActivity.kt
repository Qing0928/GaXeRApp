package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.github.mikephil.charting.data.Entry
import org.json.JSONArray
import org.json.JSONObject
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


class HomeActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ResourceType", "CommitPrefEdits", "RtlHardcoded", "SdCardPath")
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
                /*
                R.id.addDev ->{
                    /*
                    intent.addCategory("android.intent.category.FINDDEV")
                    intent.putExtra("token", token)
                    startActivity(intent)

                     */
                    return@setOnItemSelectedListener true
                }
                 */
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
                        devButton.setImageResource(R.drawable.gas)
                        devButton.scaleType = ImageView.ScaleType.FIT_CENTER
                        devButton.layoutParams = LinearLayout.LayoutParams(
                            200,
                            200)
                            .apply {
                                topMargin = 20
                            }
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
                        //把裝置按鈕加進橫向滾動
                        horizontalScroll.addView(devButton)
                        //leftPosition += 50
                    }
                    //將警報改為未發送過
                    if(pref.getString(devList[i].toString(), null) == null){
                        editor.putString(devList[i].toString(), "0").apply()
                    }
                    else{
                        editor.putString(devList[i].toString(), "0").apply()
                    }
                    //leftPosition += 100
                }
                var tmp = ""
                for (i in 0 until devList.length()){
                    val groupCheck:String? = getData.getData("groupcheck?tok=${token}&dev=${devList[i]}")
                    groupCheck?.let { Log.d("groupCheck", it) }
                    if(groupCheck != null && groupCheck != "nan"){
                        //第二個裝置如果跟上一個裝置同一群組就跳過
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
                                //把文字加進ScrollView
                                groupScroll.addView(groupText)

                                //宣告CardView
                                val groupCard = CardView(this)
                                groupCard.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400)
                                val cardParams = groupCard.layoutParams as LinearLayout.LayoutParams
                                //cardParams.topMargin = 40
                                cardParams.gravity = Gravity.CENTER_HORIZONTAL
                                groupCard.layoutParams = cardParams
                                groupCard.setBackgroundResource(R.drawable.horizontal_scroll)
                                //宣告按鈕
                                if (devGroupList != null) {
                                    var leftMargin = 10
                                    //將裝置從清單拿出來，逐個放入CardView
                                    for(dev in devGroupList){
                                        val devButton = ImageButton(this)
                                        devButton.setImageResource(R.drawable.gas)
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
                                        //把按鈕加進CardView
                                        groupCard.addView(devButton)
                                        leftMargin += 240
                                    }
                                    //宣告關閉所有裝置按鈕
                                    val btnTurnoff = Button(this)
                                    btnTurnoff.text = "關閉所有裝置"
                                    btnTurnoff.textSize = 15F
                                    btnTurnoff.layoutParams = LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT)
                                        .apply {
                                            marginStart = 235
                                            topMargin = 270
                                        }
                                    btnTurnoff.setOnClickListener {
                                        var responseGroup = ""
                                        Thread{
                                            for (dev in devGroupList){
                                                responseGroup = getData.getData("swupdate?tok=${token}&dev=${dev}&sw=False")!!
                                            }
                                            if(responseGroup == "ok"){
                                                runOnUiThread {
                                                    Toast.makeText(this, "群組中的裝置皆已關閉", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }.start()
                                    }
                                    groupCard.addView(btnTurnoff)
                                }
                                //宣告取消群組按鈕
                                val unGroup = ImageButton(this)
                                unGroup.setImageResource(R.drawable.cancel)
                                unGroup.scaleType = ImageView.ScaleType.FIT_CENTER
                                unGroup.layoutParams = LinearLayout.LayoutParams(120, 120)
                                val layoutParams = unGroup.layoutParams as LinearLayout.LayoutParams
                                layoutParams.marginStart = 650
                                unGroup.layoutParams = layoutParams
                                unGroup.setOnClickListener{
                                    val errorDialog = AlertDialog.Builder(this)
                                    errorDialog.setTitle("注意")
                                    errorDialog.setMessage("真的要刪除群組嗎?")
                                    errorDialog.setCancelable(true)
                                    errorDialog.setNegativeButton("取消"){_, _ ->}
                                    errorDialog.setPositiveButton("確定刪除"){_, _ ->
                                        Thread{
                                            val responseUnGroup = getData.getData("ungroup?tok=${token}&group=${groupCheck}")
                                            if(responseUnGroup == "ok"){
                                                val path = Paths.get("/data/data/com.example.gaxer/shared_prefs/${groupCheck}.xml")
                                                try {
                                                    val result = Files.deleteIfExists(path)
                                                    if (result){
                                                        runOnUiThread {
                                                            groupScroll.removeView(groupCard)
                                                            groupScroll.removeView(groupText)
                                                            Toast.makeText(this, "刪除群組成功", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    else{
                                                        runOnUiThread {
                                                            Toast.makeText(this, "刪除群組失敗", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                                catch (e:IOException){
                                                    runOnUiThread {
                                                        Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                            else{
                                                runOnUiThread {
                                                    Toast.makeText(this, "TimeOut", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }.start()
                                    }
                                    runOnUiThread {
                                        errorDialog.show()
                                    }
                                }
                                //把取消群組按鈕加進CardView
                                groupCard.addView(unGroup)
                                //把CardView加進去ScrollView
                                groupScroll.addView(groupCard)
                            }
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
    override fun onBackPressed() {
        //super.onBackPressed()
        //Log.d("BackPressed", "disable")
    }
}