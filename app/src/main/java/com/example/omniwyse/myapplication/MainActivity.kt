package com.example.omniwyse.myapplication

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.omniwyse.myapplication.ChatApp.SplashActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatbtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this,SplashActivity::class.java)
            startActivity(intent)
        })
    }
}
