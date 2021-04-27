package com.oktydeniz.chatapp.opening.opening.views.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentSingInBinding
import com.oktydeniz.chatapp.opening.opening.views.home.HomeActivity

class SingInFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding: FragmentSingInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingInBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        binding.singInToMainPage.setOnClickListener {
            singIn()
        }
        binding.textSingUp.setOnClickListener {
            val action = SingInFragmentDirections.actionSingInFragmentToSingUpFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.textForgotPassword.setOnClickListener {
            val action = SingInFragmentDirections.actionSingInFragmentToPasswordFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun singIn() {
        val mail = binding.userMail.text.toString()
        val password = binding.userPassword.text.toString()
        if (mail.isNotEmpty() && password.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail)
                .matches()
        ) {
            firebaseAuth.signInWithEmailAndPassword(
                mail, password
            ).addOnSuccessListener {
                Toast.makeText(requireContext(), getString(R.string.welcome), Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        } else {
            if (mail.isEmpty()) binding.userMail.error = getString(R.string.null_message)
            if (password.isEmpty()) binding.userPassword.error = getString(R.string.null_message)
            //binding.userMail.requestFocus()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}