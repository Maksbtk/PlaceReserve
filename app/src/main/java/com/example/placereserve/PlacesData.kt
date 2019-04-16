package com.example.placereserve

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.database.*

object PlacesData{


    //
    //Русик,здеся надо закачивать заведения из бд
    //

    fun getPlaces () :List<Places>{

        var PlacesList : MutableList<Places> =   mutableListOf()







        PlacesList.add(Places("Йохан Пивохан","Проспект Кирова, 58",R.drawable.r1))
        PlacesList.add(Places("Карл у клары","Проспект Кирова, 51Б",R.drawable.r2))
        PlacesList.add(Places("Гренки","Ленина проспект, 41 ",R.drawable.r1))
        PlacesList.add( Places("Maya Pizza","Иркутский тракт, 42",R.drawable.r2))

        return  PlacesList
    }




}