package com.example.gaxer

import android.annotation.SuppressLint
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

        //接收上一個activity傳遞過來的資料
        val token:String? = intent.getStringExtra("token")
        val xAxisDataLastActivity: ArrayList<Entry> = intent.getParcelableArrayListExtra<Entry>("xAxisData") as ArrayList<Entry>
        val xLabelLastActivity: ArrayList<String> = intent.getStringArrayListExtra("xLabel") as ArrayList<String>
        lineChart.updateData(xAxisDataLastActivity, xLabelLastActivity)

        //頁面常駐資料載入
        val nowTemp = findViewById<TextView>(R.id.nowTemp)
        val nowGas = findViewById<TextView>(R.id.nowGas)
        val nowBattery = findViewById<TextView>(R.id.nowBattery)
        val nowDevStatus = findViewById<ImageView>(R.id.imageDevStatus)
        Thread{
            var url = "resident?tok=${token}"
            var response:String? = getData.getData(url)
            if (response != null){
                val residentData = JSONObject(response)
                runOnUiThread{
                    nowTemp.text = residentData.getString("temp") + "℃"
                    nowBattery.text = residentData.getString("battery") + "%"
                    nowGas.text = residentData.getString("gas") + "ppm"
                }
            }
            //用圖標顯示裝置是否可用
            url ="safestatus?tok=${token}"
            response = getData.getData(url)
            if (response != null && response != "0000"){
                runOnUiThread{
                    nowDevStatus.setImageResource(R.drawable.redlight)
                }
            }
            else{
                runOnUiThread {
                    nowDevStatus.setImageResource(R.drawable.greenlight)
                }
            }
        }.start()

        //更新圖表按鈕
        val btnRefresh = findViewById<ImageButton>(R.id.imageButtonRefresh)
        btnRefresh.setOnClickListener {
            Thread{
                val xAxisData = ArrayList<Entry>()
                xAxisData.clear()
                val url = "data/?tok=${token}&record=4"
                val response:String? = getData.getData(url)
                val xLabel: ArrayList<String> = getData.parseDataTime(response)
                val remain: MutableList<String> = getData.parseDataRemaining(response)
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
            val url = "swstatus?tok=${token}"
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

        gasSwitch.setOnCheckedChangeListener {_, isChecked ->
            if(isChecked){
                Thread{
                    var url = "safestatus?tok=${token}"
                    var response:String? = getData.getData(url)
                    if (response != null && response == "0000") {
                        url = "swupdate?tok=${token}&sw=True"
                        response = getData.getData((url))
                        runOnUiThread {
                            nowDevStatus.setImageResource(R.drawable.greenlight)
                            Toast.makeText(this, "閥門已經開啟", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        if (response != null){
                            Log.d("status", response!!)
                        }
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
                                errorDialog.setMessage("電量")
                                errorDialog.setCancelable(false)
                                errorDialog.setPositiveButton("確定") {_,  _ ->
                                    gasSwitch.isChecked = false
                                }
                                runOnUiThread {
                                    errorDialog.show()
                                }
                            }
                        }
                        runOnUiThread {
                            nowDevStatus.setImageResource(R.drawable.redlight)
                            Toast.makeText(this, "請排除異常後再啟動裝置", Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
            else{
                Thread{
                    val url = "swupdate?tok=${token}&sw=False"
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
}