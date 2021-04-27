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
import com.google.firebase.firestore.FirebaseFirestore
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentSingUpBinding
import com.oktydeniz.chatapp.opening.opening.views.home.HomeActivity


class SingUpFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebasefrs: FirebaseFirestore
    private var _binding: FragmentSingUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSingUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebasefrs = FirebaseFirestore.getInstance()
        actions()
    }

    private fun actions() {
        binding.iHaveAccountText.setOnClickListener {
            val actions = SingUpFragmentDirections.actionSingUpFragmentToSingInFragment()
            Navigation.findNavController(it).navigate(actions)
        }
        binding.registerButton.setOnClickListener {
            val mail = binding.userMailSingUp.text.toString()
            val userName = binding.userNameSingUp.text.toString()
            if (checkPassword() && checkUserMailName()) {
                val password = binding.userSingUpPassword.text.toString()
                singUp(userName, mail, password)
            } else {
                Toast.makeText(requireContext(), getString(R.string.check_info), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun singUp(username: String, mail: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(mail, password).addOnSuccessListener {
            Toast.makeText(requireContext(), getString(R.string.welcome), Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            requireActivity().finish()
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser!!.uid
                val hasMap = HashMap<String, Any>()
                hasMap["userMail"] = mail
                hasMap["userName"] = username
                hasMap["userBirthday"] = "null"
                hasMap["education"] = "null"
                hasMap["aboutUser"] = "null"
                hasMap["userImage"] = "https://medicine.nus.edu.sg/pcm/wp-content/uploads/sites/5/2021/01/default-avatar-profile-icon-grey-1.jpg"
                firebasefrs.collection("Users").document(user).set(hasMap)
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPassword(): Boolean {
        return (binding.userSingUpPassword.text.toString() == binding.userPasswordCheck.text.toString())
                && binding.userSingUpPassword.text.toString().length >= 6
    }

    private fun checkUserMailName(): Boolean {
        return binding.userMailSingUp.text.toString().trim()
            .isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(
            binding.userMailSingUp.text.toString().trim()
        ).matches() && binding.userNameSingUp.text.toString().trim().isNotEmpty()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}