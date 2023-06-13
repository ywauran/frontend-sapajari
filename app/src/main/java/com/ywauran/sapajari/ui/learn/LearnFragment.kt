package com.ywauran.sapajari.ui.learn

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.ywauran.sapajari.adapter.CategoryChallengeAdapter
import com.ywauran.sapajari.data.remote.response.CategoryChallengeResponse
import com.ywauran.sapajari.databinding.FragmentLearnBinding
import com.ywauran.sapajari.ui.challenge.ChallengeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LearnFragment : Fragment() {

    private lateinit var binding: FragmentLearnBinding
    private lateinit var categoryChallengeAdapter: CategoryChallengeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLearnBinding.inflate(inflater, container, false)
        setupRecyclerView()
        fetchDataFromApi()
        return binding.root
    }

    private fun setupRecyclerView() {
        categoryChallengeAdapter = CategoryChallengeAdapter {categoryChallengeResponse ->
            val intent = Intent(requireContext(), ChallengeActivity::class.java)
            startActivity(intent)
        }

        binding.rvCategoryLesson.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryChallengeAdapter
        }
    }

    private fun fetchDataFromApi() {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getCategoryChallenges()

        call.enqueue(object : Callback<List<CategoryChallengeResponse>> {
            override fun onResponse(
                call: Call<List<CategoryChallengeResponse>>,
                response: Response<List<CategoryChallengeResponse>>
            ) {
                if (response.isSuccessful) {
                    val categoryLessonResponseList = response.body()
                    if (categoryLessonResponseList != null) {
                        categoryChallengeAdapter.setItems(categoryLessonResponseList)
                    }
                } else {
                    // Handle API error
                    Log.e("LearnFragment", "API Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<CategoryChallengeResponse>>, t: Throwable) {
                // Handle network failure
                Log.e("LearnFragment", "Network Error: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}

