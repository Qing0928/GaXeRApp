package com.example.gaxer

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class FindDevActivity : AppCompatActivity() {
    private val REQUEST_ENABLE = 1
    private val adapterBT = BluetoothAdapter.getDefaultAdapter()
    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_dev)

        val pref = getSharedPreferences("info", 0)
        val token:String? = pref.getString("token", null)
        val bottomNav = findViewById<BottomNavigationView>(R.id.navigationbottomView)
        val intent = Intent("android.intent.action.MAIN")

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
                    intent.addCategory("android.intent.category.FINDDEV")
                    intent.putExtra("token", token)
                    startActivity(intent)
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

        if(adapterBT == null){
            Log.d("BLE", "BLE not support")
        }
        if (!adapterBT?.isEnabled!!){
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, REQUEST_ENABLE)
        }
        val pairedDdev = adapterBT.bondedDevices
        val scrollView = findViewById<LinearLayout>(R.id.ScrollLinear)
        if(pairedDdev.isNotEmpty()){
            for (dev in pairedDdev){
                val btDev = TextView(this)
                btDev.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                btDev.text = "${dev.name}\n${dev.address}"
                btDev.textSize = 20F
                btDev.setOnClickListener{
                    intent.addCategory("android.intent.category.SETTINGDEV")
                    intent.putExtra("address", dev.address)
                    startActivity(intent)
                }
                scrollView.addView(btDev)
                Log.d("Name", dev.name)
                Log.d("Mac", dev.address)
            }
        }
        else{
            val btDev = TextView(this)
            btDev.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            btDev.text = "no paired device"
            btDev.textSize = 20F
            scrollView.addView(btDev)
            Log.d("BLE", "no paired device")
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE){
            when(requestCode){
                RESULT_OK ->{
                    if (adapterBT?.isEnabled!!){
                        Toast.makeText(this, "藍芽已經開啟", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this, "藍芽已經關閉", Toast.LENGTH_SHORT).show()
                    }
                }
                RESULT_CANCELED ->{
                    Toast.makeText(this, "藍芽開啟失敗", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}