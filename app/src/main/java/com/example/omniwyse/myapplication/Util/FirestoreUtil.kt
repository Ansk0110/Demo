package com.example.omniwyse.myapplication.Util

//import com.firebase.ui.auth.data.model.User
import android.content.Context
import android.util.Log
import com.example.omniwyse.myapplication.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.example.omniwyse.myapplication.recyclerview.item.PersonItem
import com.example.omniwyse.myapplication.recyclerview.item.TextMessageItem
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item

object FirestoreUtil {
    private  val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
                get() = firestoreInstance.document("users/${FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("UID is null.")}")

    private val chatChannelIsCollectionRef = firestoreInstance.collection("chatChannels")

    fun initCurrentUserIfFirstTime(onComplete:() -> Unit){
        currentUserDocRef.get().addOnSuccessListener { documentSnapshot ->
            if(!documentSnapshot.exists()){
                val newUser = com.example.omniwyse.myapplication.model.User(FirebaseAuth.getInstance().currentUser?.displayName ?: "",
                                    "",null)
                currentUserDocRef.set(newUser).addOnSuccessListener {
                    onComplete()
                }
            }
            else
                onComplete()
        }
    }

    fun  updateCurrentUser(name:String = "",bio:String ="",profilePicturePath:String? = null){
        val userFieldMap = mutableMapOf<String,Any>()
        if(name.isNotBlank())userFieldMap["name"] = name
        if(bio.isNotBlank()) userFieldMap["bio"] = bio
        if(profilePicturePath != null)
            userFieldMap["profilePicturePath"] = profilePicturePath
        currentUserDocRef.update(userFieldMap)
    }

    fun getCurrentUser(onComplete: (User) -> Unit){
        currentUserDocRef.get().addOnSuccessListener {
            onComplete(it.toObject(User::class.java)!!)

        }
    }

    fun addUserListener(context: Context,onListen: (List<Item>)  -> Unit): ListenerRegistration{
        return  firestoreInstance.collection("users")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        Log.e("FIRESTORE","Users listener error.",firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    if (querySnapshot != null) {
                        querySnapshot.documents.forEach {
                            if(it.id != FirebaseAuth.getInstance().currentUser?.uid)
                                items.add(PersonItem(it.toObject(User ::class.java)!!,it.id,context))
                        }
                    }
                    onListen(items)
                }
    }

    fun removeListener(registration: ListenerRegistration) = registration.remove()

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {
        currentUserDocRef.collection("engagedChatChannels")
                .document(otherUserId).get().addOnSuccessListener {
                    if(it.exists()){
                        onComplete(it["channelId"] as @kotlin.ParameterName(name = "channelId") String)
                        return@addOnSuccessListener
                    }
                    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                    val newChannel = chatChannelIsCollectionRef.document()
                    newChannel.set(ChatChannel(mutableListOf(currentUserId,otherUserId)))

                    currentUserDocRef
                            .collection("engagedChatChannels")
                            .document(otherUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    firestoreInstance.collection("users").document(otherUserId)
                            .collection("engagedChatChannels")
                            .document(currentUserId)
                            .set(mapOf("channelId" to newChannel.id))

                    onComplete(newChannel.id)
                }
    }

    fun addChatMessageListner(channelId: String, context: Context,
                              onListen: (List<Item>) -> Unit):ListenerRegistration {
        return chatChannelIsCollectionRef.document(channelId).collection("messages")
                .orderBy("time")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException != null){
                        Log.e("FIRESTORE","ChatMessageListner error.",firebaseFirestoreException)
                        return@addSnapshotListener
                    }

                    val items = mutableListOf<Item>()
                    if (querySnapshot != null) {
                        querySnapshot.documents.forEach {
                            if(it["type"] == MessageType.TEXT){
                                items.add(TextMessageItem(it.toObject(TextMessage::class.java)!!,context))
                            }
                            else
                                TODO("Addd image message ")

                        }
                        onListen(items)
                    }
                }
    }

    fun sendMessage(message: Message,channelId: String){
        chatChannelIsCollectionRef.document(channelId)
                .collection("messages")
                .add(message)
    }

}