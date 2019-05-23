package com.example.placereserve

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class PlacesViewModel: ViewModel() {
    var placesList : MutableLiveData<List<Places>> = MutableLiveData()
    //инициализируем список и заполняем его данными пользователей
    init {
        placesList.value = PlacesData.getPlaces()
    }
    fun getListPlaces() = placesList



    var favoritePlacesList : MutableLiveData<List<PlacesFavorite>> = MutableLiveData()
    init {
        favoritePlacesList.value = PlacesData.getFavoritePlaces()
    }
    fun getListFavoritePlaces() = favoritePlacesList

    //для обновления списка передаем второй список пользователей
//    fun updateListPlaces() {
  //      PlacesList.value = PlacesData.getAnotherPlaces()
    // }

}