package com.example.gaxer

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
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
        lineChart = ChartSetting(findViewById(R.id.lineChart), null)
        val responseView = findViewById<TextView>(R.id.textView)
        val button = findViewById<Button>(R.id.button)
        val entries = ArrayList<Entry>()
        entries.add(Entry(0F, 4F))
        entries.add(Entry(1f, 1f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 4f))
        entries.add(Entry(5f, 10f))
        entries.add(Entry(5f, 10f))
        button.setOnClickListener() {
            Toast.makeText(this, "hello toast", Toast.LENGTH_SHORT).show()
            lineChart.updateData(entries)
        }
        val getData = HttpGet()
        val buttonGetRequest = findViewById<Button>(R.id.btn_get)
        buttonGetRequest.setOnClickListener{
            Thread{
                val url = "https://gaxer.ddns.net:443/data/?tok=123456abcd&record=6"
                val response:String? = getData.getData(url)
                val parseTest: MutableList<String> = getData.parseDataTime(response)
                val parseTestRemain: MutableList<String> = getData.parseDataRemaining(response)
            }.start()
        }
        /*
        buttonGetRequest.setOnClickListener() {
            Thread() {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://gaxer.ddns.net:443/data/?tok=123456abcd&record=1").build()
                val response = client.newCall(request).execute()
                val responseStr:String? = response.body()?.string()
                runOnUiThread { responseView.text = "Success" }
                if (responseStr != null) {
                    Log.d("OkHttpGET", responseStr)
                }
                val strArray: String = responseStr.toString()
                val jsonArray = JSONArray(strArray)
                val test: String = jsonArray.getString(0)
                Log.d("json", test)
                val json: String = test
                val jsonObject = JSONObject(json)
                val test2: String = jsonObject.getString("gas1")
                Log.d("json", test2)
            }.start()
        }
         */


        /*
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val entries = ArrayList<Entry>()
        entries.add(Entry(0F, 4F))
        entries.add(Entry(1f, 1f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 4f))
        entries.add(Entry(5f, 10f))
        val months:MutableList<String> = ArrayList()
        months.add("Jan")
        months.add("Feb")
        months.add("Mar")
        months.add("Apr")
        months.add("May")
        months.add("June")
        val dataset = LineDataSet(entries, "Test")
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        //val months = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June")

        val yAxisRight = lineChart.axisRight
        yAxisRight.setEnabled(false)

        val yAxisLeft = lineChart.axisLeft
        yAxisLeft.setGranularity(1f)

        // Setting Data
        val data = LineData(dataset)
        //lineChart.setData(data)
        lineChart.data = data
        lineChart.animateX(2500)
        //refresh
        lineChart.invalidate()
        */
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
