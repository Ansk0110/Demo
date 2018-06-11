package com.example.omniwyse.myapplication.ChatApp

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.omniwyse.myapplication.R
import com.example.omniwyse.myapplication.Service.MyFirebaseInstanceIdService
import com.example.omniwyse.myapplication.Util.FirestoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_chat_app_sign_in.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask

class ChatAppSignIn : AppCompatActivity() {

    private  val Rc_signIn = 1

    private  val signInProviders =
            listOf(AuthUI.IdpConfig.EmailBuilder()
                    .setAllowNewAccounts(true)
                    .setRequireName(true)
                    .build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_app_sign_in)


        account_sign_in.setOnClickListener {

            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                    .setAvailableProviders(signInProviders)
                    .setLogo(R.drawable.ic_chat)
                    .build()

            startActivityForResult(intent,Rc_signIn)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Rc_signIn){
            val response = IdpResponse.fromResultIntent(data)

            if(resultCode == Activity.RESULT_OK){
                val progressDailog = indeterminateProgressDialog("Setting up your account")
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(intentFor<ChatActivity>().newTask().clearTask())

                    val registrationToken = FirebaseInstanceId.getInstance().token

                    MyFirebaseInstanceIdService.addTokenToFirestore(registrationToken)
                    progressDailog.dismiss()
                }
                startActivity(intentFor<ChatActivity>().newTask().clearTask())
                progressDailog.dismiss()
            }
            else if (resultCode == Activity.RESULT_CANCELED){
                if (response == null) return

                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK ->
                            longSnackbar(signin_layout,"No network")
                    ErrorCodes.UNKNOWN_ERROR ->
                        longSnackbar(signin_layout,"Unknown Error ")
                }
            }
        }

    }
}
