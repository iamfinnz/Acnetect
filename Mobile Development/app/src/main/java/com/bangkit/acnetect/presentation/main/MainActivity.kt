package com.bangkit.acnetect.presentation.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.acnetect.R
import com.bangkit.acnetect.adapter.ArticleAdapter
import com.bangkit.acnetect.databinding.ActivityMainBinding
import com.bangkit.acnetect.model.Article
import com.bangkit.acnetect.model.User
import com.bangkit.acnetect.presentation.user.UserActivity
import com.bangkit.acnetect.utils.showDialogError
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.StandardCharsets
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_POSITION = "extra_position"
    }

    //Carousel
    var sampleImages = intArrayOf(
        R.drawable.cs1,
        R.drawable.cs2,
        R.drawable.cs3
    )

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var userDatabase: DatabaseReference
    private lateinit var materialDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private var listenerUser = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            hideLoading()
            val user = snapshot.getValue(User::class.java)
            user?.let {
                mainBinding.apply {
                    tvNameUserMain.text = it.nameUser

                    Glide
                        .with(this@MainActivity)
                        .load(it.avatarUser)
                        .placeholder(android.R.color.darker_gray)
                        .into(ivAvatarMain)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            hideLoading()
            Log.e("MainActivity", "[onCancelled] - ${error.message}")
            showDialogError(this@MainActivity, error.message)
        }
    }

    //Recyclerview
    private var article: MutableList<Article> = ArrayList()
    lateinit var articleAdapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        //Init
        userDatabase = FirebaseDatabase.getInstance().getReference("users")
        currentUser = FirebaseAuth.getInstance().currentUser

        val carouselView = findViewById(R.id.carouselView) as CarouselView;
        carouselView.setPageCount(sampleImages.size);
        carouselView.setImageListener(imageListener);

        getDataFirebase()
        onAction()

        //transparent background searchview
        val searchPlateId = searchData.getContext()
            .resources.getIdentifier("android:id/search_plate", null, null)

        val searchPlate = searchData.findViewById<View>(searchPlateId)
        searchPlate?.setBackgroundColor(Color.TRANSPARENT)
        searchData.setImeOptions(EditorInfo.IME_ACTION_DONE)
        searchData.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                articleAdapter.filter.filter(newText)
                return true
            }
        })

        rvAcne.setLayoutManager(LinearLayoutManager(this))
        rvAcne.setHasFixedSize(true)

        //get data json
        getListAcne()
    }

    var imageListener: ImageListener = object : ImageListener {
        override fun setImageForPosition(position: Int, imageView: ImageView) {
            // You can use Glide or Picasso here
            imageView.setImageResource(sampleImages[position])
        }
    }

    private fun getDataFirebase() {
        showLoading()
        userDatabase
            .child(currentUser?.uid.toString())
            .addValueEventListener(listenerUser)

    }

    private fun showLoading() {
        mainBinding.swipeMain.isRefreshing = true
    }

    private fun hideLoading() {
        mainBinding.swipeMain.isRefreshing = false
    }

    private fun onAction() {
        mainBinding.apply {
            ivAvatarMain.setOnClickListener {
                startActivity<UserActivity>()
            }
        }

    }

    private fun getListAcne() {
        try {
            val stream = assets.open("daftar_acne2.json")
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            val strContent = String(buffer, StandardCharsets.UTF_8)
            try {
                val jsonObject = JSONObject(strContent)
                val jsonArray = jsonObject.getJSONArray("daftar_acne")
                for (i in 0 until jsonArray.length()) {
                    val jsonObjectData = jsonArray.getJSONObject(i)
                    val dataApi = Article()
                    dataApi.nama = jsonObjectData.getString("nama")
                    dataApi.penyebab = jsonObjectData.getString("penyebab")
                    dataApi.solusi = jsonObjectData.getString("solusi")
                    dataApi.image = jsonObjectData.getString("img")
                    dataApi.penjelasan = jsonObjectData.getString("penjelasan")
                    article.add(dataApi)
                }
                articleAdapter = ArticleAdapter(this, article)
                val manager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                rvAcne.layoutManager = manager
                rvAcne.adapter = articleAdapter
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } catch (ignored: IOException) {
            Toast.makeText(
                this@MainActivity,
                "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}