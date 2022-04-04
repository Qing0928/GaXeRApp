package com.example.gaxer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import org.json.JSONObject

/*
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
*/

/*
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.ValueFormatter
 */


class MainActivity : AppCompatActivity(){
    private lateinit var lineChart: ChartSetting
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener() {
            Toast.makeText(this, "hello toast", Toast.LENGTH_SHORT).show()
        }

        lineChart = ChartSetting(findViewById(R.id.lineChart), null)

        val getData = DataProcess()
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
        val txAxisData: ArrayList<Entry> = intent.getParcelableArrayListExtra<Entry>("xAxisData") as ArrayList<Entry>
        val txLabel: ArrayList<String> = intent.getStringArrayListExtra("xLabel") as ArrayList<String>

        lineChart.updateData(txAxisData, txLabel)
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
        }

        /*
        val pieChart = findViewById<PieChart>(R.id.pieChart)
        pieChart.setNoDataText("老哥，我還沒吃飯呢，快給我數據")
        val pieEntry1 = PieEntry(100f)
        val pieEntry2 = PieEntry(300f)
        val pieEntry3 = PieEntry(500f)
        val list = mutableListOf(pieEntry1, pieEntry2, pieEntry3)
        val pieDataSet = PieDataSet(list, "用戶量")
        val pieData = PieData(pieDataSet!!)
        pieChart.data = pieData
        pieChart.invalidate()
        */
    }
    /*
    override fun onClick(view: View?){
        if (view is Button){
            when(view.id){
                R.id.button->
                    Toast.makeText(this, "hello world", Toast.LENGTH_SHORT).show()
                R.id.btn_get->
                    Toast.makeText(this, "get succes", Toast.LENGTH_SHORT).show()
            }
        }
    }*/
}