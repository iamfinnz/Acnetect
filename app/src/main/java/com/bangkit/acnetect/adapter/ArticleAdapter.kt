package com.bangkit.acnetect.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.acnetect.R
import com.bangkit.acnetect.model.Article
import com.bangkit.acnetect.presentation.detail.DetailActivity
import com.bangkit.acnetect.adapter.ArticleAdapter.MainViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.list_acne.view.*

class ArticleAdapter(
    var context: Context,
    var articleList: MutableList<Article>) : RecyclerView.Adapter<MainViewHolder>(), Filterable {

    var articleFilterList: List<Article> = ArrayList(articleList)

    override fun getFilter(): Filter {
        return modelFilter
    }

    private val modelFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Article> = ArrayList()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(articleFilterList)
            } else {
                val filterPattern = constraint.toString().lowercase()
                for (modelMainFilter in articleFilterList) {
                    if (modelMainFilter.nama.lowercase().contains(filterPattern) ||
                        modelMainFilter.penyebab.lowercase().contains(filterPattern)
                    ) {
                        filteredList.add(modelMainFilter)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            articleList.clear()
            articleList.addAll(results.values as List<Article>)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_acne, parent, false)
        return MainViewHolder(view)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = articleList[position]

        holder.tvNamaAcne.text = data.nama

        Glide.with(context)
            .load(data.image)
            .transform(CenterCrop(), RoundedCorners(25))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder.imageAcne)

        //send data to detail activity
        holder.cvListMain.setOnClickListener { view: View? ->
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.DETAIL_ACNE, articleList[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cvListMain: CardView
        var tvNamaAcne: TextView
        var imageAcne: ImageView

        init {
            cvListMain = itemView.cvListMain
            tvNamaAcne = itemView.li_tv_nama_acne
            imageAcne = itemView.li_image_acne
        }
    }

}