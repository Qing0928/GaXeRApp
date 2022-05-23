package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.FormBody
import org.json.JSONArray
import org.json.JSONObject

class AddGroupActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_group)

        //val token:String? = intent.getStringExtra("token")
        val pref = getSharedPreferences("info", 0)
        val token:String? = pref.getString("token", null)
        val getData = DataProcess()

        val bottomNav = findViewById<BottomNavigationView>(R.id.navigationbottomView)
        bottomNav.setOnItemSelectedListener {
            val intent = Intent("android.intent.action.MAIN")
            when(it.itemId){
                R.id.home ->{
                    intent.addCategory("android.intent.category.ALL")
                    intent.putExtra("token", token)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                /*
                R.id.addDev ->{

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

        //確定註冊
        val btnRegister = findViewById<Button>(R.id.btn_group)
        btnRegister.setOnClickListener {
            val groupName = findViewById<EditText>(R.id.editTextGroupname)

            if (groupName.text.toString() == ""){
                val errorDialog = AlertDialog.Builder(this)
                errorDialog.setTitle("錯誤")
                errorDialog.setMessage("群組名稱未輸入")
                errorDialog.setCancelable(false)
                errorDialog.setPositiveButton("確定"){_, _ ->
                    //do nothing
                }
                runOnUiThread {
                    errorDialog.show()
                }
            }

            if (devCheckBoxCheck.isNotEmpty() && groupName.text.toString() != ""){
                Log.d("Check", devCheckBoxCheck.toString())
                Thread{
                    var dev = "["
                    for(i in devCheckBoxCheck){
                        //取得裝置的MAC以供儲存在資料庫
                        val prefDev = getSharedPreferences("dev", 0)
                        if (prefDev.getString(i, null) != null){
                            dev += "{\"${i}\":\"${prefDev.getString(i, null)}\"},"
                        }
                    }
                    dev += "]"
                    //製作Post表單
                    val dataBody:FormBody
                    if(token != null){
                        dataBody = FormBody.Builder()
                            .add("token", token)
                            .add("name", groupName.text.toString())
                            .add("dev", dev)
                            .build()
                        //發出註冊請求
                        val registerGroup = getData.postData("groupregister", dataBody)
                        if (registerGroup != null && registerGroup == "ok"){
                            runOnUiThread {
                                Toast.makeText(this,"註冊成功", Toast.LENGTH_SHORT).show()
                                val intent = Intent("android.intent.action.MAIN")
                                intent.addCategory("android.intent.category.ALL")
                                startActivity(intent)
                            }
                        }
                    }
                    val prefGroup = getSharedPreferences("group"+groupName.text.toString(), 0)
                    val editGroup = prefGroup.edit()
                    editGroup.putString("group", devCheckBoxCheck.toString()).apply()
                }.start()
            }

            if (devCheckBoxCheck.isEmpty()){
                val errorDialog = AlertDialog.Builder(this)
                errorDialog.setTitle("錯誤")
                errorDialog.setMessage("請至少選擇一個裝置")
                errorDialog.setCancelable(false)
                errorDialog.setPositiveButton("確定"){_, _ ->

                }
                runOnUiThread {
                    errorDialog.show()
                }
            }
        }
    }
}