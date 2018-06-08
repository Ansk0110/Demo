package com.example.omniwyse.myapplication.Fragment


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.omniwyse.myapplication.ChatApp.ChatAppSignIn

import com.example.omniwyse.myapplication.R
import com.example.omniwyse.myapplication.Util.FirestoreUtil
import com.example.omniwyse.myapplication.Util.StorageUtil
import com.example.omniwyse.myapplication.glide.GlideApp
import com.firebase.ui.auth.AuthUI
//import com.firebase.ui.auth.data.model.User
import com.example.omniwyse.myapplication.model.User
import com.google.android.gms.auth.api.Auth
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MyAccount : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImageBytes: ByteArray
    private var pictureJustChanged = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_account, container, false)
        view.apply {
            imageView_profile_picture.setOnClickListener {
                val intent = Intent().apply{
                    type ="image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/jpeg"))
                }
                startActivityForResult(Intent.createChooser(intent,"Select Image"),RC_SELECT_IMAGE)
            }

            btn_save.setOnClickListener {
                if(::selectedImageBytes.isInitialized)
                    StorageUtil.uploadProfilePhoto(selectedImageBytes){
                        imagepath ->
                        FirestoreUtil.updateCurrentUser(editText_name.text.toString(),
                                                        editText_bio.text.toString(),
                                                        imagepath)
                    }
                else
                    FirestoreUtil.updateCurrentUser(editText_name.text.toString(),
                            editText_bio.text.toString(),
                            null)
                toast("Saving")
            }
            btn_sign_out.setOnClickListener{
                AuthUI.getInstance()
                        .signOut(this@MyAccount.context!!)
                        .addOnCompleteListener {
                            startActivity(intentFor<ChatAppSignIn>().newTask().clearTask())
                        }
            }
        }
        return  view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
                data != null && data.data != null){
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media
                    .getBitmap(activity?.contentResolver,selectedImagePath)
            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImageBytes = outputStream.toByteArray()
            GlideApp.with(this)
                    .load(selectedImageBytes)
                    .into(imageView_profile_picture)
            pictureJustChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser { user: User ->
            if (this@MyAccount.isVisible){
                editText_name.setText(user.name)
                editText_bio.setText(user.bio)
                if(!pictureJustChanged && user.profilepicturepath != null){
                    GlideApp.with(this)
                            .load(StorageUtil.pathToReference(user.profilepicturepath))
                            .placeholder(R.drawable.ic_account_circle_black_24dp)
                            .into(imageView_profile_picture)
                }
            }
        }
    }


}
