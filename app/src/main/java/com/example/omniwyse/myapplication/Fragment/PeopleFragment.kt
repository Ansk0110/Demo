package com.example.omniwyse.myapplication.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.omniwyse.myapplication.AppConstants
import com.example.omniwyse.myapplication.ChatScreenActivity

import com.example.omniwyse.myapplication.R
import com.example.omniwyse.myapplication.Util.FirestoreUtil
import com.example.omniwyse.myapplication.recyclerview.item.PersonItem
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_people.*
import com.xwray.groupie.kotlinandroidextensions.Item
import org.jetbrains.anko.support.v4.startActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class PeopleFragment : Fragment() {

    private lateinit var userListenerRegistration: ListenerRegistration

    private  var shouldInitRecyclerView = true

    private  lateinit var peopleSection:Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        userListenerRegistration = FirestoreUtil.addUserListener(this.activity!!,this::updateRecyclerView)
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        FirestoreUtil.removeListener(userListenerRegistration)
        shouldInitRecyclerView = true

    }

    private fun updateRecyclerView(items: List<Item>){

        fun init(){
            recycler_view_people.apply {
                layoutManager = LinearLayoutManager(this@PeopleFragment.context)
                adapter = GroupAdapter<ViewHolder>().apply {
                    peopleSection = Section(items)
                    add(peopleSection)
                    setOnItemClickListener(onItemClick)
                }
            }
            shouldInitRecyclerView = false
        }

        fun updateItems() = peopleSection.update(items)

        if(shouldInitRecyclerView)
            init()
        else
            updateItems()

    }

    private val onItemClick = OnItemClickListener { item, view ->
        if(item is PersonItem){
            startActivity<ChatScreenActivity>(
                    AppConstants.USER_NAME to item.person.name,
                    AppConstants.USER_ID to item.userId
            )
        }
    }

}
