package com.example.gaxer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONObject

class AddGroupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        val token:String? = intent.getStringExtra("token")
        val getData = DataProcess()

        val bottomNav = findViewById<BottomNavigationView>(R.id.navigationbottomView)
        bottomNav.setOnItemSelectedListener {
            val intent = Intent("android.intent.action.MAIN")
            when(it.itemId){
                R.id.home ->{
                    //intent.removeCategory("android.intent.category.ADDGROUP")
                    intent.addCategory("android.intent.category.ALL")
                    intent.putExtra("token", token)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.addDev ->{
                    //intent.removeCategory("android.intent.category.ALL")
                    intent.addCategory("android.intent.category.ADDGROUP")
                    intent.putExtra("token", token)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                R.id.setting->{
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }

        val scrollLinear = findViewById<LinearLayout>(R.id.ScrollLinear)
        var devList: JSONArray
        Thread{
            val response:String? = getData.getData("devlist?tok=${token}")
            if(response != null){
                val devListObj = JSONObject(response).getString("devList")
                devList = JSONArray(devListObj)
                for(i in 0 until devList.length()){
                    Thread{
                        val groupCheck:String? = getData.getData("groupcheck?tok=${token}&dev=${devList[i]}")
                        if(groupCheck != null && groupCheck=="nan"){
                            runOnUiThread {
                                val devCheckBox = CheckBox(this)
                                devCheckBox.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 150)
                                val layoutParams = devCheckBox.layoutParams as LinearLayout.LayoutParams
                                layoutParams.marginStart = 50
                                devCheckBox.layoutParams = layoutParams
                                devCheckBox.text = devList[i].toString()
                                devCheckBox.textSize = 20F
                                scrollLinear.addView(devCheckBox)
                            }
                        }
                    }.start()
                }
            }
        }.start()
    }
}