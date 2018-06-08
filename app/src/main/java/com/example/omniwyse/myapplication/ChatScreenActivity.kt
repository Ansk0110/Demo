package com.example.omniwyse.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.omniwyse.myapplication.Util.FirestoreUtil
import com.example.omniwyse.myapplication.model.MessageType
import com.example.omniwyse.myapplication.model.TextMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.abc_tooltip.view.*
import kotlinx.android.synthetic.main.activity_chat_screen.*
import org.jetbrains.anko.linearLayout
import org.jetbrains.anko.toast
import java.util.*

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var messagesListenerRegistration: ListenerRegistration
    private  var shouldInitRecyclerView = true
    private lateinit var messagesSection: Section

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)

        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)
        FirestoreUtil.getOrCreateChatChannel(otherUserId){
            channelId ->

            messagesListenerRegistration=
                                FirestoreUtil.addChatMessageListner(channelId,this,this :: updateRecyclerView)

            imageView_send.setOnClickListener {
                val messageToSend = TextMessage(edit_text_message.text.toString(),Calendar.getInstance().time,
                                                FirebaseAuth.getInstance().currentUser!!.uid,MessageType.TEXT)
                edit_text_message.setText("")
                FirestoreUtil.sendMessage(messageToSend, channelId)
            }

            fab_send_image.setOnClickListener {
                //TODO: send image messages
            }
        }
    }

    private  fun updateRecyclerView(messages : List<Item>){
        fun init(){
            recycler_view_messages.apply {
                layoutManager = LinearLayoutManager(this@ChatScreenActivity)
                adapter = GroupAdapter<ViewHolder>().apply {
                    messagesSection = Section(messages)
                    this.add(messagesSection)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = messagesSection.update(messages)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()

        recycler_view_messages.scrollToPosition(recycler_view_messages.adapter.itemCount - 1)
    }
}
