package com.example.gaxer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.security.MessageDigest
import okhttp3.FormBody

class LoginActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val account = findViewById<EditText>(R.id.editTextTextPersonName)
        val psd = findViewById<EditText>(R.id.editTextTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val getData = DataProcess()
        val pref = getSharedPreferences("info", 0)
        val editor = pref.edit()
        val intent = Intent("android.intent.action.MAIN")

        btnLogin.setOnClickListener {
            if(account.text.toString() == "" || psd.text.toString() == ""){
                Toast.makeText(this, "尚未輸入資料", Toast.LENGTH_SHORT).show()
            }
            else{
                //hash password
                val hashPsd = MessageDigest.getInstance("SHA-256")
                    .digest(psd.text.toString().toByteArray())
                    .fold(""){ str, i -> //str->初始值，i->each one of ByteArray
                        str + "%02x".format(i)
                        //把psd.text轉成ByteArray，雜湊過後以空字串""為開頭，把ByteArray裡面的每個值逐個拿出來轉成hex組起來
                    }.uppercase()
                //Log.d("sha256", hashPsd)
                //get user token
                Thread{
                    val url = "signin"
                    val dataBody = FormBody.Builder()
                        .add("acc", account.text.toString())
                        .add("ps", hashPsd)
                        .build()
                    val response:String? = getData.postData(url, dataBody)
                    if (response != null) {
                        Log.d("response", response)
                    }
                    editor.putString("account", account.text.toString()).apply()
                    editor.putString("token", response).apply()
                    val test:String? = pref.getString("token", "")
                    if (test != null) {
                        Log.d("pref", test)
                    }
                    else{
                        Log.d("pref", "false")
                    }
                    intent.addCategory("android.intent.category.START")
                    startActivity(intent)
                }.start()
            }
        }
    }
}