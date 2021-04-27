package com.oktydeniz.chatapp.opening.opening.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentPasswordBinding


class PasswordFragment : Fragment() {
    private var auth = FirebaseAuth.getInstance()
    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.forgotPasswordSendMailBtn.setOnClickListener {
            val mail = binding.userMailForgotPage.text.toString()
            if (mail.isNotEmpty()) {
                auth.sendPasswordResetEmail(mail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val action =
                            PasswordFragmentDirections.actionPasswordFragmentToPasswordSecondFragment()
                        action.mail = mail
                        Navigation.findNavController(it).navigate(action)
                    } else {
                        Toast.makeText(context, getText(R.string.check_info), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                binding.userMailForgotPage.error = getString(R.string.null_message)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}