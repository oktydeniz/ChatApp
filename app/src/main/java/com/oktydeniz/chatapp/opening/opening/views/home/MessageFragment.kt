package com.oktydeniz.chatapp.opening.opening.views.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.oktydeniz.chatapp.databinding.FragmentMessageBinding
import com.oktydeniz.chatapp.opening.opening.adapters.MessageAdapter
import com.oktydeniz.chatapp.opening.opening.models.MessageModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MessageFragment : Fragment() {

    private val TAG = "MessageFragment"
    private val args: MessageFragmentArgs by navArgs()
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private var id: String? = null
    private var messageModelList = ArrayList<MessageModel>()
    private var keyList = ArrayList<String>()
    private var reference: DatabaseReference? = null
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance().currentUser!!
    private lateinit var adapter: MessageAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        actions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessageBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun init() {
        id = args.userId
        binding.userNameMessage.text = args.userName
        reference = firebaseDatabase.reference
        adapter = MessageAdapter(keyList, messageModelList)
        binding.messageRecyclerView.adapter = adapter
        loadMessages()
    }

    private fun actions() {
        binding.sendMessageImage.setOnClickListener {

            /*val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)*/
            val dateNow = Calendar.getInstance().time
            sendMessage(
                auth.uid,
                args.userId,
                "text",
                dateNow.toString(),
                false,
                binding.messageArea.text.toString()
            )

        }
        binding.goBackImage.setOnClickListener {
            val action = MessageFragmentDirections.actionMessageFragmentToBottomHomeFragment()
            Navigation.findNavController(it).navigate(action)

        }
    }

    private fun sendMessage(
        currentUserId: String,
        userId: String,
        textType: String,
        time: String,
        seen: Boolean,
        messageText: String
    ) {

        if (reference != null) {
            val messageId = reference!!.child("Message").child(auth.uid).push().key
            val hasMap = HashMap<String, Any>()
            hasMap["type"] = textType
            hasMap["seen"] = seen
            hasMap["time"] = time
            hasMap["text"] = messageText
            hasMap["from"] = auth.uid

            reference!!.child("Message").child(currentUserId).child(userId)
                .child(messageId.toString())
                .setValue(hasMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        reference!!.child("Message").child(userId).child(currentUserId)
                            .child(messageId.toString()).setValue(hasMap).addOnCompleteListener {
                                binding.messageArea.setText("")
                            }
                    }
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun loadMessages() {
        reference?.child("Message")?.child(auth.uid)!!.child(id!!)
            .addChildEventListener(childEventListener)
    }

    private val childEventListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val model = snapshot.getValue(MessageModel::class.java)
            model?.let {
                Log.i(TAG, "onChildAdded:  $model")
                messageModelList.add(it)
                adapter.notifyDataSetChanged()
                snapshot.key?.let { it1 -> keyList.add(it1) }
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }

    }
}

