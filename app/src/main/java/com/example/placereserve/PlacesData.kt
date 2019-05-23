package com.example.placereserve

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object PlacesData{

    val favoritePlacesList : MutableList<PlacesFavorite> = mutableListOf()

    private lateinit var firebaseAuth: FirebaseAuth

    fun getData(str1: String, str2: String) {
        favoritePlacesList.add(PlacesFavorite(str1, str2, R.drawable.background_favorite_places))
    }

    fun getPlaces () :List<Places>{

        var PlacesList : MutableList<Places> =   mutableListOf()

        PlacesList.add(Places("Йохан Пивохан","Проспект Кирова, 58",R.drawable.r1))
        PlacesList.add(Places("Карл у Клары","Проспект Кирова, 51Б",R.drawable.r2))
        PlacesList.add(Places("Гренки","Ленина проспект, 41 ",R.drawable.r1))
        PlacesList.add( Places("Maya Pizza","Иркутский тракт, 42",R.drawable.r2))

        return  PlacesList
    }

    fun getFavoritePlaces () :List<PlacesFavorite>{
        val database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
//        if ( favoritePlacesList.size != 0) {
//            favoritePlacesList.clear()
//        }
        if (user != null) {
            val myRef = database.getReference("Пользователи").child(user.phoneNumber!!).child("ИзбранныеЗаведения")
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        var place = ds.key.toString()
                        var addressFav = ds.getValue().toString()
                        if ( !favoritePlacesList.contains(PlacesFavorite(place, addressFav, R.drawable.background_favorite_places))) {
                            getData(place, addressFav)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
        return  favoritePlacesList
    }
}