package com.example.omniwyse.myapplication.recyclerview.item

import android.content.ClipData
import android.content.Context
import com.example.omniwyse.myapplication.R
import com.example.omniwyse.myapplication.Util.StorageUtil
import com.example.omniwyse.myapplication.glide.GlideApp
import com.example.omniwyse.myapplication.model.User
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_person.*
import kotlinx.android.synthetic.main.item_person.view.*


class PersonItem(val person: User,
                val userId:String,
                private val context: Context): Item(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView_name.text = person.name
        viewHolder.textView_bio.text = person.bio
        if(person.profilepicturepath != null){
            GlideApp.with(context)
                    .load(StorageUtil.pathToReference(person.profilepicturepath))
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .into(viewHolder.imageView_profile_picture)
        }
    }

    override fun getLayout() = R.layout.item_person
}