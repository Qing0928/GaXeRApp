package com.example.gaxer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.FormBody
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
        val devCheckBoxCheck = mutableSetOf<String>()
        Thread{
            val response:String? = getData.getData("devlist?tok=${token}")
            if(response != null){
                val devListObj = JSONObject(response).getString("devList")
                devList = JSONArray(devListObj)
                Thread{
                    for(i in 0 until devList.length()){
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
                                devCheckBox.tag = devList[i]
                                //對CheckBox設定監聽器，根據點擊時的狀態來決定刪除或增加set中的元素
                                devCheckBox.setOnClickListener{
                                    if(devCheckBox.isChecked){
                                        if(!devCheckBoxCheck.contains(devCheckBox.tag)){
                                            devCheckBoxCheck.add(devCheckBox.tag.toString())
                                        }
                                    }
                                    if (!devCheckBox.isChecked){
                                        if (devCheckBoxCheck.contains(devCheckBox.tag)){
                                            devCheckBoxCheck.remove(devCheckBox.tag)
                                        }
                                    }
                                }
                                scrollLinear.addView(devCheckBox)
                            }
                        }
                    }
                }.start()
            }
        }.start()
        val btnRegister = findViewById<Button>(R.id.btn_group)
        btnRegister.setOnClickListener {
            val groupName = findViewById<EditText>(R.id.editTextGroupname)
            if (groupName.text.toString() == ""){
                Log.d("groupName", "fail")
            }
            if (devCheckBoxCheck.isNotEmpty()){
                Thread{
                    var dev = "["
                    for(i in devCheckBoxCheck){
                        val pref = getSharedPreferences("dev", 0)
                        if (pref.getString(i, null) != null){
                            dev += "{\"${i}\":\"${pref.getString(i, null)}\"},"
                        }
                    }
                    dev += "]"
                    val dataBody = FormBody.Builder()
                        .add("token", token)
                        .add("name", groupName.text.toString())
                        .add("dev", dev)
                        .build()
                    val response:String? = getData.postData("groupregister", dataBody)
                    if(response != null){
                        Log.d("groupRegister", response)
                    }
                }.start()
                Log.d("groupName", groupName.text.toString())
            }
            else{
                Log.d("groupRegister", "nothing be selected")
            }
            Log.d("Check", devCheckBoxCheck.toString())
        }
    }
}