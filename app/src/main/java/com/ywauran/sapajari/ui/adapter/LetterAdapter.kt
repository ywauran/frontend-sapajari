package com.ywauran.sapajari.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ywauran.sapajari.R
import com.ywauran.sapajari.adapter.NumberAdapter
import com.ywauran.sapajari.data.remote.response.LetterResponse
import com.ywauran.sapajari.data.remote.response.NumberResponse

class LetterAdapter(
    private val listener: (LetterResponse) -> Unit
) : RecyclerView.Adapter<LetterAdapter.LetterViewHolder>() {

    private val letterList: ArrayList<LetterResponse> = ArrayList()

    fun setItems(items: List<LetterResponse>) {
        letterList.clear()
        letterList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LetterViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_letter, parent, false)
        return LetterViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: LetterViewHolder, position: Int) {
        val currentItem = letterList[position]
        holder.bindView(currentItem, listener)
    }

    override fun getItemCount(): Int {
        return letterList.size
    }

    class LetterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val letterTextView: TextView = itemView.findViewById(R.id.tv_symbol_letter)

        fun bindView(item: LetterResponse, listener: (LetterResponse) -> Unit) {
            letterTextView.text = item.symbol

            itemView.setOnClickListener {
                listener(item)
            }
        }
    }
}
