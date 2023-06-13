package com.ywauran.sapajari.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ywauran.sapajari.data.remote.response.User
import com.ywauran.sapajari.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var fullName: String
    private lateinit var userUid: String
    private lateinit var usersRef: DatabaseReference
    private lateinit var usersListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userUid)
        usersListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    fullName = user?.fullName ?: ""

                    updateUI()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the database retrieval error if needed
            }
        }
    }

    override fun onResume() {
        super.onResume()
        usersRef.addValueEventListener(usersListener)
    }

    override fun onPause() {
        super.onPause()
        usersRef.removeEventListener(usersListener)
    }

    private fun updateUI() {
        binding.tvFullname.text = fullName
    }
}