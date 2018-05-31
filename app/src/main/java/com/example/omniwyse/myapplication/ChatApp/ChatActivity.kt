package com.example.omniwyse.myapplication.ChatApp

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.omniwyse.myapplication.R
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_people ->{
                    true
                }
                R.id.navigation_myaccount ->{
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}
