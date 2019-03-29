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

import android.view.View
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

import java.util.Locale.filter
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class MainActivity : AppCompatActivity() {


    //инициализируем ViewModel ленивым способом
    private val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

        // слушатель на нажатиие всей области серчвью
        SearchId.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                SearchId.setIconified(false)
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
        val btnnvg :  BottomNavigationView = findViewById(R.id.Navigationb)
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

        save_userdata.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                layout_user.setVisibility(View.VISIBLE)
              //  layout_places.setVisibility(View.INVISIBLE)
                Navigationb.setVisibility(View.VISIBLE)
                layout_change.setVisibility(View.INVISIBLE)
            }
        })

        change_data.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                layout_user.setVisibility(View.INVISIBLE)
                layout_places.setVisibility(View.INVISIBLE)
                Navigationb.setVisibility(View.INVISIBLE)
                layout_change.setVisibility(View.VISIBLE)
            }
        })

    }


}


