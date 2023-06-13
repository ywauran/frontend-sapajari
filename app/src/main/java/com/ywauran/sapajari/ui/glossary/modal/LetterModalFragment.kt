package com.ywauran.sapajari.ui.glossary

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ywauran.sapajari.R

class LetterModalFragment : DialogFragment() {
    companion object {
        private const val ARG_SYMBOL = "symbol"
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(symbol: String, imageUrl: String): LetterModalFragment {
            val fragment = LetterModalFragment()
            val args = Bundle()
            args.putString(ARG_SYMBOL, symbol)
            args.putString(ARG_IMAGE_URL, imageUrl)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var descriptionTextView: TextView
    private lateinit var letterImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_letter_modal, container, false)
        descriptionTextView = rootView.findViewById(R.id.tv_description_letter_modal)
        letterImageView = rootView.findViewById(R.id.iv_letter_modal)

        arguments?.let {
            val symbol = it.getString(ARG_SYMBOL)
            val imageUrl = it.getString(ARG_IMAGE_URL)

            descriptionTextView.text = symbol

            imageUrl?.let { image ->
                Glide.with(this)
                    .load("http://192.168.1.7:8000/images/letters/${image}")
                    .into(letterImageView)
            }
        }

        return rootView
    }
}
