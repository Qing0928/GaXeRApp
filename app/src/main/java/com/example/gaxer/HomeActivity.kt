package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.data.Entry
import org.json.JSONArray
import org.json.JSONObject


class HomeActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val token:String? = intent.getStringExtra("token")

        val intent = Intent("android.intent.action.MAIN")
        intent.addCategory("android.intent.category.MAINACTIVITY")

        Log.d("home", token.toString())

        BottomNavigation(findViewById(R.id.navigationbottomView))
        val getData = DataProcess()

        //loadFragment(HomeFragment())
        val horizontalScroll = findViewById<LinearLayout>(R.id.HorizontalLinear)
        var devList:JSONArray
        Thread{
            var response:String? = getData.getData("devlist?tok=${token}")
            Log.d("maybe", "ok")
            Log.d("response", response.toString())
            if (response!=null){
                val devListObj = JSONObject(response).getString("devList")
                devList = JSONArray(devListObj)

                //val devList = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)
                //var leftPosition = 0
                for(i in 0 until devList.length()){
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
                        Log.d("devList", devList[i].toString())
                    }

                }

            }
        }.start()
        /*
        val testButton = ImageButton(this)
        testButton.setImageResource(R.drawable.battery)
        testButton.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 200)
        val layoutParams = testButton.layoutParams as FrameLayout.LayoutParams
        //layoutParams.setMargins(0, 100, 0, 0)
        //layoutParams.width = 300
        layoutParams.topMargin = 500
        layoutParams.height = 400
        testButton.layoutParams = layoutParams
        testButton.id = 123
        testButton.tag = "123"
        Log.d("idTest", testButton.id.toString())
        container.addView(testButton)

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