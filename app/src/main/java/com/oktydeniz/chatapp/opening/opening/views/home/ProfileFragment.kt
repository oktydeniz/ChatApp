package com.oktydeniz.chatapp.opening.opening.views.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentProfileBinding
import com.oktydeniz.chatapp.opening.opening.models.UserProfile
import com.oktydeniz.chatapp.opening.opening.views.auth.AuthActivity
import java.util.*
import kotlin.collections.HashMap

class ProfileFragment : Fragment() {
    private val TAG = "ProfileFragment"
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var storageReference: StorageReference
    private lateinit var firebasefirestore: FirebaseFirestore
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    private var uri: Uri? = null
    private lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editabilityFalse()
        init()
        getUserData()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        firebasefirestore = FirebaseFirestore.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        binding.userProfileUpdate.setOnClickListener {
            updateUserInfo()
        }
        binding.profileUserImage.setOnClickListener {
            selectUserImage()
        }
    }

    private fun getUserData() {
        val reference = firebasefirestore.collection("Users").document(auth.currentUser!!.uid)
        reference.get().addOnSuccessListener {
            it?.let {
                Log.i(TAG, "getUserData: id :  ${it.data.toString()}")
                it.data?.let { data ->
                    val name = data["userName"].toString()
                    val mail = data["userMail"].toString()
                    val about = data["aboutUser"].toString()
                    val education = data["education"].toString()
                    val birthday = data["userBirthday"].toString()
                    val image = data["userImage"].toString()
                    val userId = it.id
                    val user = UserProfile(name, mail, image, birthday, education, about, userId)
                    Log.i(TAG, "getUserData: $user")
                    binding.user = user
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_user_info -> {
                editabilityTrue()
            }
            R.id.user_sing_out -> {
                val user = FirebaseAuth.getInstance()
                user.signOut()
                val intent = Intent(requireContext(), AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                requireActivity().finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editabilityFalse() {
        binding.profileUserAbout.isEnabled = false
        binding.profileUserBirthday.isEnabled = false
        binding.profileUserEducation.isEnabled = false
        binding.profileUserName.isEnabled = false
        binding.userProfileUpdate.visibility = View.INVISIBLE
        binding.profileUserImage.isEnabled = false
    }

    private fun editabilityTrue() {
        binding.profileUserAbout.isEnabled = true
        binding.profileUserBirthday.isEnabled = true
        binding.profileUserEducation.isEnabled = true
        binding.profileUserName.isEnabled = true
        binding.userProfileUpdate.visibility = View.VISIBLE
        binding.profileUserImage.isEnabled = true

    }

    private fun updateUserInfo() {
        uri?.let {
            val name = binding.profileUserName.text.toString()
            val birthday = binding.profileUserBirthday.text.toString()
            val education = binding.profileUserEducation.text.toString()
            val about = binding.profileUserAbout.text.toString()
            val uuid = UUID.randomUUID()
            val imageName = "images/$uuid"
            storageReference.child(imageName).putFile(uri!!).addOnSuccessListener {

                val reference = FirebaseStorage.getInstance().getReference(imageName)
                reference.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    val user = auth.currentUser!!.uid
                    val mail = auth.currentUser!!.email
                    val map = HashMap<String, Any>()
                    map["userMail"] = mail!!
                    map["userName"] = name
                    map["userBirthday"] = birthday
                    map["education"] = education
                    map["aboutUser"] = about
                    map["userImage"] = url
                    firebasefirestore.collection("Users").document(user).update(map)
                        .addOnSuccessListener {
                            //Add Loading message
                        }.addOnFailureListener {

                        }
                }
            }
        }
    }

    private fun selectUserImage() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1001
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1002)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1002)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1002 && resultCode == Activity.RESULT_OK) {
            data?.let {
                uri = data.data
                try {
                    bitmap = if (Build.VERSION.SDK_INT >= 28) {
                        val imageSource =
                            ImageDecoder.createSource(requireContext().contentResolver, uri!!)
                        ImageDecoder.decodeBitmap(imageSource)
                    } else {
                        MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                    }
                    binding.profileUserImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}