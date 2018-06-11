package com.example.omniwyse.myapplication.model

data class User(val name : String,
                val bio : String,
                val profilepicturepath: String?,
                val registrationTokens: MutableList<String>) {
    constructor():this("","",null, mutableListOf())
}