package com.example.gaxer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    lateinit var navigationbar:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        loadFragment(HomeFragment())
        navigationbar = findViewById(R.id.navigationbottomView)
        navigationbar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> {
                    loadFragment(HomeFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.addDev ->{
                    loadFragment(AddDevFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.setting->{
                    loadFragment(SettingFragment())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }
    private fun loadFragment(fragment:Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}