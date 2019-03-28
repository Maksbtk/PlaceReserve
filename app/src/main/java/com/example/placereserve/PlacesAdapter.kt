package com.example.placereserve
import java.util.ArrayList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.places_item.view.*


class PlacesAdapter : RecyclerView.Adapter<PlacesAdapter.PlacesHolder>() {
    //создает ViewHolder и инициализирует views для списка

    private var places:MutableList<Places> =   mutableListOf()
    private var SourceList:MutableList<Places> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
        return PlacesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.places_item, parent, false)
        )
    }
    fun filter (query: String){
        places.clear()
    SourceList.forEach({
        if (it.name.toUpperCase().contains(query.toUpperCase())||it.address.toUpperCase().contains((query.toUpperCase()))){
        places.add(it)
    }
    })
        notifyDataSetChanged()
    }
    //связывает views с содержимым
    override fun onBindViewHolder(viewHolder: PlacesHolder, position: Int) {
        viewHolder.bind(places[position])

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
    class PlacesHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(place: Places) = with(itemView) {

            placesName.text = place.name
            placesAdress.text = place.address
            image2.setImageResource(place.image)
        }
    }
}
