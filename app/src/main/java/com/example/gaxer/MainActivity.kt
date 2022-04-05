package com.example.gaxer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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

        //頁面常駐資料載入
        val nowTemp = findViewById<TextView>(R.id.nowTemp)
        val nowGas = findViewById<TextView>(R.id.nowGas)
        val nowBattery = findViewById<TextView>(R.id.nowBattery)
        Thread{
            val url = "https://gaxer.ddns.net/resident?tok=123456abcd"
            val response:String? = getData.getData(url)
            if (response != null){
                val residentData = JSONObject(response)
                runOnUiThread{
                    nowTemp.text = residentData.getString("temp") + "℃"
                    nowBattery.text = residentData.getString("battery") + "%"
                    nowGas.text = residentData.getString("gas") + "ppm"
                }
            }
        }.start()

        //接收上一個activity傳遞過來的資料
        val xAxisDataLastActivity: ArrayList<Entry> = intent.getParcelableArrayListExtra<Entry>("xAxisData") as ArrayList<Entry>
        val xLabelLastActivity: ArrayList<String> = intent.getStringArrayListExtra("xLabel") as ArrayList<String>
        lineChart.updateData(xAxisDataLastActivity, xLabelLastActivity)

        //更新圖表按鈕
        val btnGet = findViewById<Button>(R.id.btn_get)
        btnGet.setOnClickListener{
            Thread{
                val xAxisData = ArrayList<Entry>()
                xAxisData.clear()
                val url = "https://gaxer.ddns.net/data/?tok=123456abcd&record=4"
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
        gasSwitch.isChecked = true
        gasSwitch.setOnCheckedChangeListener {_, isChecked ->
            if(isChecked){
                Thread{
                    val url = "https://gaxer.ddns.net/swupdate?tok=123456abcd&sw=True"
                    val response:String? = getData.getData(url)
                    if (response != null) {
                        Log.d("Switch", response)
                    }
                }.start()
                Toast.makeText(this, "閥門已經開啟", Toast.LENGTH_SHORT).show()
            }
            else{
                Thread{
                    val url = "https://gaxer.ddns.net/swupdate?tok=123456abcd&sw=False"
                    val response:String? = getData.getData(url)
                    if (response != null) {
                        Log.d("Switch", response)
                    }
                }.start()
                Toast.makeText(this, "閥門已經關閉", Toast.LENGTH_SHORT).show()
            }
        }


    }
}