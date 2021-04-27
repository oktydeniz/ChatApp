package com.oktydeniz.chatapp.opening.opening.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentOneUserProfileBinding
import com.oktydeniz.chatapp.opening.opening.models.UserProfile

class OneUserProfileFragment : Fragment() {

    private val TAG = "OneUserProfileFragment"
    private var _binding: FragmentOneUserProfileBinding? = null
    private val binding get() = _binding!!
    private val args: OneUserProfileFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!.uid
    private lateinit var firebasefirestore: FirebaseFirestore
    private lateinit var user: UserProfile
    private var checkFriends: String = ""
    private lateinit var otherUserId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOneUserProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        firebasefirestore = FirebaseFirestore.getInstance()
        val arg = args.userİd
        if (arg != "temp") {
            getUser(arg)
        }
    }

    //Get selected user from db
    private fun getUser(str: String) {
        firebasefirestore.collection("Users").document(str).addSnapshotListener { value, error ->
            if (error != null) Toast.makeText(context, error.localizedMessage, Toast.LENGTH_SHORT)
                .show()
            value?.let {
                it.let { i ->
                    val userName = i["userName"].toString()
                    val userMail = i["userMail"].toString()
                    val userImage = i["userImage"].toString()
                    val aboutUser = i["aboutUser"].toString()
                    val education = i["education"].toString()
                    val userBirthday = i["userBirthday"].toString()
                    val userId = i.id
                    user = UserProfile(
                        userName,
                        userMail,
                        userImage,
                        userBirthday,
                        education,
                        aboutUser,
                        userId
                    )
                    binding.user = user
                    selectedUserId()
                }
            }
        }
    }

    // get selected user id from db
    private fun selectedUserId() {
        firebasefirestore.collection("Users").whereEqualTo("userMail", user.userMail)
            .addSnapshotListener { value, error ->
                value?.let {
                    Log.i(TAG, "actions: ${it.documents[0].id}")
                    otherUserId = it.documents[0].id
                    controls()
                }
            }
        actions()
    }

    // does the selected user have friend requests from current user
    private fun controls() {
        firebasefirestore.collection("Requests").document(otherUserId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                it.let {
                    val document = it.result
                    if (document!!.exists()) {
                        checkFriends = document.get("status").toString()
                        Log.i(TAG, "controls: $checkFriends Request Exists")
                        binding.sendFriendsRequest.setImageResource(R.drawable.friends)
                        binding.friendsRequestText.text = getString(R.string.cancel_the_request)
                    }
                }
            }
        }
        firebasefirestore.collection(currentUser).document(args.userİd)
            .addSnapshotListener { value, error ->
                error?.let {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
                value?.let {
                    if (!it.exists()) {
                        binding.sendFriendsRequest.visibility = View.INVISIBLE
                        binding.friendsRequestText.visibility = View.INVISIBLE
                    }
                }
            }
    }

    //Events
    private fun actions() {
        binding.sendFriendsRequest.setOnClickListener {
            if (checkFriends != "") {
                cancelFriendsRequest(currentUser, otherUserId)
            } else {
                sendFriendsRequest(currentUser, otherUserId)
            }
        }
        binding.startMessage.setOnClickListener {
            val action =
                OneUserProfileFragmentDirections.actionOneUserProfileFragmentToMessageFragment()
            action.userId = args.userİd
            action.userName = user.userName
            Navigation.findNavController(it).navigate(action)
        }
    }

    //if there is no friends request send
    private fun sendFriendsRequest(user: String, otherUser: String) {
        Log.i(TAG, "sendFriendsRequest: User : $user  and $otherUser ")
        val hasMap = HashMap<String, Any>()
        hasMap["from"] = user
        hasMap["to"] = otherUser
        hasMap["status"] = "sending"
        firebasefirestore.collection("Requests").document(user).set(hasMap)
            .addOnSuccessListener {
                val hasMapTwo = HashMap<String, Any>()
                hasMapTwo["from"] = user
                hasMapTwo["to"] = otherUser
                hasMapTwo["status"] = "receipt"
                firebasefirestore.collection("Requests").document(otherUser).set(hasMapTwo)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), getText(R.string.done), Toast.LENGTH_SHORT)
                            .show()
                    }
            }

    }

    //if there is  friends request cancel it
    private fun cancelFriendsRequest(currentUser: String, otherUserId: String) {
        checkFriends = ""
        firebasefirestore.collection("Requests").document(currentUser).delete()
            .addOnSuccessListener {
                firebasefirestore.collection("Requests").document(otherUserId).delete()
                    .addOnSuccessListener {

                        Toast.makeText(requireContext(), getText(R.string.done), Toast.LENGTH_SHORT)
                            .show()
                        binding.sendFriendsRequest.setImageResource(R.drawable.add_friends)
                        binding.friendsRequestText.text = getString(R.string.send_friends_request)
                    }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}