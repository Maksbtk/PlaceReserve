package com.example.placereserve

import android.content.DialogInterface
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.placereserve.PlacesData.historyPlacesList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.history_place_item.view.*


class HistoryPlacesAdapter : RecyclerView.Adapter<HistoryPlacesAdapter.PlacesHolder>() {
    //создает ViewHolder и инициализирует views для списка

    var historyPlaces: MutableList<PlacesHistory> = mutableListOf()
    var SourceListHistory: MutableList<PlacesHistory> = mutableListOf()
    lateinit var firebaseAuth: FirebaseAuth

    val database = FirebaseDatabase.getInstance()

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
        firebaseAuth = FirebaseAuth.getInstance()
        viewHolder.bind(historyPlaces[position])
        val user = firebaseAuth.currentUser

        viewHolder.itemView.button_remove_item_history.setOnClickListener {
            val animAlpha: Animation = AnimationUtils.loadAnimation(viewHolder.itemView.context, R.anim.alpha)
            it.startAnimation(animAlpha)


            // build alert dialog
            val dialogBuilder = AlertDialog.Builder(viewHolder.itemView.context)

            // set message of alert dialog
            dialogBuilder.setMessage("Это действие невозможно отменить")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Принять", DialogInterface.OnClickListener { dialog, id ->
                    val myr= database.getReference("Пользователи").child(user?.phoneNumber!!).child("Активные брони")
                        .child(historyPlaces[position].nameH).child(historyPlaces[position].addressH).child(historyPlaces[position].dateH)
                    myr.removeValue()
                    val myr1 =database.getReference("Заведения").child(historyPlaces[position].nameH)
                        .child("Столы").child("Номер стола")
                        .child(historyPlaces[position].table).child("Бронь").child("Дата").child(historyPlaces[position].dateH)
                    myr1.removeValue()
                    Snackbar.make(
                        it,
                        "Удалено",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    historyPlacesList.remove(PlacesHistory( historyPlaces[position].nameH,historyPlaces[position].addressH,historyPlaces[position].dateH
                        ,historyPlaces[position].timeH,R.drawable.background_history,historyPlaces[position].table))
                    refreshHistoryPlaces(PlacesData.getHistoryPlaces())

                })
                // negative button text and action
                .setNegativeButton("Отменить", DialogInterface.OnClickListener { dialog, id ->
                    dialog.cancel()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("Вы уверены что хотие отменить бронь?")
            // show alert dialog
            alert.show()


//               val myr= database.getReference("Пользователи").child(user?.phoneNumber!!).child("Активные брони")
//                    .child(historyPlaces[position].nameH).child(historyPlaces[position].addressH).child(historyPlaces[position].dateH)
//            myr.removeValue()
//                val myr1 =database.getReference("Заведения").child(historyPlaces[position].nameH)
//                    .child("Столы").child("Номер стола")
//                    .child(historyPlaces[position].table).child("Бронь").child("Дата").child(historyPlaces[position].dateH)
//                   myr1.removeValue()
//            Snackbar.make(
//               it,
//                "Удалено",
//                Snackbar.LENGTH_SHORT
//            ).show()
//            historyPlacesList.remove(PlacesHistory( historyPlaces[position].nameH,historyPlaces[position].addressH,historyPlaces[position].dateH
//                ,historyPlaces[position].timeH,R.drawable.background_history,historyPlaces[position].table))
//            refreshHistoryPlaces(PlacesData.getHistoryPlaces())

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
            var maskDateHis = historyPlaces.dateH.replace(' ', '/')
            placesDateH.text = maskDateHis
            placesTimeH.text = historyPlaces.timeH
            imageHistory.setImageResource(historyPlaces.imageH)
        }
    }
}
