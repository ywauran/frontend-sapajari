package com.ywauran.sapajari.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ywauran.sapajari.databinding.FragmentProfileBinding
import com.ywauran.sapajari.ui.auth.AuthActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var fullName: String
    private lateinit var profilePhotoUri: String
    private lateinit var userUid: String
    private lateinit var usersRef: DatabaseReference
    private lateinit var usersListener: ValueEventListener
    private lateinit var logoutDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutDialog = AlertDialog.Builder(requireContext())
            .setTitle("Keluar")
            .setMessage("Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                // Perform the logout operation here
                logout()
            }
            .setNegativeButton("Tidak", null)
            .create()

        binding.btnLogout.setOnClickListener {
            logoutDialog.show()
        }

        binding.btnProfil.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}