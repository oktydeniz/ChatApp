package com.oktydeniz.chatapp.opening.opening.views.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentPasswordSecondBinding

class PasswordSecondFragment : Fragment() {
    private var _binding: FragmentPasswordSecondBinding? = null
    private val binding get() = _binding!!
    private val args: PasswordSecondFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasswordSecondBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val mail = args.mail
        binding.sendAgainMail.setOnClickListener {
            if (mail != "null") {
                auth.sendPasswordResetEmail(mail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT).show()
                        val action =
                            PasswordSecondFragmentDirections.actionPasswordSecondFragmentToSingInFragment()
                        Navigation.findNavController(it).navigate(action)
                    } else {
                        Toast.makeText(context, getText(R.string.check_info), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        binding.backToSingInPageBtn.setOnClickListener {
            val action =
                PasswordSecondFragmentDirections.actionPasswordSecondFragmentToSingInFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}