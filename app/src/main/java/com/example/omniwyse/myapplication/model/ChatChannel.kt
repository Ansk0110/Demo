package com.example.omniwyse.myapplication.model

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}