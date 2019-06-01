package com.example.placereserve

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.onlylemi.mapview.library.MapView
import android.app.TimePickerDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import java.util.*
import android.text.format.DateUtils
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import com.example.placereserve.PlacesData.favoritePlacesList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_place.*
import kotlinx.android.synthetic.main.activity_place_info.*
import com.squareup.picasso.MemoryPolicy
import kotlinx.android.synthetic.main.popup_window_for_admin.*
import java.lang.Exception
import kotlin.collections.ArrayList

class PlaceActivity : AppCompatActivity() {

    private val TAG = "PlaceActivity"
    lateinit var firebaseAuth: FirebaseAuth
    var numberforCall :String = ""
    var calendar = Calendar.getInstance()
    val database = FirebaseDatabase.getInstance()
    lateinit var checkTableData : ValueEventListener
    private var mapListener: CustomMapViewListener? = null

    // MAP
    private var mapView: MapView? = null
    private val ref = FirebaseDatabase.getInstance().reference
    private lateinit var myRef: DatabaseReference
    private lateinit var database2: DatabaseReference
    var date = "" + calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.get(Calendar.MONTH) + 1) + " " + calendar.get(
        Calendar.YEAR
    )
    var time = "" + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)

    override fun onDestroy() {
        mapTarget = null
            //  checkTableData.removeEventListener(this)
        mapView = null
        mapListener = null
        intent.removeExtra(PAGE_TAG)
        intent.removeExtra(SELECTED_TAG)

        //DebugApp.getRefWatcher(this).watch(this) // LeakCanary test
        super.onDestroy()
    }
    var id_stola = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place)
        firebaseAuth = FirebaseAuth.getInstance()
        mapView = null
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser


        if (!intent.hasExtra("place_name") || !intent.hasExtra("place_address")) {
            // sorry, but not
            Log.e(TAG, "Extras is null!")
            finish()
            return
        }



         checkTableData = database.getReference("Заведения").child(intent.getStringExtra("place_name"))
            .child("Столы").child("Номер стола")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (mapListener!=null) {
                        mapListener!!.updateTables()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                }
            })





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
                intent.putExtra(PAGE_TAG, INFO_PAGE)
                updatePageUI(false)
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
                v.startAnimation(animAlpha)
                setDate(v)
            }
        })

        time_text.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                setTime(v)
            }
        })

            //окно админа, кнопка удаления брони
        btn_remove.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                //v.startAnimation(animAlpha)


                val ref = database.getReference("Заведения").child(intent.getStringExtra("place_name"))
                    .child("Столы").child("Номер стола")
                    .child(id_stola.toString()).child("Бронь").child("Дата")
                    .child(date).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                           var NumberVisitor =dataSnapshot.child("Номер клиента").value.toString()
                            DelEteDataVisitio(NumberVisitor)
                            ref.removeEventListener(this)

                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            ref.removeEventListener(this)
                        }
                    })


                mapListener!!.updateTables()
               updateButton(intent.getIntExtra(PAGE_TAG, INFO_PAGE))

            }
        })

// окно админа, кнопка инфо о забронированном столике
        btn_popUp.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)



                // Initialize a new layout inflater instance
                val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

                // Inflate a custom view using layout inflater
                val view = inflater.inflate(R.layout.popup_window_for_admin,null)

                // Initialize a new instance of popup window
                val popupWindow = PopupWindow(
                    view, // Custom view to show in popup window
                    LinearLayout.LayoutParams.MATCH_PARENT, // Width of popup window
                    LinearLayout.LayoutParams.MATCH_PARENT // Window height
                )

                // Set an elevation for the popup window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    popupWindow.elevation = 10.0F
                }


                // If API level 23 or higher then execute the code
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    // Create a new slide animation for popup window enter transition
                    val slideIn = Slide()
                    slideIn.slideEdge = Gravity.TOP
                    popupWindow.enterTransition = slideIn

                    // Slide animation for popup window exit transition
                    val slideOut = Slide()
                    slideOut.slideEdge = Gravity.RIGHT
                    popupWindow.exitTransition = slideOut

                }

                // Get the widgets reference from custom view
                val imya = view.findViewById<TextView>(R.id.nameeeValue)
                val buttonPopup = view.findViewById<ImageButton>(R.id.button_exit_popUp)
                val nomer = view.findViewById<TextView>(R.id.numValue)

                // Set click listener for popup window's text view
                nomer.setOnClickListener{
                    // Change the text color of popup window's text view
                    //tv.setTextColor(Color.RED)
                    it.startAnimation(animAlpha)



                    // Here, thisActivity is the current activity
                    if (ContextCompat.checkSelfPermission(this@PlaceActivity,
                            Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this@PlaceActivity,
                                Manifest.permission.READ_CONTACTS)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed, we can request the permission.
                            ActivityCompat.requestPermissions(this@PlaceActivity,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {

                    }

                }

                // Set a click listener for popup's button widget
                buttonPopup.setOnClickListener{
                    // Dismiss the popup window
                    btn_popUp.visibility = View.VISIBLE
                    popupWindow.dismiss()
                }

                // Set a dismiss listener for popup window
                popupWindow.setOnDismissListener {
                   // Toast.makeText(applicationContext,"Popup closed",Toast.LENGTH_SHORT).show()
                }


                // Finally, show the popup window on app
                TransitionManager.beginDelayedTransition(place_layout)
                popupWindow.showAtLocation(
                    place_layout, // Location to display popup window
                    Gravity.BOTTOM, // Exact position of layout to display popup
                    500, // X offset
                    500 // Y offset
                )


               val ref = database.getReference("Заведения").child(intent.getStringExtra("place_name"))
                    .child("Столы").child("Номер стола")
                    .child(id_stola.toString()).child("Бронь").child("Дата")
                   .child(date).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            nomer.text =dataSnapshot.child("Номер клиента").value.toString()
                            numberforCall= nomer.text.toString()
                            imya.text=dataSnapshot.child("Имя клиента").value.toString()

                            ref.removeEventListener(this)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Failed to read value
                            ref.removeEventListener(this)
                        }
                    })


                btn_popUp.visibility = View.INVISIBLE
            }
        })

        admin_exit_btn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)

                // build alert dialog
                val dialogBuilder = AlertDialog.Builder(this@PlaceActivity)

                    // set message of alert dialog
                      dialogBuilder.setMessage("Вы больше не сможете войти под учетной записью администратора заведения")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Принять", DialogInterface.OnClickListener { dialog, id ->
                        signOut()
                    })
                    // negative button text and action
                    .setNegativeButton("Отменить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Вы уверены что хотите выйти?")
                // show alert dialog
                alert.show()
            }
        })
        val adapterHis = HistoryPlacesAdapter()
        btn_confirm.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (mapListener!!.bitmapChoosed != null) {
                    var reserve = ""

                    ref.child("Заведения").child("Йохан Пивохан")
                        .child("Столы").child("Номер стола").child(mapListener!!.choosedTableNumber.toString())
                        .child("Бронь").child("Дата").child(date).child("Забронирован")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                reserve = dataSnapshot.getValue().toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Failed to read value
                            }
                        })

                    if (reserve != "true") {

                        ref.child("Заведения").child(intent.getStringExtra("place_name"))
                            .child("Столы").child("Номер стола").child(mapListener!!.choosedTableNumber.toString())
                            .child("Бронь").child("Дата").child(date).child("Забронирован").setValue("true")


                        ref.child("Заведения").child(intent.getStringExtra("place_name"))
                            .child("Столы").child("Номер стола").child(mapListener!!.choosedTableNumber.toString())
                            .child("Бронь").child("Дата").child(date)
                            .child("Время").setValue(time)

                        ref.child("Заведения").child(intent.getStringExtra("place_name"))
                            .child("Столы").child("Номер стола").child(mapListener!!.choosedTableNumber.toString())
                            .child("Бронь").child("Дата").child(date)
                            .child("Номер клиента").setValue(user!!.phoneNumber!!)

//                        ref.child("Пользователи").child(user.phoneNumber!!).child("Активные брони")
//                            .child(intent.getStringExtra("place_name")).child("Дата")
//                            .child(date).setValue(date)




                        ref.child("Пользователи").child(user.phoneNumber!!).child("ИмяПользователя")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    ref.child("Заведения").child(intent.getStringExtra("place_name"))
                                        .child("Столы").child("Номер стола")
                                        .child(mapListener!!.choosedTableNumber.toString())
                                        .child("Бронь").child("Дата").child(date)
                                        .child("Имя клиента").setValue(dataSnapshot.getValue())

                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Failed to read value
                                }
                            })

                        ref.child("Заведения").child(intent.getStringExtra("place_name"))
                            .child("Столы").child("Номер стола").child(mapListener!!.choosedTableNumber.toString())
                            .child("Бронь").child("Дата").child(date)
                            .child("Номер клиента")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.getValue().toString() == user!!.phoneNumber.toString()) {
                                        mapListener!!.bitmapChoosed!!.bitmap = TableIconsCache.busyIconBmp
                                        mapView!!.refresh()

                                        Toast.makeText(
                                            this@PlaceActivity,
                                            "Вы забронировали стол на $date в $time",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        if ( intent.getStringExtra("place_status")=="1") {

                                            var r =  ref.child("Пользователи").child(user.phoneNumber!!).child("Активные брони")
                                                .child(intent.getStringExtra("place_name")).child(intent.getStringExtra("place_address")).child(date)
                                            r.child("НомерСтола").setValue(id_stola.toString())
                                            r.child("Время").setValue(time)

                                            PlacesData.historyPlacesList.add(
                                                PlacesHistory(
                                                    intent.getStringExtra("place_name"),
                                                    intent.getStringExtra("place_address"), date
                                                    , time, R.drawable.background_history,
                                                    id_stola.toString()
                                                )
                                            )
                                            adapterHis.refreshHistoryPlaces(PlacesData.getHistoryPlaces())
                                            val goHis = Intent(this@PlaceActivity, HistoryPlaces::class.java)
                                            startActivity(goHis)
                                            finish()
                                        }


                                    } else {
                                        mapView!!.refresh()
                                        Snackbar.make(
                                            place_info_layout,
                                            "К сожалению, кто-то забронировал раньше Вас. Выберите, пожалуйста, другой стол.",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Failed to read value
                                }
                            })
                    }
                }
                updateButton(intent.getIntExtra(PAGE_TAG, INFO_PAGE))
            }
        })

        updatePageUI(true)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    var callVisitor = Intent(Intent.ACTION_CALL)
                    callVisitor.setData(Uri.parse("tel:" + numberforCall))
                    startActivity(callVisitor)
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
    // установка обработчика выбора времени
    var t: TimePickerDialog.OnTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        time = "$hourOfDay:$minute"
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
            date = "" + dayOfMonth + " " + (monthOfYear + 1) + " " + year
            updateDate()
            intent.putExtra(SELECTED_TAG, UNSELECTED)
            updateButton(intent.getIntExtra(PAGE_TAG, INFO_PAGE))
//            posBut = -1
//            intent.putExtra(SELECTED_TAG, UNSELECTED)
        }

    fun DelEteDataVisitio (numberVisitor: String) {

        val myr= database.getReference("Пользователи").child(numberVisitor).child("Активные брони")
            .child(intent.getStringExtra("place_name")).child(intent.getStringExtra("place_address"))
            .child(date)
        myr.removeValue()
        val myr1 =database.getReference("Заведения").child(intent.getStringExtra("place_name"))
            .child("Столы").child("Номер стола")
            .child(id_stola.toString()).child("Бронь").child("Дата").child(date)
        myr1.removeValue()
        Snackbar.make(
            place_layout,
            "Удалено",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    private fun updateDate() {
        sit_count.text = ""
        date_text!!.text =
            (DateUtils.formatDateTime(
                this,
                calendar.timeInMillis,
                DateUtils.FORMAT_NUMERIC_DATE or DateUtils.FORMAT_SHOW_YEAR
            ))
        if(mapListener != null) {
            mapListener!!.updateTables()
        }
    }
    private var changeNameListener: ValueEventListener? = null

    private fun signOut() {
        if (changeNameListener != null) {
            val user = firebaseAuth.currentUser
            database.getReference("Пользователи").child(user!!.phoneNumber!!).child("ИмяПользователя")
                .removeEventListener(changeNameListener!!)
            changeNameListener = null
        }
        firebaseAuth.signOut()
        val user = firebaseAuth.currentUser

        if (user == null) {
            if (intent.hasExtra(MainActivity.MAIN_MENU_PAGE_TAG)) {
                intent.removeExtra(MainActivity.MAIN_MENU_PAGE_TAG)
            }
            val auth = Intent(this, AuthActivity::class.java)
            startActivity(auth)
            finish()
            return
        }
    }

    private fun updateTime() {
        time_text!!.text =
            (DateUtils.formatDateTime(
                this,
                calendar.timeInMillis,
                DateUtils.FORMAT_SHOW_TIME
            ))
        if(mapListener != null) {
            mapListener!!.updateTables()
        }
    }

    // отображаем диалоговое окно для выбора даты
    fun setDate(v: View) {

        val startEndDate = DatePickerDialog(
            this@PlaceActivity, d,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        startEndDate.datePicker.minDate = System.currentTimeMillis()
        startEndDate.datePicker.maxDate = System.currentTimeMillis() + 2592000000
        startEndDate.show()
    }

    // отображаем диалоговое окно для выбора времени
    fun setTime(v: View) {
        TimePickerDialog(
            this@PlaceActivity, t,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE), true
        ).show()
    }

    private fun parsingFromDatabase(page: Int) {
        if (!intent.hasExtra("place_name") || !intent.hasExtra("place_address")) {
            // sorry, but not
            Log.e(TAG, "Extras is null!")
            finish()
            return
        }

        when (page) {
            INFO_PAGE -> {
                restaurant_name_from_info.text = intent.getStringExtra("place_name")
                restaurant_address_from_info.text = intent.getStringExtra("place_address")

                val myRef =
                    database.getReference("Заведения").child(intent.getStringExtra("place_name")).child("Данные")
                myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        restaurant_description_from_info.text = dataSnapshot.child("Описание").value.toString()
                        restaurant_specialinfo_from_info.text = dataSnapshot.child("Кухня").value.toString()
                        myRef.removeEventListener(this)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        myRef.removeEventListener(this)
                    }
                })
                val adapterFav = FavoritePlacesAdapter()
                val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
                val btnForFavorite = findViewById<ImageView>(R.id.btn_for_favorite)
                btnForFavorite.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        v.startAnimation(animAlpha)
                        val user = firebaseAuth.currentUser
                        val myRef =
                            database.getReference("Пользователи").child(user?.phoneNumber!!).child("ИзбранныеЗаведения")
                                .child(intent.getStringExtra("place_name"))
                        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val a = dataSnapshot.getValue()
                                if (a == null) {
                                    database.getReference("Пользователи")
                                        .child(user?.phoneNumber!!)
                                        .child("ИзбранныеЗаведения")
                                        .child(intent.getStringExtra("place_name"))
                                        .setValue(intent.getStringExtra("place_address"))
                                    Snackbar.make(
                                        place_info_layout,
                                        "Добавлено в избранные",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    adapterFav.refreshFavoritePlaces(PlacesData.getFavoritePlaces())
                                } else {
                                    myRef.removeValue()
                                    favoritePlacesList.remove(
                                        PlacesFavorite(
                                            intent.getStringExtra("place_name"),
                                            intent.getStringExtra("place_address"),
                                            R.drawable.background_favorite_places
                                        )
                                    )
                                    adapterFav.refreshFavoritePlaces(favoritePlacesList)
                                    adapterFav.notifyDataSetChanged()
                                    Snackbar.make(
                                        place_info_layout,
                                        "Удалено из избранных",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                            }
                        })
                    }
                })

                //типо loading images

                var images = getImages()
                val photos = images.size
                val width = resources.getDimension(R.dimen.imageview_width)
                for (i in 0..(photos - 1)) {
                    val iv = ImageView(this)
                    iv.setBackgroundColor(resources.getColor(R.color.image_background_color))
                    Picasso.get().load(images[i]).memoryPolicy(MemoryPolicy.NO_CACHE).resize(width.toInt(), 0).into(iv)
                    linear_images.addView(iv)
                }
            }
            CHOOSE_PAGE -> {
                restaurant_name_from_choose.text = intent.getStringExtra("place_name")
                restaurant_address.text = intent.getStringExtra("place_address")

                when (intent.getStringExtra("place_name")) {
                    "Йохан Пивохан" -> {
                        loadMap("test_map1.png")

                    }
                    "Карл у Клары" -> {
                        loadMap("test_map_2.png")
                    }
                    else ->{
                        loadMap("r00.png")
                    }
                }
            }
            CHOOSE_PAGE_ADMIN -> {
                restaurant_name_from_choose.text = intent.getStringExtra("place_name")
                restaurant_address.text = intent.getStringExtra("place_address")

                when (intent.getStringExtra("place_name")) {
                    "Йохан Пивохан" -> {
                        loadMap("test_map1.png")

                    }
                    "Карл у Клары" -> {
                        loadMap("test_map_2.png")
                    }
                    else ->{
                        loadMap("r00.png")
                    }
                }
            }
        }
    }

    fun getImages():ArrayList<Int>{
        var arr : ArrayList<Int>
        when (intent.getStringExtra("place_name")) {
            "Йохан Пивохан" -> {
                arr = arrayListOf(R.drawable.yohan1, R.drawable.yohan2, R.drawable.yohan3)

            }
            "Карл у Клары" -> {
                arr= arrayListOf(R.drawable.karl1, R.drawable.karl2)
            }
            else -> {
                arr =  arrayListOf(R.drawable.r0)
            }
        }
        return arr
    }
    fun updatePageUI(reparse: Boolean) {
        var flag : Int
        if (intent.getStringExtra("place_status")=="2") {
            flag = intent.getIntExtra(PAGE_TAG, CHOOSE_PAGE_ADMIN)

        } else  {
            flag = intent.getIntExtra(PAGE_TAG, INFO_PAGE)
        }


        when (flag) {
            INFO_PAGE -> {
                choose_layout.visibility = View.INVISIBLE
                place_info_layout.visibility = View.VISIBLE
            }
            CHOOSE_PAGE -> {
                choose_layout.visibility = View.VISIBLE
                place_info_layout.visibility = View.INVISIBLE
                updateDate()
                updateTime()
            }
            CHOOSE_PAGE_ADMIN -> {
                choose_layout.visibility = View.VISIBLE
                place_info_layout.visibility = View.INVISIBLE
                back_in_place_from_choose.visibility = View.INVISIBLE
                admin_exit_btn.visibility = View.VISIBLE
                sit_count.visibility = View.VISIBLE
                updateDate()
                updateTime()
            }
        }
        if (reparse) {
            parsingFromDatabase(flag)
        }
        updateButton(intent.getIntExtra(PAGE_TAG, INFO_PAGE))
    }


    var flag = UNSELECTED
    fun updateButton(page: Int) {
        flag = intent.getIntExtra(SELECTED_TAG, UNSELECTED)
        if (page == INFO_PAGE) {
            flag = UNSELECTED
        }
        if (page== REMOVE) {
        flag= REMOVE
        }
        when (flag) {
            UNSELECTED -> {
                btn_confirm.visibility = View.INVISIBLE
                btn_remove.visibility = View.INVISIBLE
                btn_popUp.visibility = View.INVISIBLE

            }
            SELECTED -> {
                btn_confirm.visibility = View.VISIBLE
                btn_remove.visibility = View.INVISIBLE
                btn_popUp.visibility = View.INVISIBLE
            }
            REMOVE -> {
                btn_popUp.visibility = View.VISIBLE
                btn_remove.visibility = View.VISIBLE
                btn_confirm.visibility = View.INVISIBLE
            }
        }
    }

    // подгрузка карты с помощью Picasso
    private var mapTarget: Target? = object : com.squareup.picasso.Target {
        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            if (bitmap == null) {
                Log.e(TAG, "Image is null!")
            } else {
                // загружаем в view
                mapListener = CustomMapViewListener(this@PlaceActivity, mapView!!)
                if (mapListener != null) {
                    mapView!!.setMapViewListener(mapListener)
                    mapView!!.loadMap(bitmap)
                }
            }

        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            Log.e(TAG, "Ah shit, here we go again")
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }
    }

    fun loadMap(mapName: String) {
        if (mapView != null) return

        // Поиск mapview
        mapView = findViewById(R.id.mapview)
        TableIconsCache.prepareIcons()

        // TODO: change to firebase images database
        val url = "file:///android_asset/$mapName"
        Picasso.get().load(url).into(mapTarget!!)
    }


    companion object {
        // Pages
        const val PAGE_TAG = "page"
        const val INFO_PAGE = 1
        const val CHOOSE_PAGE = 2
        const val CHOOSE_PAGE_ADMIN = 3
        const val SELECTED_TAG = "sit_selected"
        const val UNSELECTED = 0
        const val SELECTED = 1
        const val REMOVE = 4
        const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10
    }
}