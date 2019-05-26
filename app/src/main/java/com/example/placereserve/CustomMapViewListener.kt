package com.example.placereserve

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.support.design.widget.TabLayout
import android.util.Log
import android.widget.Toast
import com.example.placereserve.TableIconsCache.Companion.choosedIconBmp
import com.example.placereserve.TableIconsCache.Companion.freeIconBmp
import com.google.firebase.database.*
import com.onlylemi.mapview.library.MapViewListener
import com.onlylemi.mapview.library.layer.BitmapLayer
import kotlinx.android.synthetic.main.activity_place.*
import com.google.firebase.database.DataSnapshot
import com.onlylemi.mapview.library.MapView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception
import java.util.concurrent.CompletableFuture


class CustomMapViewListener(private var placeActivity: PlaceActivity, private var currentMap: MapView) :
    MapViewListener {
    var isLoader: Boolean = false
    private val database = FirebaseDatabase.getInstance()
    var bitmapChoosed: BitmapLayer? = null
    var choosedTableNumber = 0
    private var TAG: String = "CustomMapViewListener"

    override fun onMapLoadSuccess() {
        isLoader = true

        val myRef =
            database.getReference("Заведения").child(placeActivity.intent.getStringExtra("place_name")).child("Столы")
        createTables(myRef)
    }

    override fun onMapLoadFail() {
        Log.e(TAG, "Ah shit, here we go again")
    }

    fun drawDick(x: Float, y: Float, id: Int) {
        if (!isLoader) return
        var bitmapLayer = BitmapLayer(currentMap, freeIconBmp)
        bitmapLayer!!.location = PointF(x, y)
        bitmapLayer!!.isAutoScale = true
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
        currentMap!!.addLayer(bitmapLayer)
        currentMap!!.refresh()
    }

    /**
     * database должен начинаться с чайлада "Столы"
     */
    fun createTables(database: DatabaseReference) {
        if (!isLoader) return
        var tree = database.child("Номер стола")

        tree.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val id = ds.key
                    val x = ds.child("Координаты").child("x").value.toString()
                    val y = ds.child("Координаты").child("y").value.toString()
                    drawDick(x!!.toFloat(), y!!.toFloat(), id.toString().toInt())
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