package com.example.placereserve

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.arch.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_changedata.back_in_useer
import kotlinx.android.synthetic.main.activity_history_places.*

class HistoryPlaces : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_places)

        val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
        val adapter = HistoryPlacesAdapter()
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
    }

    override fun onStart() {
        super.onStart()

        val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
        val adapter = HistoryPlacesAdapter()

        history_places_list.layoutManager = LinearLayoutManager(this)
        history_places_list.adapter = adapter

        userViewModel.getListHistoryPlaces().observe(this, Observer {
            it?.let {
                adapter.refreshHistoryPlaces(it)
                adapter.notifyDataSetChanged()
            }
        })
    }
}