package com.example.placereserve

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

class PlaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)

        if(intent.getStringExtra("place_name") != null) {
            Toast.makeText(this, intent.getStringExtra("place_name"), Toast.LENGTH_SHORT).show()
        }
    }
}