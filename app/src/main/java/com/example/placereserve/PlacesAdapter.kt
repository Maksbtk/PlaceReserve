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
import kotlinx.android.synthetic.main.activity_favoriteplaces.*
import kotlinx.android.synthetic.main.activity_main.view.*

class PlacesAdapter : RecyclerView.Adapter<PlacesAdapter.PlacesHolder>() {
    //создает ViewHolder и инициализирует views для списка

    val places: MutableList<Places> = mutableListOf()
    val SourceList: MutableList<Places> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
        return PlacesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.places_item, parent, false)
        )
    }

    fun filter(query: String) {
        places.clear()
        SourceList.forEach({
            if (it.name.toUpperCase().contains(query.toUpperCase()) || it.address.toUpperCase().contains((query.toUpperCase()))) {
                places.add(it)
            }
        })
        notifyDataSetChanged()
    }

    //связывает views с содержимым
    override fun onBindViewHolder(viewHolder: PlacesHolder, position: Int) {

        viewHolder.bind(places[position])

        viewHolder.itemView.image2.setOnClickListener {

            val intent: Intent = Intent(viewHolder.itemView.context, PlaceActivity::class.java)
            intent.putExtra("place_name", viewHolder.itemView.placesName.text)
            intent.putExtra("place_address", viewHolder.itemView.placesAdress.text)
            intent.putExtra("place_status", "1")
            viewHolder.itemView.context.startActivity(intent)


            //Toast.makeText(viewHolder.itemView.context, "Clicked item", Toast.LENGTH_SHORT).show()

        }
    }

    override fun getItemCount() = places.size

    //передаем данные и оповещаем адаптер о необходимости обновления списка
    fun refreshPlaces(places: List<Places>) {
        SourceList.clear()
        SourceList.addAll(places)
        filter("")
        //this.places = places
        notifyDataSetChanged()
    }

    //внутренний класс ViewHolder описывает элементы представления списка и привязку их к RecyclerView
    class PlacesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(place: Places) = with(itemView) {

            placesName.text = place.name
            placesAdress.text = place.address
            image2.setImageResource(place.image)

        }
    }
}
