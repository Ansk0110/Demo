package com.example.omniwyse.myapplication.ChatApp

import android.annotation.SuppressLint
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.example.omniwyse.myapplication.Fragment.MyAccount
import com.example.omniwyse.myapplication.Fragment.PeopleFragment
import com.example.omniwyse.myapplication.R
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_people.*

class ChatActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        replaceFragment(PeopleFragment())

        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_people ->{
                    replaceFragment(PeopleFragment())
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

    private  fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment)
                .commit()
    }
}
