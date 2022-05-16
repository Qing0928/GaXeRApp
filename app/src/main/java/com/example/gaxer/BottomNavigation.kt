package com.example.gaxer

import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigation(private val bottomNav:BottomNavigationView){
    init {
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
                    Log.d("Navigation", "click")
                    return@setOnItemSelectedListener true
                }
                R.id.addDev ->{
                    return@setOnItemSelectedListener true
                }
                R.id.setting->{
                    return@setOnItemSelectedListener true
                }
                else -> return@setOnItemSelectedListener false
            }
        }
    }
}