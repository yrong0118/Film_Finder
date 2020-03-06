package com.yue.mymovie.Chat


import android.app.DatePickerDialog
import android.icu.text.Edits
import android.media.MediaDrm
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
import com.yue.mymovie.Chat.ChatModel.*
import com.yue.mymovie.LoginOrRegister.User

import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.Util.Companion.getLogChatHead
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
        var selectedList = arrayListOf<User>()
        var groupOrSingleID: String? = ""
        val TAG = "ChatLog Frafment"

        fun newInstance(list : ArrayList<User>,groupOrSingleId: String): ChatLogFragment {
            groupOrSingleID = groupOrSingleId
            selectedList = list
            var args = Bundle()
            var fragment = ChatLogFragment()
            fragment.setArguments(args)
            return fragment
        }
        val currentUserUID = Util.getCurrentUserUid()
//        var currentUser :User?=null
        var chatToUser : User? = null
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
        adapter.clear()

        goBackToChatList.setOnClickListener{

        }


//        setDummyData()
        Util.fetchCurrentUser2 { currentUser ->

            listenForMessages(groupOrSingleID!!,currentUser)

            sendMessage.setOnClickListener {

                if (selectedList.size > 1) {
                    Log.d(TAG, "Attempt to send message.... to the group")
                    performSendMessageToGroup(selectedList)
                }else {
                    Log.d(TAG, "Attempt to send message.... to the single")
                    performSendMessageToSingle(selectedList,currentUser)
                }
//               listenForMessages(groupOrSingleID!!,currentUser)

            }
        }


        return view
    }

    private fun listenForMessages(groupOrSingleId: String,currentUser: User) {
        var ref : DatabaseReference
        if (selectedList.size > 1){
            ref = FirebaseDatabase.getInstance().getReference("/${Util.GROUPCHATS}/${groupOrSingleId}/messsages")
        } else {
            ref = FirebaseDatabase.getInstance().getReference("/${Util.TWOPERSONCHATS}/${groupOrSingleId}/messages")
        }

        ref.addChildEventListener(object :ChildEventListener{
            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot:  DataSnapshot, p1: String?) {

                Log.d(TAG,"@@@@@@@")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val messageOrVote= dataSnapshot.getValue(MessageType::class.java)

                if (messageOrVote != null) {
                    if (messageOrVote.messageType == Util.MESSAGE) {
                        Log.d(TAG, messageOrVote!!.messageId)

                        val messRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}")
                        messRef.addChildEventListener(object :ChildEventListener{
                            override fun onChildRemoved(p0: DataSnapshot) {

                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                            }

                            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
                                if (chatMessage != null && chatMessage.messageId.equals(messageOrVote.messageId)){
                                    Log.d(TAG, chatMessage!!.messageId)
                                    if (chatMessage.sendUserId.equals(currentUserUID)) {
                                        adapter.add(
                                            ChatFromItem(
                                                chatMessage.text,
                                                currentUser
                                            )
                                        )
                                    } else {
                                        Util.fetchUser(chatMessage.sendUserId){ chatToUser->
                                            adapter.add(
                                                ChatToItem(
                                                    chatMessage.text,
                                                    chatToUser
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
                                if (chatMessage != null && chatMessage.messageId == messageOrVote.messageId) {
                                    Log.d(TAG, chatMessage!!.messageId)
                                    if (chatMessage.sendUserId == currentUserUID) {
                                        adapter.add(
                                            ChatFromItem(
                                                chatMessage.text,
                                                currentUser
                                            )
                                        )
                                    } else {
                                        Util.fetchUser(chatMessage.sendUserId){ chatToUser->
                                            adapter.add(
                                                ChatToItem(
                                                    chatMessage.text,
                                                    chatToUser
                                                )
                                            )
                                        }
                                    }
                                }

                            }

                        })
                    }
                }
            }
        })
    }



    private fun performSendMessageToGroup(selectedList :ArrayList<User>) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (currentUserUID == "") return
        val timestamp = System.currentTimeMillis() / 1000
        val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()
        val chatMessage = ChatMessage(messageRef.key!!,currentUserUID,text,timestamp)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message in the message table: ${messageRef.key}")
            }
        val groupRef = FirebaseDatabase.getInstance().getReference("/${Util.GROUPCHATS}/${groupOrSingleID}/messages").push()
        val messageType = MessageType(Util.MESSAGE,messageRef.key!!)
        groupRef.setValue(messageType)
            .addOnSuccessListener {
                Log.d(TAG, "Saved messageType in the group table: ${messageRef.key}, $groupOrSingleID")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
         addListOnGroupMember(groupOrSingleID!!,text,selectedList,timestamp)
    }


    private fun performSendMessageToSingle(selectedList :ArrayList<User>,currentUser: User) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (currentUserUID == "") return
        val timestamp = System.currentTimeMillis() / 1000
        val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()

        val chatMessage = ChatMessage(messageRef.key!!, currentUserUID,text,timestamp)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message in the message table: ${messageRef.key}")
            }
        val twoPersonRef = FirebaseDatabase.getInstance().getReference("/${Util.TWOPERSONCHATS}/${groupOrSingleID}/messages").push()

        val messageType = MessageType(Util.MESSAGE,messageRef.key!!)
        twoPersonRef.setValue(messageType)
            .addOnSuccessListener {
                Log.d(TAG, "Saved messageType in the twoPersonChat table: ${messageRef.key}, $groupOrSingleID")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        addListOnSingleMember(groupOrSingleID!!,text,selectedList,timestamp,currentUser)

    }

    private fun addListOnSingleMember(singleChatID: String, text:String, selectedList: ArrayList<User>,timestamp : Long,currentUser: User) {
        var  iterator = selectedList.iterator()
        var memberRef :DatabaseReference? = null

        updateSenderMessageList(singleChatID!!, selectedList.get(0)!!.username, selectedList.get(0)!!.profileImageUrl,text,timestamp)

        iterator.forEach {
            if (it.uid != currentUserUID){
                memberRef = FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/${it.uid}/${singleChatID}")

                Util.fetchUser(singleChatID){singleUser->

                    val latestMessage = ListLatestMessage(singleChatID,singleUser.username,singleUser.profileImageUrl,text,false,timestamp)
                    memberRef!!.setValue(latestMessage)
                        .addOnSuccessListener {
                            Log.d(TAG, "add the TwoPersonChatId to the list of member: ${memberRef!!.key}, ${singleChatID}")
                        }
                }

            }

        }
    }

    private fun addListOnGroupMember(groupID: String?, text:String, selectedList: ArrayList<User>,timestamp: Long) {

        var  iterator = selectedList.iterator()
        var memberRef :DatabaseReference? = null
        updateSenderMessageList(groupID!!, getLogChatHead(selectedList),"",text,timestamp)

        iterator.forEach {
            memberRef = FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/${it.uid}/${groupID}")
            val latestMessage = ListLatestMessage(groupID!!,getLogChatHead(selectedList),"",text,false,timestamp)
            memberRef!!.setValue(latestMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "add the groupid to the list of member: ${memberRef!!.key}, ${groupID}")
                }
        }
    }

    private fun updateSenderMessageList(groupOrTwoPersonChatID:String,username: String, imageUri: String,text: String,timestamp: Long) {

        val memberRef = FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/${currentUserUID}/${groupOrTwoPersonChatID}")

        var latestMessage = ListLatestMessage(groupOrTwoPersonChatID!!,username,imageUri,text,true,timestamp)


        memberRef!!.setValue(latestMessage)
            .addOnSuccessListener {
                Log.d(TAG, "add the groupOrSingleId to the list of member: ${memberRef!!.key}, ${groupOrTwoPersonChatID}")
            }

    }

//    private fun getToId(selectedList: ArrayList<User>) : ArrayList<String>{
//        var resultList = arrayListOf<String>()
//        var iterator = selectedList.iterator()
//        iterator.forEach(){
//            resultList.add(it.uid)
//        }
//        return resultList
//    }

//    fun foundChatTotUser(sendUserUid: String) {
//        val ref = FirebaseDatabase.getInstance().getReference("/${Util.USERS}/$sendUserUid")
//        Log.d(TAG, "sendUserUid: ${sendUserUid}")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) {
//                val p0User = p0.getValue(User::class.java)
//                Log.d(TAG, "p0User?.uid  ${p0User?.uid}    ${p0User?.profileImageUrl}")
//                if (p0User?.uid .equals(sendUserUid)){
//                    chatToUser = p0User
//                    Log.d(TAG, " equals${chatToUser?.uid}    ${chatToUser?.profileImageUrl}.")
//                }
//
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//        Log.d(TAG, "final  toChatUser ${chatToUser?.uid}")
//    }

    //    private fun setDummyData() {
//        var adapter = GroupAdapter<GroupieViewHolder>()
//
//
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//        adapter.add(ChatFromItem("From message........"))
//        adapter.add(ChatToItem("this message is toooooooooooo........"))
//    }
//    private fun fetchChatTotUser1(sendUserUid: String) :User?{
//        val ref = FirebaseDatabase.getInstance().getReference("/${Util.USERS}/$sendUserUid")
//        Log.d(TAG, "sendUserUid: ${sendUserUid}")
//        var iii:User? = null
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//            override fun onDataChange(p0: DataSnapshot) {
//                val p0User=p0.getValue(User::class.java)
//                Log.d(TAG, "p0User?.uid  ${p0User?.uid}    ${p0User?.profileImageUrl}")
//                if (p0User?.uid .equals(sendUserUid)){
//                    iii = p0User
//                    Log.d(TAG, " equals${iii?.uid}    ${iii?.profileImageUrl}.")
//                }
//
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//        })
//        Log.d(TAG, "final  toChatUser ${iii?.uid}")
//        return iii
//    }

}



