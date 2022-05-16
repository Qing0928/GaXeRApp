package com.example.gaxer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
//棄用
class HomeFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        /*
        val xxx = root.findViewById<TextView>(R.id.textViewTest)
        xxx.text = "shit"
        */
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val xx = root.findViewById<FrameLayout>(R.id.container)
        val btnTest= Button(xx.context)
        btnTest.setOnClickListener {
            Toast.makeText(xx.context, "finally", Toast.LENGTH_SHORT).show()
        }
        btnTest.text = "test"
        //btnTest.layoutParams.height = 100
        //xx.addView(btnTest)
        val cardView = CardView(xx.context)
        xx.addView(cardView)
        return root
    }


}