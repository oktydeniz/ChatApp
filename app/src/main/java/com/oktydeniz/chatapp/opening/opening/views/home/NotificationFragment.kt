package com.oktydeniz.chatapp.opening.opening.views.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.databinding.FragmentNotificationBinding
import com.oktydeniz.chatapp.opening.opening.adapters.NotificationsPageAdapter
import com.oktydeniz.chatapp.opening.opening.models.NotificationModel
import com.oktydeniz.chatapp.opening.opening.utils.RecyclerViewClickInterface
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class NotificationFragment : Fragment(), RecyclerViewClickInterface {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser!!.uid
    private lateinit var firebasefirestore: FirebaseFirestore
    private var userKeyList = ArrayList<NotificationModel>()
    private lateinit var adapter: NotificationsPageAdapter
    private lateinit var from: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        adapter = NotificationsPageAdapter(userKeyList, this)
        firebasefirestore = FirebaseFirestore.getInstance()
        getNotifications()
    }

    private fun getNotifications() {
        userKeyList.clear()
        firebasefirestore.collection("Requests").get().addOnSuccessListener {
            it?.let {
                for (doc in it) {
                    if (doc.id != user) {
                        if (doc.data["status"] == "sending") {
                            from = doc["from"].toString()
                            firebasefirestore.collection("Users").document(from).get()
                                .addOnSuccessListener { data ->
                                    data?.let { item ->
                                        val userName = item.data?.get("userName").toString()
                                        val userImage = item.data?.get("userImage").toString()
                                        val notification =
                                            NotificationModel(userName, userImage, from)
                                        userKeyList.add(notification)
                                        adapter.notifyDataSetChanged()
                                        binding.notificationRecyclerView.adapter = adapter
                                    }
                                }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemClick(position: Int) {}

    // show user profile
    override fun onItemClick(userId: String) {
        val action =
            NotificationFragmentDirections.actionBottomNotificationsFragmentToOneUserProfileFragment()
        action.userİd = userId
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    //accept friends request
    override fun onItemClickYes(userId: String, otherUserId: String) {
        val hasMap = HashMap<String, Any>()
        hasMap["date"] = FieldValue.serverTimestamp()
        firebasefirestore.collection(userId).document(otherUserId).set(hasMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    firebasefirestore.collection(otherUserId).document(userId).set(hasMap)
                        .addOnSuccessListener {
                            Toast.makeText(context, getString(R.string.done), Toast.LENGTH_SHORT)
                                .show()
                            //if possess success delete friends request for each user
                            onItemClickNo(userId, otherUserId)
                        }
                }
            }
        adapter.notifyDataSetChanged()
    }

    //delete friends request
    override fun onItemClickNo(userId: String, otherUserId: String) {
        firebasefirestore.collection("Requests").document(userId).delete()
            .addOnSuccessListener {
                firebasefirestore.collection("Requests").document(otherUserId).delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, getText(R.string.done), Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        adapter.notifyDataSetChanged()

    }
}