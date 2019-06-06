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
import kotlinx.android.synthetic.main.activity_favoriteplaces.*
import kotlinx.android.synthetic.main.activity_history_places.*

class FavoritePlaces : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoriteplaces)

        val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
        val adapter = FavoritePlacesAdapter()
        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)

        favorite_places_list.layoutManager = LinearLayoutManager(this)
        favorite_places_list.adapter = adapter

        userViewModel.getListFavoritePlaces().observe(this, Observer {
            it?.let {
                adapter.refreshFavoritePlaces(it)
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
        val adapter = FavoritePlacesAdapter()

        favorite_places_list.layoutManager = LinearLayoutManager(this)
        favorite_places_list.adapter = adapter

        if (PlacesData.favoritePlacesList.size == 0) {
            informm.visibility = View.VISIBLE
            favorite_places_list.visibility = View.INVISIBLE
        } else {
            informm.visibility = View.INVISIBLE
            favorite_places_list.visibility = View.VISIBLE
        }

        userViewModel.getListFavoritePlaces().observe(this, Observer {
            it?.let {
                adapter.refreshFavoritePlaces(it)
                adapter.notifyDataSetChanged()
            }
        })
    }
}