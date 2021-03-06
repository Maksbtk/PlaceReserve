package com.example.placereserve

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.arch.lifecycle.Observer
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.ALPHA_CHANGED
import android.content.Intent
import com.example.placereserve.PlacesData.historyPlacesList
import kotlinx.android.synthetic.main.activity_changedata.back_in_useer
import kotlinx.android.synthetic.main.activity_history_places.*
import android.view.WindowManager.LayoutParams.ANIMATION_CHANGED as ANIMATION_CHANGED1

class HistoryPlaces : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_places)

        val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
        val adapter = HistoryPlacesAdapter(this)
        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)

        history_places_list.layoutManager = LinearLayoutManager(this)
        history_places_list.adapter = adapter

        userViewModel.getListHistoryPlaces().observe(this, Observer {
            it?.let {
                adapter.refreshHistoryPlaces(it)
                adapter.notifyDataSetChanged()
            }
        })

        back_in_useer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                finish()
            }
        })
        CheckListOnZeroCount()
    }


//    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams?) {
//        super.onWindowAttributesChanged(params)
//        when (params) {
//            ALPHA_CHANGED -> {
//                CheckListOnZeroCount()
//            }
//        }
//    }



    fun CheckListOnZeroCount (){
        if (historyPlacesList.size==0){
            inform.visibility = View.VISIBLE
            history_places_list.visibility = View.INVISIBLE
        }
        else{
            inform.visibility = View.INVISIBLE
            history_places_list.visibility = View.VISIBLE
        }
    }
    override fun onStart() {
        super.onStart()

        val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
        val adapter = HistoryPlacesAdapter(this)

        history_places_list.layoutManager = LinearLayoutManager(this)
        history_places_list.adapter = adapter

        CheckListOnZeroCount()
        userViewModel.getListHistoryPlaces().observe(this, Observer {
            it?.let {
                adapter.refreshHistoryPlaces(it)
                adapter.notifyDataSetChanged()
            }
        })
    }
}