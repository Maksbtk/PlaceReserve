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
import java.io.IOException
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.content.Intent
import java.util.*
import android.text.format.DateUtils
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_place.*
import kotlinx.android.synthetic.main.activity_place_info.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener


class PlaceActivity : AppCompatActivity() {

    private val TAG = "PlaceActivity"

    var calendar = Calendar.getInstance()
    val database = FirebaseDatabase.getInstance()

    // MAP
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        if (!intent.hasExtra("place_name") || !intent.hasExtra("place_address")) {
            // sorry, but not
            Log.e(TAG, "Extras is null!")
            finish()
            return
        }

        select_table.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                intent.putExtra(PAGE_TAG, CHOOSE_PAGE)
                updatePageUI(true)
            }
        })

        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        back_in_place_from_choose.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                finish()
            }
        })
        back_in_place_from_info.setOnClickListener(object : View.OnClickListener {
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
        btn_confirm.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //TODO: something
                finish()
            }
        })
        updatePageUI(true)
    }

    // установка обработчика выбора времени
    var t: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        updateTime()
//        posBut = -1
//        intent.putExtra(SELECTED_TAG, UNSELECTED)
    }

    // установка обработчика выбора даты
    var d: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
//            posBut = -1
//            intent.putExtra(SELECTED_TAG, UNSELECTED)
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

    private fun parsingFromDatabase(page: Int) {
        if (!intent.hasExtra("place_name") || !intent.hasExtra("place_address")) {
            // sorry, but not
            Log.e(TAG, "Extras is null!")
            finish()
            return
        }

        when(page) {
            INFO_PAGE-> {
                restaurant_name_from_info.text = intent.getStringExtra("place_name")
                restaurant_address_from_info.text = intent.getStringExtra("place_address")

                val myRef = database.getReference("Заведения").child(intent.getStringExtra("place_name")).child("Данные")
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        restaurant_description_from_info.text = dataSnapshot.child("Описание").value.toString()
                        restaurant_specialinfo_from_info.text = dataSnapshot.child("Кухня").value.toString()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                    }
                })

                val checkBox = findViewById<CheckBox>(R.id.checkBox)
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    val msg = "You have " + (if (isChecked) "checked" else "unchecked") + " this Check it Checkbox."
                    Toast.makeText(this@PlaceActivity, msg, Toast.LENGTH_SHORT).show()
                    if (isChecked) {
                        
                    }
                }
//                    val checkBox : CheckBox = findViewById(R.id.checkBox)

                //типо loading images
                val images = arrayListOf(R.drawable.photo1, R.drawable.photo2, R.drawable.photo3, R.drawable.photo4)
                val photos = images.size
                val width = resources.getDimension(R.dimen.imageview_width)
                for (i in 0..(photos - 1)) {
                    val iv = ImageView(this)
                    iv.setBackgroundColor(resources.getColor(R.color.imageBackgroundColor))
                    val bmp = BitmapFactory.decodeResource(resources, images[i])
                    iv.setImageBitmap(bmp)
                    linear_images.addView(iv)
                    iv.layoutParams.width = width.toInt()
                    iv.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }
            CHOOSE_PAGE->{
                restaurant_name_from_choose.text = intent.getStringExtra("place_name")
                restaurant_address.text = intent.getStringExtra("place_address")
                loadMap("test_map1.png")
            }
        }
    }

    fun updatePageUI(reparse:Boolean){
        var flag = intent.getIntExtra(PAGE_TAG, INFO_PAGE)

        when (flag) {
            INFO_PAGE-> {
                choose_layout.visibility = View.INVISIBLE
                place_info_layout.visibility = View.VISIBLE
            }
            CHOOSE_PAGE->{
                choose_layout.visibility = View.VISIBLE
                place_info_layout.visibility = View.INVISIBLE
                updateDate()
                updateTime()
            }
        }
        if(reparse) {
            parsingFromDatabase(flag)
        }
        updateButton(flag)
    }


    var flag = UNSELECTED
    fun updateButton(page: Int) {
        flag = intent.getIntExtra(SELECTED_TAG, UNSELECTED)
        if(page == INFO_PAGE) {
            flag = UNSELECTED
        }
        when (flag) {
            UNSELECTED -> {
                btn_confirm.visibility = View.INVISIBLE
            }
            SELECTED -> {
                btn_confirm.visibility = View.VISIBLE
            }
        }
    }

    fun loadMap(mapName:String) {
        // Поиск mapview
        mapView = findViewById(R.id.mapview)

        // подгрузка карты
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(assets.open(mapName))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // загружаем в view
        val listener = CustomMapViewListener(this, mapView!!)
        mapView!!.setMapViewListener(listener)
        mapView!!.loadMap(bitmap)
    }


    companion object {
        // Pages
        const val PAGE_TAG = "page"
        const val INFO_PAGE = 1
        const val CHOOSE_PAGE = 2

        const val SELECTED_TAG = "sit_selected"
        const val UNSELECTED = 0
        const val SELECTED = 1
    }
}