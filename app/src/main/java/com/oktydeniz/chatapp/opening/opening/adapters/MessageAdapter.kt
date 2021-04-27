package com.oktydeniz.chatapp.opening.opening.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.oktydeniz.chatapp.R
import com.oktydeniz.chatapp.opening.opening.models.MessageModel

class MessageAdapter(
    private val userKeyList: List<String>,
    private val messageModel: List<MessageModel>
) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val TAG = "MessageAdapter"

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference? = null
    private var userId: String? = null
    private var auth: FirebaseAuth? = null
    private var user: FirebaseUser? = null
    private val viewTypeSend = 1
    private val viewTypeReceived = 2
    var state: Boolean = false

    init {
        databaseReference = firebaseDatabase.reference
        auth = FirebaseAuth.getInstance()
        user = auth!!.currentUser
        userId = user!!.uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder: ViewType $viewType")
        val view: View = if (viewType == viewTypeSend) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_send_row, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.message_received_row, parent, false)
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = messageModel[position]
        Log.i(TAG, "onBindViewHolder:  Message ${model.text} ")
        holder.type().text = model.text
    }

    override fun getItemCount(): Int {
        return messageModel.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageModel[position].from.equals(userId)) {
            state = true
            viewTypeSend
        } else {
            state = false
            viewTypeReceived
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var textViewMessage: TextView
        fun type(): TextView {
            textViewMessage = if (state) {

                itemView.findViewById(R.id.sendingMessage)
            } else {
                itemView.findViewById(R.id.receivedMessage)
            }
            return textViewMessage
        }
    }

}