package com.example.placereserve

import android.content.Intent
import java.util.ArrayList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.places_item.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.placereserve.PlacesData.favoritePlacesList
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.favorite_place_item.view.*
import kotlinx.android.synthetic.main.history_place_item.view.*

class HistoryPlacesAdapter : RecyclerView.Adapter<HistoryPlacesAdapter.PlacesHolder>() {
    //создает ViewHolder и инициализирует views для списка

    var historyPlaces: MutableList<PlacesHistory> = mutableListOf()
    var SourceListHistory: MutableList<PlacesHistory> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
        return PlacesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.history_place_item, parent, false)
        )
    }

    fun filter() {
        historyPlaces.clear()
        historyPlaces.addAll(SourceListHistory)

        notifyDataSetChanged()
    }

    //связывает views с содержимым
    override fun onBindViewHolder(viewHolder: PlacesHolder, position: Int) {

        viewHolder.bind(historyPlaces[position])

        viewHolder.itemView.imageHistory.setOnClickListener {
            val intent: Intent = Intent(viewHolder.itemView.context, PlaceActivity::class.java)
            intent.putExtra("place_name", viewHolder.itemView.placesNameH.text)
            intent.putExtra("place_address", viewHolder.itemView.placesAddressH.text)
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = historyPlaces.size

    //передаем данные и оповещаем адаптер о необходимости обновления списка

    fun refreshHistoryPlaces(historyPlaces: List<PlacesHistory>) {
        SourceListHistory.clear()
        SourceListHistory.addAll(historyPlaces)
        filter()
        //this.places = places
        notifyDataSetChanged()
    }

    //внутренний класс ViewHolder описывает элементы представления списка и привязку их к RecyclerView
    class PlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(historyPlaces: PlacesHistory) = with(itemView) {
            placesNameH.text = historyPlaces.nameH
            placesAddressH.text = historyPlaces.addressH
            placesDateH.text = historyPlaces.dateH
            placesTimeH.text = historyPlaces.timeH
            imageHistory.setImageResource(historyPlaces.imageH)
        }
    }
}
