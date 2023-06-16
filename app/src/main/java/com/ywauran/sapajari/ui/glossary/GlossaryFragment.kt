package com.ywauran.sapajari.ui.glossary

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ywauran.sapajari.R
import com.ywauran.sapajari.adapter.NumberAdapter
import com.ywauran.sapajari.data.remote.response.LetterResponse
import com.ywauran.sapajari.data.remote.response.NumberResponse
import com.ywauran.sapajari.data.remote.response.User
import com.ywauran.sapajari.databinding.FragmentGlossaryBinding
import com.ywauran.sapajari.ui.adapter.LetterAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GlossaryFragment : Fragment() {
    private lateinit var binding: FragmentGlossaryBinding
    private lateinit var numberAdapter: NumberAdapter
    private lateinit var letterAdapter: LetterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGlossaryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchDataFromApi()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userUid)
        val usersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    val fullName = user?.fullName ?: ""
                    val firstName = fullName.split(" ").firstOrNull() ?: ""
                    val fullText = "Hi, $firstName"
                    updateUI(fullText)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the database retrieval error if needed
            }
        }

        usersRef.addValueEventListener(usersListener)
    }

    private fun setupRecyclerView() {
        numberAdapter = NumberAdapter { numberResponse ->
            val numberModalFragment = NumberModalFragment.newInstance(numberResponse.description.toString(), numberResponse.url.toString())
            numberModalFragment.show(parentFragmentManager, "NumberModalFragment")
        }

        binding.rvNumber.apply {
            layoutManager = GridLayoutManager(requireContext(), 4)
            adapter = numberAdapter
        }

        letterAdapter = LetterAdapter { letterResponse ->
            val letterModalFragment = LetterModalFragment.newInstance(letterResponse.description.toString(), letterResponse.url.toString())
            letterModalFragment.show(parentFragmentManager, "LetterModalFragment")
        }

        binding.rvLetter.apply {
            layoutManager = GridLayoutManager(requireContext(), 4) // Set 4 columns
            adapter = letterAdapter
        }
    }

    private fun fetchDataFromApi() {
        val apiService = ApiConfig.getApiService()

        val numbersCall = apiService.getNumbers()
        numbersCall.enqueue(object : Callback<List<NumberResponse>> {
            override fun onResponse(
                call: Call<List<NumberResponse>>,
                response: Response<List<NumberResponse>>
            ) {
                if (response.isSuccessful) {
                    val numberResponseList = response.body()
                    if (numberResponseList != null) {
                        numberAdapter.setItems(numberResponseList)
                    }
                } else {
                    // Handle API error
                    Log.e("FragmentGlossary", "Numbers API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<NumberResponse>>, t: Throwable) {
                // Handle network failure
                Log.e("FragmentGlossary", "Numbers Network Error: ${t.message}")
            }
        })

        val lettersCall = apiService.getLetters()
        lettersCall.enqueue(object : Callback<List<LetterResponse>> {
            override fun onResponse(
                call: Call<List<LetterResponse>>,
                response: Response<List<LetterResponse>>
            ) {
                if (response.isSuccessful) {
                    val letterResponseList = response.body()
                    if (letterResponseList != null) {
                        letterAdapter.setItems(letterResponseList)
                    }
                } else {
                    // Handle API error
                    Log.e("FragmentGlossary", "Letters API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<LetterResponse>>, t: Throwable) {
                // Handle network failure
                Log.e("FragmentGlossary", "Letters Network Error: ${t.message}")
            }
        })
    }

    private fun updateUI(fullText: String) {
        binding.tvHi.text = fullText
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
