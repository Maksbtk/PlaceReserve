package com.example.placereserve

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object PlacesData {

    val favoritePlacesList: MutableList<PlacesFavorite> = mutableListOf()
    val historyPlacesList: MutableList<PlacesHistory> = mutableListOf()
    val PlacesList: MutableList<Places> = mutableListOf()

    private lateinit var firebaseAuth: FirebaseAuth

    fun getFavoriteData(name: String, address: String) {
        favoritePlacesList.add(PlacesFavorite(name, address, R.drawable.background_favorite_places))
    }

    fun getHistoryData(name: String, address: String, date: String, time: String, table: String) {
        historyPlacesList.add(PlacesHistory(name, address, date, time, R.drawable.background_history, table))
    }

    fun getPlaces(): List<Places> {

        PlacesList.add(Places("Йохан Пивохан", "Проспект Кирова, 58", R.drawable.r1))
        PlacesList.add(Places("Карл у Клары", "Проспект Кирова, 51Б", R.drawable.r2))
        PlacesList.add(Places("Гренки", "Ленина проспект, 41 ", R.drawable.r1))
        PlacesList.add(Places("Maya Pizza", "Иркутский тракт, 42", R.drawable.r2))

        return PlacesList
    }

    fun getFavoritePlaces(): List<PlacesFavorite> {
        val database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        if (user != null) {
            val myRef = database.getReference("Пользователи").child(user.phoneNumber!!).child("ИзбранныеЗаведения")
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        var place = ds.key.toString()
                        var addressFav = ds.getValue().toString()
                        if (!favoritePlacesList.contains(
                                PlacesFavorite(
                                    place,
                                    addressFav,
                                    R.drawable.background_favorite_places
                                )
                            )
                        ) {
                            getFavoriteData(place, addressFav)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
        return favoritePlacesList
    }

    fun getHistoryPlaces(): List<PlacesHistory> {
        val database = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        if (user != null) {
            val myRef = database.getReference("Пользователи")
                .child(user.phoneNumber!!)
                .child("Активные брони")
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds_1 in dataSnapshot.children) {
                        var placeHis = ds_1.key.toString()
                        val myRef_1 = database.getReference("Пользователи")
                            .child(user.phoneNumber!!)
                            .child("Активные брони").child(placeHis)
                        myRef_1.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (ds_2 in dataSnapshot.children) {
                                    var addressHis = ds_2.key.toString()
                                    val myRef_2 = database.getReference("Пользователи")
                                        .child(user.phoneNumber!!)
                                        .child("Активные брони").child(placeHis).child(addressHis)
                                    myRef_2.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            for (ds_3 in dataSnapshot.children) {
                                                var dateHis = ds_3.key.toString()
                                                var timeHis = ds_3.child("Время").getValue().toString()
                                                var tableNumber = ds_3.child("НомерСтола").getValue().toString()

                                                if (!historyPlacesList.contains(
                                                        PlacesHistory(
                                                            placeHis,
                                                            addressHis,
                                                            dateHis,
                                                            timeHis,
                                                            R.drawable.background_history,
                                                            tableNumber
                                                        )
                                                    )
                                                ) {
                                                    getHistoryData(placeHis, addressHis, dateHis, timeHis, tableNumber)
                                                }
                                            }
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
        }
        return historyPlacesList
    }
}