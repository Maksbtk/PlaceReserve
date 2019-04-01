package com.example.placereserve

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

import java.util.Locale.filter
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {


    //инициализируем ViewModel ленивым способом
    private val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
    companion object {

        const val TOTAL_COUNT = "total_count"

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val animAlpha : Animation=AnimationUtils.loadAnimation(this, R.anim.alpha)
        val btnnvg :  BottomNavigationView = findViewById(R.id.Navigationb)


        val flag  = intent.getIntExtra(TOTAL_COUNT,1)
        when (flag) {
            1 -> {
                layout_user.setVisibility(View.INVISIBLE)
                layout_places.setVisibility(View.VISIBLE)
                btnnvg.menu.findItem(R.id.navigation_places).setEnabled(false)
                btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)


            }
            2 -> {

                layout_user.setVisibility(View.VISIBLE)
                layout_places.setVisibility(View.INVISIBLE)
                btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)
                btnnvg.menu.findItem(R.id.navigation_user).setEnabled(false)

            }
        }
        // BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        //инициализируем адаптер и присваиваем его списку
        val adapter = PlacesAdapter()


        placesList.layoutManager = LinearLayoutManager(this)
        placesList.adapter = adapter

        //подписываем адаптер на изменения списка
        userViewModel.getListPlaces().observe(this, Observer {
            it?.let {
                adapter.refreshPlaces(it)
            }
        })


        layout_places.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {


            }
        })
        // слушатель на нажатиие всей области серчвью
        SearchId.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                SearchId.setIconified(false)
               // Navigationb.setVisibility(View.INVISIBLE)

            }
        })



        //слушаетль на изменение поля ввода серчвью
        SearchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                //    Toast.makeText(this@MainActivity, "слушаетль работает", Toast.LENGTH_SHORT).show()
                adapter.filter(newText)

                // adapter.refreshPlaces()
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

        })
        //слушатель меню итемов юзер\плейсес

        btnnvg.setOnNavigationItemSelectedListener ( object : BottomNavigationView.OnNavigationItemSelectedListener {
           override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
               when (item.itemId) {
                   R.id.navigation_user -> {

                       layout_user.setVisibility(View.VISIBLE)
                       layout_places.setVisibility(View.INVISIBLE)
                       item.setEnabled(false)
                      btnnvg.menu.findItem(R.id.navigation_places).setEnabled(true)

                   }
                   R.id.navigation_places -> {
                       layout_user.setVisibility(View.INVISIBLE)
                       layout_places.setVisibility(View.VISIBLE)
                       item.setEnabled(false)

                       btnnvg.menu.findItem(R.id.navigation_user).setEnabled(true)


                   }
               }
            return false
            }
        })



        change_data.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val cda = Intent(this@MainActivity, Change_data_user::class.java)
                startActivity(cda)
            }
        })


    }


}


