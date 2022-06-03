package com.bangkit.acnetect.presentation.detail

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.acnetect.R
import com.bangkit.acnetect.model.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.tvNamaAcne
import kotlinx.android.synthetic.main.activity_detail.tvPenyebab
import kotlinx.android.synthetic.main.activity_detail.tvSolusi

class DetailActivity : AppCompatActivity() {
    lateinit var nama: String
    lateinit var penyebab: String
    lateinit var solusi: String
    lateinit var penjelasan: String
    lateinit var article: Article

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //set transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        //get data intent
        article = intent.getSerializableExtra(DETAIL_ACNE) as Article
        if (article != null) {
            nama = article.nama
            penyebab = article.penyebab
            solusi = article.solusi
            penjelasan = article.penjelasan

            Glide.with(this)
                .load(article.image)
                .into(imageAcneBig)

            tvNamaAcne.setText(nama)
            tvPenyebab.setText(penyebab)
            tvSolusi.setText(solusi)
            tvPenjelasan.setText(penjelasan)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DETAIL_ACNE = "DETAIL_ACNE"
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }

}