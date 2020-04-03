package com.yue.mymovie.Chat

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.icu.text.Edits
import android.media.MediaDrm
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.Chat.ChatModel.*
import com.yue.mymovie.Chat.VoteModel.WaitVoteUser
import com.yue.mymovie.LoginOrRegister.LoginFragment
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.Movie

import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.Util.Companion.fetchCurrentUser2
import com.yue.mymovie.Util.Companion.getLogChatHead
import com.yue.mymovie.Util.Companion.getRef
import com.yue.mymovie.Util.Companion.getTimestamp
import com.yue.mymovie.retrofit.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_chat_log.*
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ChatLogFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var logChatHeaderTextView: TextView
    lateinit var goBackToChatList: ImageView
    lateinit var sendMessage: Button
    lateinit var mCallbackToChat: OnChatLogGoBackListener
    lateinit var moreChatLog: ImageView
    lateinit var mCallbackNewVote : OnChatLogNewVote
    lateinit var currentUser: User
    var groupCreaterUID : String = "${Util.TWOPERSONCHATS}"
    var chatAdapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        var selectedList = arrayListOf<User>()
        var chatLogId: String? = ""
        var chatLogHeader: String? = ""
        val TAG = "ChatLog Frafment"
        var chatLog: Util.ChatLog? = null
        lateinit var mCallbackToVoteShow: ChatlogToVoteShowListener

        fun newInstance(list: ArrayList<User>, chatlog: Util.ChatLog): ChatLogFragment {
            chatLog =chatlog
            chatLogId = chatlog.chatLogId
            chatLogHeader = chatlog.chatLogHeader
            selectedList = list
            var args = Bundle()
            var fragment = ChatLogFragment()
            fragment.setArguments(args)
            return fragment
        }

//        var adapterMovieSearchByKW = GroupAdapter<GroupieViewHolder>()
    }

    interface OnChatLogGoBackListener {
        fun chatLogGoback()
    }

    interface OnChatLogNewVote {
        fun chatLogNewVote(selectedList: ArrayList<User>,chatLog: Util.ChatLog)
    }

    override fun onResume() {
        super.onResume()
        chatAdapter.clear()
        recyclerView.adapter = chatAdapter
    }

    override fun onStart() {
        super.onStart()
        chatAdapter.clear()
        recyclerView.adapter = chatAdapter
    }

    override fun onStop() {
        super.onStop()
        chatAdapter.clear()
        recyclerView.adapter = chatAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chat_log, container, false)
        recyclerView = view.findViewById(R.id.recyclerview_chat_log)
        logChatHeaderTextView = view.findViewById(R.id.group_member_text_Logchat)
        goBackToChatList = view.findViewById(R.id.ic_go_back_Logchat)
        sendMessage = view.findViewById(R.id.send_button_chat_log)





        goBackToChatList.setOnClickListener {
            chatAdapter.clear()
            mCallbackToChat.chatLogGoback()
        }
//        setDummyData()

        if (selectedList.size == 1) {
            //single chat, no groupCreater
            fetchCurrentUser2 { _currentUser->
                currentUser = _currentUser
                logChatHeaderTextView.text = chatLogHeader
                listenForMessages(chatLogId!!, currentUser)

                sendMessage.setOnClickListener {

                    if (selectedList.size == 1) {
                        Log.d(TAG, "Attempt to send message.... to the single")
                        performSendMessageToSingle(selectedList,currentUser)

                    } else {
                        Log.d(TAG, "Attempt to send message.... to the group")
                        performSendMessageToGroup(selectedList,currentUser)
                    }

                }

                moreChatLog = view.findViewById(R.id.ic_group_infor_Logchat)

                moreChatLog.setOnClickListener(View.OnClickListener { v -> showMenu(v) })

            }
        } else {
            //group chat

            fetchCurrentUser2 { _currentUser -> getGroupCreater { _groupCreaterUID->

                currentUser = _currentUser
                logChatHeaderTextView.text = chatLogHeader
                listenForMessages(chatLogId!!, currentUser)

                sendMessage.setOnClickListener {

                    if (selectedList.size == 1) {
                        Log.d(TAG, "Attempt to send message.... to the single")
                        performSendMessageToSingle(selectedList,currentUser)

                    } else {
                        Log.d(TAG, "Attempt to send message.... to the group")
                        performSendMessageToGroup(selectedList,currentUser)
                    }

                }

                groupCreaterUID = _groupCreaterUID

                moreChatLog = view.findViewById(R.id.ic_group_infor_Logchat)

                moreChatLog.setOnClickListener(View.OnClickListener { v -> showMenu(v) })

            }
        }


        }
        return view

    }

    private fun listenForMessages(chatLogId: String, currentUser: User) {
        val messageTypeList = arrayListOf<MessageType>()
        val chatLogItemClassList =arrayListOf<ChatLogItemClass>()
        var ref: DatabaseReference
        if (selectedList.size == 1) {
            ref = FirebaseDatabase.getInstance()
                .getReference("/${Util.TWOPERSONCHATS}/${chatLogId}/messages")

        } else {
            ref = FirebaseDatabase.getInstance()
                .getReference("/${Util.GROUPCHATS}/${chatLogId}/messages")
        }


        val lengthComparator = Comparator { c1: ChatLogItemClass, c2: ChatLogItemClass -> (c1.timestamp.toInt() - c2.timestamp.toInt())}

        getRef(TAG,ref,messageTypeList){
            val messageOrVote = messageTypeList[it.size - 1]
            Util.getChatLogItemClass(messageOrVote,chatLogItemClassList,currentUser, selectedList,
                chatLog!!){
//                Log.d(TAG,"${it.text}")
                if (it.size == messageTypeList.size) {
                    chatLogItemClassList.sortWith(lengthComparator)
                    for(i in 0 until it.size) {
                        var cur = it[i]
                        Log.d(TAG,"type: ${cur.type}, text: ${cur.text}, time: ${cur.timestamp}")
                        chatAdapter.add(
                            ChatLogItem(
                                cur.type,
                                cur.text,
                                cur.user,
                                cur.selectedList,
                                cur.chatLog,
                                cur.voteId,
                                cur.timestamp)
                        )
                        chatAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }


    private fun performSendMessageToGroup(selectedList: ArrayList<User>,currentUser:User) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (currentUser.uid == "") return
        val timestamp =  getTimestamp()
        val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()
        val chatMessage = ChatMessage(messageRef.key!!, currentUser.uid, text, timestamp)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message in the message table(group): ${messageRef.key}")
            }
        val groupRef = FirebaseDatabase.getInstance()
            .getReference("/${Util.GROUPCHATS}/${chatLogId}/messages").push()
        val messageType = MessageType(Util.MESSAGE, messageRef.key!!,timestamp)
        groupRef.setValue(messageType)
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "Saved messageType in the group table: ${messageRef.key}, $chatLogId"
                )
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(chatAdapter.itemCount - 1)
            }

        addListOnGroupMember(chatLogId!!, text, selectedList, timestamp,currentUser)
    }


    private fun performSendMessageToSingle(selectedList: ArrayList<User>,currentUser: User) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (currentUser.uid == "") return
        val timestamp = System.currentTimeMillis() / 1000
        val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()

        val chatMessage = ChatMessage(messageRef.key!!, currentUser.uid, text, timestamp)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message in the message table(single): ${messageRef.key}")
            }
        val twoPersonRef = FirebaseDatabase.getInstance()
            .getReference("/${Util.TWOPERSONCHATS}/${chatLogId}/messages").push()

        val messageType = MessageType(Util.MESSAGE, messageRef.key!!,timestamp)
        twoPersonRef.setValue(messageType)
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "Saved messageType in the twoPersonChat table: ${messageRef.key}, $chatLogId"
                )
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(chatAdapter.itemCount - 1)
            }
        addListOnSingleMember(chatLogId!!, text, selectedList, timestamp,currentUser)

    }

    private fun addListOnSingleMember(
        chatLogId: String,
        text: String,
        selectedList: ArrayList<User>,
        timestamp: Long,
        currentUser: User
    ) {
        var iterator = selectedList.iterator()
        var memberRef: DatabaseReference? = null

        updateSenderMessageList(
            chatLogId!!,
            selectedList.get(0)!!.profileImageUrl,
            text,
            timestamp,
            currentUser
        )

        iterator.forEach {
            if (it.uid != currentUser.uid) {
                memberRef = FirebaseDatabase.getInstance()
                    .getReference("/${Util.LISTS}/${it.uid}/${chatLogId}")

                Util.fetchUser(it.uid) { singleUser ->

                    val latestMessage = ListLatestMessage(
                        Util.ChatLog(chatLogId,currentUser.username),
                        selectedList,
                        currentUser.profileImageUrl,
                        text,
                        false,
                        timestamp
                    )
                    memberRef!!.setValue(latestMessage)
                        .addOnSuccessListener {
                            Log.d(
                                TAG,
                                "add the TwoPersonChatId to the list of member: ${memberRef!!.key}, ${chatLogId}"
                            )
                        }
                }

            }

        }
    }

    private fun addListOnGroupMember(
        groupID: String,
        text: String,
        selectedList: ArrayList<User>,
        timestamp: Long,
        currentUser: User
    ) {

        var iterator = selectedList.iterator()
        var memberRef: DatabaseReference? = null
        updateSenderMessageList(groupID, "", text, timestamp,currentUser)

        iterator.forEach {
            memberRef =
                FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/${it.uid}/${groupID}")
            val latestMessage = ListLatestMessage(
                Util.ChatLog(groupID!!,chatLogHeader!!),
                selectedList,
                "",
                text,
                false,
                timestamp
            )
            memberRef!!.setValue(latestMessage)
                .addOnSuccessListener {
                    Log.d(
                        TAG,
                        "add the groupid to the list of member: ${memberRef!!.key}, ${groupID}"
                    )
                }
        }
    }

    private fun updateSenderMessageList(
        chatLogId: String,
        imageUri: String,
        text: String,
        timestamp: Long,
        currentUser: User
    ) {

        val memberRef = FirebaseDatabase.getInstance()
            .getReference("/${Util.LISTS}/${currentUser.uid}/${chatLogId}")

        var latestMessage = ListLatestMessage(Util.ChatLog(chatLogId, chatLogHeader!!),
            selectedList, imageUri, text, true, timestamp)


        memberRef!!.setValue(latestMessage)
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "add the groupOrSingleId to the list of member: ${memberRef!!.key}, ${chatLogId}"
                )
            }

    }

    @SuppressLint("RestrictedApi")
    private fun showMenu(view: View) {
        val menu: PopupMenu = PopupMenu(this.context!!, view)
        menu.setOnMenuItemClickListener(this::onMenuItemClickChatLog)
        val inflater = menu.getMenuInflater()
        inflater.inflate(R.menu.nav_menu_chatlog_group_manager, menu.menu)
        val menuHelper = MenuPopupHelper(this.context!!, menu.getMenu() as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }


    fun onMenuItemClickChatLog(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_vote -> {
//                val intent = Intent(this.activity, MainActivity::class.java)
//                startActivity(intent)
                if (groupCreaterUID.equals(Util.TWOPERSONCHATS)){
                    Toast.makeText(this.context,"You Cannot Greate Vote in This Chat!",Toast.LENGTH_SHORT).show()
                } else if (currentUser.uid != groupCreaterUID){
                    Toast.makeText(this.context,"Merely GroupGreater Can Create Vote!",Toast.LENGTH_SHORT).show()
                } else {
                    Log.d(TAG, "crete a new vote ")
                    chatAdapter.clear()
                    mCallbackNewVote.chatLogNewVote(selectedList, chatLog!!)
                }


            }
            R.id.menu_vote_history -> {
                Log.d(TAG, "go to the vote history")
            }
            else -> {
            }
        }
        return true
    }



    private fun getGroupCreater(getList:(String) -> Unit) {
        val groupCreaterRef: DatabaseReference = FirebaseDatabase.getInstance()
                .getReference("/${Util.GROUPCHATS}/${chatLogId}/${Util.GROUPCREATER}")
        groupCreaterRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val groupCreater = dataSnapshot.getValue(String::class.java)
                Log.d(TAG,"groupCreater UID : ${groupCreater}geId}")
                getList(groupCreater!!)
            }

            override fun onCancelled(p0: DatabaseError) {}
        })


    }





    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallbackToChat = context as OnChatLogGoBackListener
            mCallbackNewVote = context as OnChatLogNewVote
            mCallbackToVoteShow= context as ChatlogToVoteShowListener
        } catch (e: ClassCastException) {
        }
    }
}







