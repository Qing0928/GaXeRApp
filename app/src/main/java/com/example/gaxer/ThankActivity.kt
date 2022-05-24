package com.example.gaxer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class ThankActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank)

        val pref = getSharedPreferences("info", 0)
        val token:String? = pref.getString("token", null)
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

                R.id.addDev ->{
                    runOnUiThread {
                        Toast.makeText(this, "已經在鳴謝囉", Toast.LENGTH_SHORT).show()
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.setting->{
                    intent.addCategory("android.intent.category.ADDGROUP")
                    intent.putExtra("token", token)
                    startActivity(intent)
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }
}