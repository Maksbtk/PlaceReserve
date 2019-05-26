package com.example.placereserve

import android.graphics.PointF
import android.util.Log
import android.widget.Toast
import com.example.placereserve.TableIconsCache.Companion.busyIconBmp
import com.example.placereserve.TableIconsCache.Companion.choosedIconBmp
import com.example.placereserve.TableIconsCache.Companion.freeIconBmp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.onlylemi.mapview.library.MapViewListener
import com.onlylemi.mapview.library.layer.BitmapLayer
import kotlinx.android.synthetic.main.activity_place.*
import com.google.firebase.database.DataSnapshot
import com.onlylemi.mapview.library.MapView


class CustomMapViewListener(private var placeActivity: PlaceActivity, private var currentMap: MapView) :
    MapViewListener {
    var isLoader: Boolean = false
    private val database = FirebaseDatabase.getInstance()
    private val database2 = FirebaseDatabase.getInstance().reference

    var bitmapChoosed: BitmapLayer? = null
    var choosedTableNumber = 0
    var checkDateReservationUser = ""
//    lateinit var firebaseAuth: FirebaseAuth
//    val user = firebaseAuth.currentUser
    val user = placeActivity.firebaseAuth.currentUser

    private var TAG: String = "CustomMapViewListener"

    override fun onMapLoadSuccess() {
        isLoader = true


        val myRef = database.getReference("Заведения").child(placeActivity.intent.getStringExtra("place_name")).child("Столы")
        createTables(myRef)
    }

    override fun onMapLoadFail() {
        Log.e(TAG, "Ah shit, here we go again")
    }

    fun drawDick(x:Float, y:Float, id:Int, reserved: Boolean) {
        if (!isLoader) return
        var bitmapLayer = BitmapLayer(currentMap, freeIconBmp)
        if (reserved)
            bitmapLayer!!.bitmap = busyIconBmp
        bitmapLayer!!.location = PointF(x, y)
        bitmapLayer!!.isAutoScale = true
//        checkDateReservationUser()
        if (bitmapLayer.bitmap != busyIconBmp){
            if(checkDateReservationUser.isEmpty() ){

            bitmapLayer!!.setOnBitmapClickListener {
                // Вешаем на кнопку слушатель кликбейта за сто морей


                var tagValue = PlaceActivity.SELECTED
                val choosedId = id

                if (bitmapChoosed != null) { // Если предыдущий стол выбран
                    if (bitmapChoosed!!.equals(bitmapLayer)) { // Если это один и тот же стол. то отменяем галку
                        tagValue = PlaceActivity.UNSELECTED
                        bitmapChoosed!!.bitmap = freeIconBmp
                        bitmapChoosed = null
                        placeActivity.sit_count.text = "- место"
                        choosedTableNumber = 0
                        Toast.makeText(
                            placeActivity.applicationContext,
                            "Отмена",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else { // если это другой стол
                        bitmapChoosed!!.bitmap = freeIconBmp // обозначем занятой стол свободным
                        bitmapChoosed = bitmapLayer // и обозначем новый стол занятым
                        bitmapChoosed!!.bitmap = choosedIconBmp
                        placeActivity.sit_count.text = "1 место"
                        choosedTableNumber = choosedId

                        Toast.makeText(
                            placeActivity.applicationContext,
                            "Место выбрано " + choosedTableNumber,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    bitmapChoosed = bitmapLayer
                    bitmapChoosed!!.bitmap = choosedIconBmp
                    placeActivity.sit_count.text = "1 место"
                    choosedTableNumber = choosedId

                    Toast.makeText(
                        placeActivity.applicationContext,
                        "Место выбрано " + choosedTableNumber,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                placeActivity.intent.putExtra(PlaceActivity.SELECTED_TAG, tagValue)
                placeActivity.updateButton(PlaceActivity.CHOOSE_PAGE)
                currentMap!!.refresh()
            }
            }
            else {
                Toast.makeText(
                    placeActivity.applicationContext,
                    "Вы уже забронировали стол на " + placeActivity.date,
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
        currentMap!!.addLayer(bitmapLayer)
        currentMap!!.refresh()
    }

    /**
     * database должен начинаться с чайлада "Столы"
     */
    fun checkDateReservationUser (){

        database2.child("Пользователи").child(user!!.phoneNumber!!).child("Активные брони")
            .child(placeActivity.intent.getStringExtra("place_name")).child(placeActivity.date)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    checkDateReservationUser = dataSnapshot.getValue().toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })
    }

    fun createTables(database: DatabaseReference) {
        if(!isLoader) return
        checkDateReservationUser ()
        var tree = database.child("Номер стола")

        tree.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val id = ds.key
                    val x = ds.child("Координаты").child("x").value.toString()
                    val y = ds.child("Координаты").child("y").value.toString()
                    val reserved =
                        ds.child("Бронь").child("Дата").child(placeActivity.date).child("Забронирован").value.toString()
                    drawDick(x!!.toFloat(), y!!.toFloat(), id.toString().toInt(), reserved!!.toBoolean())
                }

                //after all remove listener
                tree.removeEventListener(this)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                tree.removeEventListener(this)
                Log.e(TAG, "Ah shit, here we go again")
            }
        })
    }
}