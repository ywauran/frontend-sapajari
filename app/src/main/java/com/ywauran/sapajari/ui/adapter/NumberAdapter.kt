package com.ywauran.sapajari.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ywauran.sapajari.R
import com.ywauran.sapajari.data.remote.response.NumberResponse

class NumberAdapter(
    private val listener: (NumberResponse) -> Unit
) : RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {

    private val numberList: ArrayList<NumberResponse> = ArrayList()

    fun setItems(items: List<NumberResponse>) {
        numberList.clear()
        numberList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_number, parent, false)
        return NumberViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
        val currentItem = numberList[position]
        holder.bindView(currentItem, listener)
    }

    override fun getItemCount(): Int {
        return numberList.size
    }

    class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val symbolTextView: TextView = itemView.findViewById(R.id.tv_symbol_number)

        fun bindView(item: NumberResponse, listener: (NumberResponse) -> Unit) {
            symbolTextView.text = item.symbol

            itemView.setOnClickListener {
                listener(item)
            }
        }
    }
}
