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
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.favorite_place_item.view.*

class FavoritePlacesAdapter : RecyclerView.Adapter<FavoritePlacesAdapter.PlacesHolder>() {
    //создает ViewHolder и инициализирует views для списка

    private var favoritePlaces:MutableList<PlacesFavorite> = mutableListOf()
    private var SourceListFavorite:MutableList<PlacesFavorite> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder {
        return PlacesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.favorite_place_item, parent, false)
        )
    }

    fun filter (query: String){
        favoritePlaces.clear()
        SourceListFavorite.forEach({
            if (it.nameF.toUpperCase().contains(query.toUpperCase()) || it.addressF.toUpperCase().contains((query.toUpperCase()))){
                favoritePlaces.add(it)
            }
        })
        notifyDataSetChanged()
    }

    //связывает views с содержимым
    override fun onBindViewHolder(viewHolder: PlacesHolder, position: Int) {

        viewHolder.bind(favoritePlaces[position])

        viewHolder.itemView.imageFavorite.setOnClickListener {

            val intent: Intent = Intent(viewHolder.itemView.context, PlaceActivity::class.java)
            intent.putExtra("place_name", viewHolder.itemView.placesNameF.text)
            intent.putExtra("place_address", viewHolder.itemView.placesAdressF.text)
            viewHolder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = favoritePlaces.size

    //передаем данные и оповещаем адаптер о необходимости обновления списка

    fun refreshFavoritePlaces(favoritePlaces: List<PlacesFavorite>) {
        SourceListFavorite.clear()
        SourceListFavorite.addAll(favoritePlaces)
        filter("")
        //this.places = places
        notifyDataSetChanged()
    }

    //внутренний класс ViewHolder описывает элементы представления списка и привязку их к RecyclerView
    class PlacesHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        fun bind(favoritePlaces: PlacesFavorite) = with(itemView) {
            placesNameF.text = favoritePlaces.nameF
            placesAdressF.text = favoritePlaces.addressF
            imageFavorite.setImageResource(favoritePlaces.imageF)
        }
    }
}
