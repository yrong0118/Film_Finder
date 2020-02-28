package com.yue.mymovie.Chat


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.LoginOrRegister.User

import com.yue.mymovie.R
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_chat_log.*

/**
 * A simple [Fragment] subclass.
 */
class ChatLogFragment : Fragment() {

    lateinit var recyclerView : RecyclerView
    lateinit var logChatHeader : TextView
    lateinit var goBackToChatList: ImageView
    lateinit var sendMessage: Button

    companion object {
        var chatToUser : User? = null
        var selectedList = arrayListOf<User>()
        val TAG = "ChatLog Frafment"
        fun newInstance(list : ArrayList<User>): ChatLogFragment {
            selectedList = list
            var args = Bundle()
            var fragment = ChatLogFragment()
            fragment.setArguments(args)
            return fragment
        }

        var adapter = GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chat_log, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_chat_log)
        logChatHeader = view.findViewById(R.id.group_member_text_Logchat)
        goBackToChatList = view.findViewById(R.id.ic_go_back_Logchat)
        sendMessage = view.findViewById(R.id.send_button_chat_log)
        logChatHeader.text = getLogChatHead(selectedList)

        recyclerView.adapter = adapter

        goBackToChatList.setOnClickListener{

        }

//        setDummyData()
        listenForMessages()

        sendMessage.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performSendMessage(selectedList)
        }

        return view
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG, chatMessage?.text)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = ChatListFragment.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text,currentUser))
                    } else {
                        fetchChatTotUser()
                        chatToUser ?: return
                        adapter.add(ChatToItem(chatMessage.text,chatToUser!!))
                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })
    }


    private fun fetchChatTotUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                chatToUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${chatToUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
//    private fun setDummyData() {
//        var adapter = GroupAdapter<GroupieViewHolder>()
//
//
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        recyclerView.adapter = adapter
//    }

    private fun getLogChatHead(selectedList: ArrayList<User>):String{
        var res = ""
        var count  = 0
        selectedList.forEach{
            if (count != selectedList.size - 1){
                res += it.username + ", "
//                Log.d(TAG,it.username)

            } else {
                res += it.username
            }
            count ++
        }
        Log.d(TAG,res)
        return res

    }


    private fun performSendMessage(selectedList :ArrayList<User>) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val toId = getToId(selectedList)

        if (fromId == null) return

    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
//        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
//
//        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
//                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }


//        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
//        latestMessageRef.setValue(chatMessage)
//
//        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
//        latestMessageToRef.setValue(chatMessage)
    }

    private fun getToId(selectedList: ArrayList<User>) : ArrayList<String>{
        var resultList = arrayListOf<String>()
        var iterator = selectedList.iterator()
        iterator.forEach(){
            resultList.add(it.uid)
        }
        return resultList
    }

}


