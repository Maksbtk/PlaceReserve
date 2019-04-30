package com.example.placereserve

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

object PlacesData{


    //
    //Русик,здеся надо закачивать заведения из бд
    //

    //val database = FirebaseDatabase.getInstance().reference

    fun getPlaces () :List<Places>{

        var PlacesList : MutableList<Places> =   mutableListOf()
        //val PlacesList : List<Places> = listOf()

        //val user = firebaseAuth.currentUser



//        database.child("Places").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//              //  nameUser.text = dataSnapshot.getValue().toString()
//               val placesList = (dataSnapshot.children.mapNotNull{it.getValue<Places>(Places::class.java)})
//                PlacesList=placesList.toMutableList()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//            }
//        })



//        PlacesList.add(Places("Йохан Пивохан","Проспект Кирова, 58"))
//        PlacesList.add(Places("Йохан Пивохан","Проспект Кирова, 58"))


        PlacesList.add(Places("Йохан Пивохан","Проспект Кирова, 58",R.drawable.r1))
        PlacesList.add(Places("Карл у клары","Проспект Кирова, 51Б",R.drawable.r2))
        PlacesList.add(Places("Гренки","Ленина проспект, 41 ",R.drawable.r1))
        PlacesList.add( Places("Maya Pizza","Иркутский тракт, 42",R.drawable.r2))

        return  PlacesList
    }




}