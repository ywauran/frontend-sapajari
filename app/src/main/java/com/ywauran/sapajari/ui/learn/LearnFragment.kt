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
import com.ywauran.sapajari.data.remote.response.RandomChallengeResponse
import com.ywauran.sapajari.databinding.FragmentLearnBinding
import com.ywauran.sapajari.model.ChallengeModel
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
        categoryChallengeAdapter = CategoryChallengeAdapter { categoryChallengeResponse ->

            val id = categoryChallengeResponse?.id ?: 4
            val challenge = mutableListOf<ChallengeModel>()
            fetchRandomChallenge(
                id,
                { text ->
                    val uppercaseText = text?.toUpperCase()

                    // Use the converted uppercase text here
                    uppercaseText?.let {
                        for (i in 0 until it.length) {
                            if (i == 0) {
                                challenge.add(ChallengeModel(it[i], false, true))
                            } else {
                                challenge.add(ChallengeModel(it[i], false, false))
                            }
                        }
                    }

                    Log.e("", challenge.toString())
                    ChallengeActivity.start(requireContext(), challenge)
                },
                { errorMessage ->
                    // Handle the error message
                    Log.e("LearnFragment", errorMessage)
                }
            )
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

    private fun fetchRandomChallenge(
        id: Int,
        successCallback: (text: String) -> Unit,
        errorCallback: (errorMessage: String) -> Unit
    ) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.getRandomChallenge(id)
        call.enqueue(object : Callback<List<RandomChallengeResponse>> {
            override fun onResponse(
                call: Call<List<RandomChallengeResponse>>,
                response: Response<List<RandomChallengeResponse>>
            ) {
                if (response.isSuccessful) {
                    val randomChallengeResponseList = response.body()
                    if (randomChallengeResponseList != null && randomChallengeResponseList.isNotEmpty()) {
                        // Assuming you only want to handle the first item in the list
                        val randomChallengeResponse = randomChallengeResponseList[0]

                        val text = randomChallengeResponse?.text ?: ""
                        Log.e("", text)
                        successCallback(text)
                    } else {
                        // Handle empty response body
                        errorCallback("Empty response body")
                    }
                } else {
                    // Handle API error
                    val errorMessage = response.errorBody()?.string()
                    val errorCode = response.code()
                    errorCallback("API Error: $errorCode - $errorMessage")
                }
            }

            override fun onFailure(call: Call<List<RandomChallengeResponse>>, t: Throwable) {
                // Handle network failure
                errorCallback("Network Error: ${t.message}")
            }
        })

    }



    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
