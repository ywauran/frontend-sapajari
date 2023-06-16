package com.ywauran.sapajari.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ywauran.sapajari.R
import com.ywauran.sapajari.model.ChallengeModel

class ChallengeAdapter : RecyclerView.Adapter<ChallengeAdapter.ChallengeVH>() {

    private val challengeList: ArrayList<ChallengeModel> = ArrayList()

    fun setItems(items: List<ChallengeModel>) {
        challengeList.clear()
        challengeList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeVH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_character, parent, false)
        return ChallengeVH(itemView)
    }

    override fun onBindViewHolder(holder: ChallengeVH, position: Int) {
        val currentItem = challengeList[position]
        holder.bindView(currentItem)
    }

    override fun getItemCount(): Int {
        return challengeList.size
    }

    class ChallengeVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textQuestion: TextView = itemView.findViewById(R.id.textQuestion)
        private val root: LinearLayout = itemView.findViewById(R.id.root)

        fun bindView(item: ChallengeModel) {
            textQuestion.text = item.char.toString()
            if (item.isSelected) {
                val selectedBackground =
                    ContextCompat.getDrawable(itemView.context, R.drawable.selected_stroke_color)
                root.background = selectedBackground
            } else if (item.isCorrect) {
                val correctBackground =
                    ContextCompat.getDrawable(itemView.context, R.drawable.correct_stroke_color)
                root.background = correctBackground
            } else {
                val defaultBackground =
                    ContextCompat.getDrawable(itemView.context, R.drawable.default_stroke_color)
                root.background = defaultBackground
            }
        }
    }
}