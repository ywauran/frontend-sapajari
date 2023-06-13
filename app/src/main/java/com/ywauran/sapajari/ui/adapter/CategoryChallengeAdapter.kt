package com.ywauran.sapajari.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ywauran.sapajari.R
import com.ywauran.sapajari.data.remote.response.CategoryChallengeResponse

class CategoryChallengeAdapter(
    private val listener: (CategoryChallengeResponse) -> Unit
) : RecyclerView.Adapter<CategoryChallengeAdapter.CategoryChallengeViewHolder>() {

    private val categoryList: ArrayList<CategoryChallengeResponse> = ArrayList()

    fun setItems(items: List<CategoryChallengeResponse>) {
        categoryList.clear()
        categoryList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryChallengeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_challenge, parent, false)
        return CategoryChallengeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryChallengeViewHolder, position: Int) {
        val currentItem = categoryList[position]
        holder.bindView(currentItem, listener)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_title_category_challenge)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.tv_description_category_challenge)
        private val categoryImageView: ImageView = itemView.findViewById(R.id.iv_category_challenge)

        fun bindView(item: CategoryChallengeResponse, listener: (CategoryChallengeResponse) -> Unit) {
            titleTextView.text = item.title
            descriptionTextView.text = item.description

            // Load the image using Glide or any other image loading library
            Glide.with(itemView)
                .load("http://192.168.1.5:8000/images/category-challenge/${item.image}")
                .into(categoryImageView)

            itemView.setOnClickListener {
                listener(item)
            }
        }
    }
}

