package com.example.omniwyse.myapplication.ChatApp

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.omniwyse.myapplication.Fragment.MyAccount
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
                    replaceFragment(MyAccount())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private  fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment_layout, fragment)
            commit()
        }
    }
}
