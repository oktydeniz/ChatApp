package com.oktydeniz.chatapp.opening.opening.views.home


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.oktydeniz.chatapp.databinding.FragmentHomeBinding
import com.oktydeniz.chatapp.opening.opening.adapters.HomePageAdapter
import com.oktydeniz.chatapp.opening.opening.models.UserProfile
import com.oktydeniz.chatapp.opening.opening.utils.RecyclerViewClickInterface

class HomeFragment : Fragment(), RecyclerViewClickInterface {
    private val TAG = "HomeFragment"
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebasefirestore: FirebaseFirestore
    private var userList = ArrayList<UserProfile>()
    private lateinit var adapter: HomePageAdapter
    private val auth = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }

    private fun init() {
        adapter = HomePageAdapter(userList,this)
        firebasefirestore = FirebaseFirestore.getInstance()
        getAllUsers()

    }

    //get all users from db
    private fun getAllUsers() {
        userList.clear()
        firebasefirestore.collection("Users").get().addOnSuccessListener {
            it?.let {
                for (doc in it) {
                    if (doc.id != auth!!.uid) {
                        val userName = doc.data["userName"].toString()
                        val userMail = doc.data["userMail"].toString()
                        val userImage = doc.data["userImage"].toString()
                        val aboutUser = doc.data["aboutUser"].toString()
                        val education = doc.data["education"].toString()
                        val userBirthday = doc.data["userBirthday"].toString()
                        val userId = doc.id
                        Log.i(TAG, "getAllUsers: user Id : $userId")
                        val user = UserProfile(
                            userName,
                            userMail,
                            userImage,
                            userBirthday,
                            education,
                            aboutUser,
                            userId
                        )
                        userList.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
                binding.homePageRecyclerView.adapter = adapter
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(position: Int) {

    }

    override fun onItemClick(userId: String) {
        val action =
            HomeFragmentDirections.actionBottomHomeFragmentToOneUserProfileFragment()
        action.userİd = userId
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    override fun onItemClickYes(userId: String, otherUserId: String) {
        TODO("Not yet implemented")
    }

    override fun onItemClickNo(userId: String, otherUserId: String) {
        TODO("Not yet implemented")
    }
}