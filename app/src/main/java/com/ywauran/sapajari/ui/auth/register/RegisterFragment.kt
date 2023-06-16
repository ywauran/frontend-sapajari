package com.ywauran.sapajari.ui.auth.register

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.ywauran.sapajari.data.remote.response.User
import com.ywauran.sapajari.databinding.FragmentRegisterBinding
import com.ywauran.sapajari.ui.auth.AuthActivity

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPass = binding.etConfirmPassword.text.toString()
            val fullName = binding.etFullname.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (password == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { registrationTask ->
                            if (registrationTask.isSuccessful) {
                                saveUserData(email, fullName)
                                Toast.makeText(
                                    requireContext(),
                                    "Berhasil Daftar, Silahkan Masuk!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(requireContext(), AuthActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Registration failed: ${registrationTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Kata Sandi Tidak Cocok", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Masih ada inputan yang kosong!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveUserData(email: String, fullName:String) {
        val sharedPref = requireActivity().getSharedPreferences("UserData", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("email", email)
        editor.putString("fullName", fullName)
        editor.apply()

        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        val user = User(fullName, "")

        userUid?.let {
            val usersRef = FirebaseDatabase.getInstance().reference.child("users")
            usersRef.child(it).setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Berhasil Daftar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        requireContext(),
                        "Failed to save user data: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
