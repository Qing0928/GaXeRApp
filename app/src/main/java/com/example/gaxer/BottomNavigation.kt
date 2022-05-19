package com.example.gaxer

import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigation(bottomNav:BottomNavigationView){
    init {
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->{
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