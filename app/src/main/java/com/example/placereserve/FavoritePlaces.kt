package com.example.placereserve

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_changedata.*
import kotlinx.android.synthetic.main.activity_main.*
import android.arch.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_changedata.back_in_useer
import kotlinx.android.synthetic.main.activity_favoriteplaces.*

class FavoritePlaces : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favoriteplaces)

        val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }

        val adapter = FavoritePlacesAdapter()
        favorite_places_list.layoutManager = LinearLayoutManager(this)
        favorite_places_list.adapter = adapter

        //подписываем адаптер на изменения списка
        userViewModel.getListFavoritePlaces().observe(this, Observer {
            it?.let {
                adapter.refreshFavoritePlaces(it)
            }
        })

        val animAlpha : Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)

        back_in_useer.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                finish()
            }
        })
    }
}