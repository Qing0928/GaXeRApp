package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.github.mikephil.charting.data.Entry
import org.json.JSONObject


class MainActivity : AppCompatActivity(){
    private lateinit var lineChart: ChartSetting
    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lineChart = ChartSetting(findViewById(R.id.lineChart), null)
        val getData = DataProcess()
        val pref = getSharedPreferences("info", 0)
        val token:String? = pref.getString("token", null)
        //接收上一個activity傳遞過來的資料
        //val token:String? = intent.getStringExtra("token")
        val devName:String? = intent.getStringExtra("devName")
        Log.d("mainActDevName", devName.toString())
        val xAxisDataLastActivity: ArrayList<Entry> = intent.getParcelableArrayListExtra<Entry>("xAxisData") as ArrayList<Entry>
        val xLabelLastActivity: ArrayList<String> = intent.getStringArrayListExtra("xLabel") as ArrayList<String>
        lineChart.updateData(xAxisDataLastActivity, xLabelLastActivity)

        //返回按鈕
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            val intent = Intent("android.intent.action.MAIN")
            intent.addCategory("android.intent.category.ALL")
            startActivity(intent)
        }
        //裝置名稱
        val textDevName = findViewById<TextView>(R.id.devName)
        textDevName.text = devName
        textDevName.textSize = 18f
        //常駐資料載入
        val nowTemp = findViewById<TextView>(R.id.nowTemp)
        val nowGas = findViewById<TextView>(R.id.nowGas)
        val nowBattery = findViewById<TextView>(R.id.nowBattery)
        val nowDevStatus = findViewById<ImageView>(R.id.imageDevStatus)
        val textDevStatus = findViewById<TextView>(R.id.textDevStatus)
        Thread{
            var url = "resident?tok=${token}&dev=${devName}"
            var response:String? = getData.getData(url)
            Log.d("mainAct", response.toString())
            if (response != null){
                val residentData = JSONObject(response)
                runOnUiThread{
                    nowTemp.text = residentData.getString("temp") + "℃"
                    nowBattery.text = residentData.getString("battery") + "%"
                    nowGas.text = residentData.getString("gas") + "ppm"
                }
            }
            //用圖標顯示裝置是否可用
            url ="safestatus?tok=${token}&dev=${devName}"
            response = getData.getData(url)
            if (response != null && response != "000000"){
                runOnUiThread{
                    textDevStatus.text = "裝置發生異常"
                    nowDevStatus.setImageResource(R.drawable.redlight)
                }
            }
            else{
                runOnUiThread {
                    textDevStatus.text = "目前一切正常"
                    nowDevStatus.setImageResource(R.drawable.greenlight)
                }
            }
        }.start()

        //更新圖表按鈕
        val btnRefresh = findViewById<ImageButton>(R.id.imageButtonRefresh)
        btnRefresh.setOnClickListener {
            Thread{
                val xAxisData = ArrayList<Entry>()
                var xLabel = ArrayList<String>()
                var remain = ArrayList<String>()
                xAxisData.clear()
                val url = "data/?tok=${token}&record=4&dev=${devName}"
                val response:String? = getData.getData(url)
                if(devName != null){
                    xLabel = getData.parseDataTime(response, devName)
                    remain = getData.parseDataRemaining(response, devName)
                }
                for ((xAxis, i) in remain.withIndex()){//(xAxis, i)>>(index, value)
                    xAxisData.add(Entry(xAxis.toFloat(), i.toFloat()))
                }
                lineChart.updateData(xAxisData, xLabel)
            }.start()
            Toast.makeText(this, "資料更新中", Toast.LENGTH_SHORT).show()
        }

        //開關操作
        val gasSwitch = findViewById<SwitchCompat>(R.id.gasSwitch)
        val errorDialog = AlertDialog.Builder(this)
        //先確定閥門狀態，設定使用者要看到的開關狀態
        Thread{
            val url = "swstatus?tok=${token}&dev=${devName}"
            val response:String? = getData.getData(url)
            if (response != null && response == "True"){
                runOnUiThread {
                    gasSwitch.isChecked = true
                }
            }
            else{
                runOnUiThread {
                    gasSwitch.isChecked = false
                }
            }
        }.start()

        //開關點擊監聽器
        gasSwitch.setOnCheckedChangeListener {_, isChecked ->
            //點擊是on
            var msgError = ""
            if(isChecked){
                Thread{
                    var url = "safestatus?tok=${token}&dev=${devName}"
                    val response:String? = getData.getData(url)
                    //確認狀態正常，開啟
                    if (response != null && response == "000000") {
                        //發出開啟訊號
                        url = "swupdate?tok=${token}&sw=True&dev=${devName}"
                        getData.getData((url))//更改開關狀態
                        runOnUiThread {
                            nowDevStatus.setImageResource(R.drawable.greenlight)
                            Toast.makeText(this, "閥門已經開啟", Toast.LENGTH_SHORT).show()
                        }
                    }
                    //狀態異常，關閉畫面上的開關
                    else{
                        if (response != null){
                            Log.d("status", response)
                            for ((index, value) in response.withIndex()) {
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
                            errorDialog.setTitle("警告")
                            errorDialog.setMessage(msgError)
                            errorDialog.setPositiveButton("確定"){_, _ ->
                                gasSwitch.isChecked = false
                            }
                            runOnUiThread {
                                errorDialog.show()
                            }
                        }
                        /*
                        when(response){
                            "0001" ->{
                                errorDialog.setTitle("警告")
                                errorDialog.setMessage("溫度異常")
                                errorDialog.setCancelable(false)
                                errorDialog.setPositiveButton("確定") {_,  _ ->
                                    gasSwitch.isChecked = false
                                }
                                runOnUiThread {
                                    errorDialog.show()
                                }
                            }
                            "0010" ->{
                                errorDialog.setTitle("警告")
                                errorDialog.setMessage("瓦斯異常")
                                errorDialog.setCancelable(false)
                                errorDialog.setPositiveButton("確定") {_,  _ ->
                                    gasSwitch.isChecked = false
                                }
                                runOnUiThread {
                                    errorDialog.show()
                                }
                            }
                            "0100" ->{
                                errorDialog.setTitle("警告")
                                errorDialog.setMessage("火焰異常")
                                errorDialog.setCancelable(false)
                                errorDialog.setPositiveButton("確定") {_,  _ ->
                                    gasSwitch.isChecked = false
                                }
                                runOnUiThread {
                                    errorDialog.show()
                                }
                            }
                            "1000" ->{
                                errorDialog.setTitle("警告")
                                errorDialog.setMessage("電量不足")
                                errorDialog.setCancelable(false)
                                errorDialog.setPositiveButton("確定") {_,  _ ->
                                    gasSwitch.isChecked = false
                                }
                                runOnUiThread {
                                    errorDialog.show()
                                }
                            }
                            "0011" ->{
                                errorDialog.setTitle("警告")
                                errorDialog.setMessage("異常搖晃")
                                errorDialog.setCancelable(false)
                                errorDialog.setPositiveButton("確定") {_,  _ ->
                                    gasSwitch.isChecked = false
                                }
                                runOnUiThread {
                                    errorDialog.show()
                                }
                            }
                        }
                        */
                        runOnUiThread {
                            nowDevStatus.setImageResource(R.drawable.redlight)
                            Toast.makeText(this, "請排除異常後再啟動裝置", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            //點擊是off
            else{
                Thread{
                    val url = "swupdate?tok=${token}&sw=False&dev=${devName}"
                    val response:String? = getData.getData(url)
                    if (response != null) {
                        Log.d("Switch", response)
                    }
                }.start()
                runOnUiThread {
                    Toast.makeText(this, "閥門已經關閉", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }
    override fun onBackPressed() {
        //super.onBackPressed()
    }
}