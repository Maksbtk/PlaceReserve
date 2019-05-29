package com.example.placereserve

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomNavigationView
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import kotlinx.android.synthetic.main.activity_main.*

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.widget.*
import com.google.firebase.auth.*
import com.google.firebase.database.*
import android.text.InputFilter
import android.widget.TextView
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_favoriteplaces.*

class MainActivity : AppCompatActivity() {

    //инициализируем ViewModel ленивым способом
    private val userViewModel by lazy { ViewModelProviders.of(this).get(PlacesViewModel::class.java) }
    val database = FirebaseDatabase.getInstance()
    private lateinit var firebaseAuth: FirebaseAuth

    // firebase listeners
    private var changeNameListener: ValueEventListener? = null

    // LeakCanary fixes
    override fun onDestroy() {
        super.onDestroy()

        if (changeNameListener != null) {
            val user = firebaseAuth.currentUser
            database.getReference("Пользователи").child(user!!.phoneNumber!!).child("ИмяПользователя")
                .removeEventListener(changeNameListener!!)
            changeNameListener = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        val et = SearchId.findViewById(
            SearchId.getContext().getResources()
                .getIdentifier("android:id/search_src_text", null, null)
        ) as EditText
        et.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(15))

        val user = firebaseAuth.currentUser
        if (user != null) {
            if (changeNameListener == null) {
                changeNameListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        nameUser.text = dataSnapshot.value.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                    }
                }
                database.getReference("Пользователи").child(user.phoneNumber!!).child("ИмяПользователя")
                    .addValueEventListener(changeNameListener as ValueEventListener)
            }
        }

        val signOut = findViewById<View>(R.id.btn_sign_out) as ImageButton

        val animAlpha: Animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
        //инициализируем адаптер и присваиваем его списку
        val adapter = PlacesAdapter()
        placesList.layoutManager = LinearLayoutManager(this)
        placesList.adapter = adapter

        //подписываем адаптер на изменения списка
        userViewModel.getListPlaces().observe(this, Observer {
            it?.let {
                adapter.refreshPlaces(it)
            }
        })

        val goToFavoritePlaces = findViewById<View>(R.id.favoriteBtn) as ImageButton
        goToFavoritePlaces.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val favoriteIntent = Intent(this@MainActivity, FavoritePlaces::class.java)
                startActivity(favoriteIntent)
            }
        })

        val goToHistoryPlaces = findViewById<View>(R.id.HistoryButton) as ImageButton
        goToHistoryPlaces.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val historyIntent = Intent(this@MainActivity, HistoryPlaces::class.java)
                startActivity(historyIntent)
            }
        })

        signOut.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)

                // build alert dialog
                val dialogBuilder = AlertDialog.Builder(this@MainActivity)

                    // set message of alert dialog
                    //  dialogBuilder.setMessage("Вы уверены что хотите выйти?")
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

        // слушатель на нажатиие всей области серчвью
        SearchId.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                SearchId.setIconified(false)

            }
        })

        //слушаетль на изменение поля ввода серчвью
        SearchId.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                //    Toast.makeText(this@MainActivity, "слушаетль работает", Toast.LENGTH_SHORT).show()
                adapter.filter(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
        })

        //слушатель меню итемов юзер\плейсес
        btnnvg.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.navigation_user -> {
                        intent.putExtra(MAIN_MENU_PAGE_TAG, USER_PAGE)
                        updateInterface()
                    }
                    R.id.navigation_places -> {
                        intent.putExtra(MAIN_MENU_PAGE_TAG, PLACES_PAGE)
                        updateInterface()
                    }
                }
                return false
            }
        })

        change_data.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                v.startAnimation(animAlpha)
                val cda = Intent(this@MainActivity, Change_data_user::class.java)
                startActivity(cda)
            }
        })

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        updateInterface()
    }

    private fun updateInterface() {
        val user = firebaseAuth.currentUser
        updateUI(user)
    }

    private fun signOut() {
        if (changeNameListener != null) {
            val user = firebaseAuth.currentUser
            database.getReference("Пользователи").child(user!!.phoneNumber!!).child("ИмяПользователя")
                .removeEventListener(changeNameListener!!)
            changeNameListener = null
        }
        firebaseAuth.signOut()
        updateInterface()
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            if (intent.hasExtra(MAIN_MENU_PAGE_TAG)) {
                intent.removeExtra(MAIN_MENU_PAGE_TAG)
            }
            val auth = Intent(this, AuthActivity::class.java)
            startActivity(auth)
            finish()
            return
        }
        val btnnvg: BottomNavigationView = this.findViewById(R.id.Navigationb)
        var flag = intent.getIntExtra(MAIN_MENU_PAGE_TAG, PLACES_PAGE)

        when (flag) {
            PLACES_PAGE -> {
                btnnvg.visibility = View.VISIBLE
                layout_user.visibility = View.INVISIBLE
                layout_places.visibility = View.VISIBLE
                btnnvg.menu.findItem(R.id.navigation_places).isEnabled = false
                btnnvg.menu.findItem(R.id.navigation_user).isEnabled = true
            }
            USER_PAGE -> {
                btnnvg.visibility = View.VISIBLE
                layout_user.visibility = View.VISIBLE
                layout_places.visibility = View.INVISIBLE
                btnnvg.menu.findItem(R.id.navigation_places).isEnabled = true
                btnnvg.menu.findItem(R.id.navigation_user).isEnabled = false
            }
        }
    }

    companion object {
        const val MAIN_MENU_PAGE_TAG = "main_menu_page"
        const val PLACES_PAGE = 1
        const val USER_PAGE = 2
    }
}