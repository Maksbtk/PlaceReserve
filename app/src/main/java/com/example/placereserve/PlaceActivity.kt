package com.example.placereserve

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.onlylemi.mapview.library.MapView
import com.onlylemi.mapview.library.layer.BitmapLayer
import com.onlylemi.mapview.library.MapViewListener
import kotlinx.android.synthetic.main.activity_place.*
import java.io.IOException
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.widget.TextView
import java.util.*
import android.widget.DatePicker
import android.widget.TimePicker
import android.text.format.DateUtils


class PlaceActivity : AppCompatActivity() {

    private val TAG = "PlaceActivity"

    var calendar = Calendar.getInstance()

    // MAP
    private var mapView: MapView? = null
    private var bitmapLayer: BitmapLayer? = null
    private var posBut = -1
    private var bitmapChoosed: BitmapLayer? = null
    private var bitmapLayer1: BitmapLayer? = null
    private var bitmapLayer2: BitmapLayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        back_in_place.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                finish()
            }
        })

        date_text.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                setDate(v)
            }
        })

        time_text.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                setTime(v)
            }
        })

        restaurant_name.text = intent.getStringExtra("place_name")
        restaurant_address.text = intent.getStringExtra("place_address")
        updateDate()
        updateTime()
        updateUI()
        loadMap()
        Toast.makeText(this, intent.getStringExtra("place_name"), Toast.LENGTH_SHORT).show()
    }

    // установка обработчика выбора времени
    var t: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        updateTime()

    }

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }

    private fun updateDate() {
        date_text!!.text =
            (DateUtils.formatDateTime(
                this,
                calendar.timeInMillis,
                DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR
            ))
    }

    private fun updateTime() {
        time_text!!.text =
            (DateUtils.formatDateTime(
                this,
                calendar.timeInMillis,
                DateUtils.FORMAT_SHOW_TIME
            ))
    }

    // отображаем диалоговое окно для выбора даты
    fun setDate(v: View) {
        DatePickerDialog(
            this@PlaceActivity, d,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

    // отображаем диалоговое окно для выбора времени
    fun setTime(v: View) {
        TimePickerDialog(
            this@PlaceActivity, t,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true
        )
            .show()
    }

    var flag = UNSELECTED
    fun updateUI() {
        if (!intent.hasExtra("place_name") || !intent.hasExtra("place_address")) {
            // sorry, but not
            Log.e(TAG, "Extras is null!")
            finish()
            return
        }

        flag = intent.getIntExtra(SELECTED_TAG, UNSELECTED)
        when (flag) {
            UNSELECTED -> {
                btn_confirm.visibility = View.INVISIBLE
            }
            SELECTED -> {
                btn_confirm.visibility = View.VISIBLE
            }
        }
    }

    val but = arrayListOf<BitmapLayer>()
    fun loadMap() {
        // Поиск mapview
        mapView = findViewById<MapView>(R.id.mapview)

        // подгрузка карты
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(assets.open("test_map1.png"))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // загружаем в view
        mapView!!.loadMap(bitmap)
        mapView!!.setMapViewListener(object : MapViewListener {
            // когда карта загрузилась
            override fun onMapLoadSuccess() {
                Log.i(TAG, "onMapLoadSuccess")

                val free = BitmapFactory.decodeResource(resources, R.drawable.free_1)
                val freeBmp = Bitmap.createScaledBitmap(free, 150, 150, false)

                val choosed = BitmapFactory.decodeResource(resources, R.drawable.choosedtable)
                val choosedBmp = Bitmap.createScaledBitmap(choosed, 150, 150, false)

                var x = 150f
                var y = 150f
                for (i in 0..3) {
                    bitmapLayer = BitmapLayer(mapView, freeBmp)
                    but.add(bitmapLayer!!)
                    but[i].location = PointF(x, y)
                    but[i].isAutoScale = true
                    but[i].setOnBitmapClickListener(BitmapLayer.OnBitmapClickListener {


                        but[i].bitmap = choosedBmp
                        intent.putExtra(SELECTED_TAG, SELECTED)

                        if(posBut == i){
                            but[posBut].bitmap = freeBmp
                            intent.putExtra(SELECTED_TAG, UNSELECTED)
                        }

                        if (posBut != i && posBut != -1){
                            but[posBut].bitmap = freeBmp
                        }

                        updateUI()

                        if (flag == UNSELECTED){
                            posBut = -1
                        }
                        else {
                            posBut = i
                        }

                        mapView!!.refresh()

                        Toast.makeText(
                            applicationContext,
                            "click2333333",
                            Toast.LENGTH_SHORT
                        ).show()
                        sit_count.text = "1 место"
                        updateUI()
                    })
                    mapView!!.addLayer(but[i])
                    mapView!!.refresh()
                    x += 60f
                    y += 60f
                }
            }

            // когда произошла ошибка загрузки
            override fun onMapLoadFail() {
                Log.i(TAG, "onMapLoadFail")
            }
        })
    }


    companion object {
        const val SELECTED_TAG = "sit_selected"
        const val UNSELECTED = 0
        const val SELECTED = 1
    }
}