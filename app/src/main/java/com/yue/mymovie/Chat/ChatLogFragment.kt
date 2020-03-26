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
import com.yue.mymovie.LoginOrRegister.LoginFragment
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.Movie

import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.Util.Companion.getLogChatHead
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


    companion object {
        var selectedVoteMovieList: ArrayList<MovieByKW> = arrayListOf()
        var selectedList = arrayListOf<User>()
        var chatLogId: String? = ""
        var chatLogHeader: String? = ""
        val TAG = "ChatLog Frafment"

        fun newInstance(list: ArrayList<User>, chatLog: Util.ChatLog): ChatLogFragment {
            chatLogId = chatLog.chatLogId
            chatLogHeader = chatLog.chatLogHeader
            selectedList = list
            var args = Bundle()
            var fragment = ChatLogFragment()
            fragment.setArguments(args)
            return fragment
        }

        val currentUserUID = Util.getCurrentUserUid()
        var chatAdapter = GroupAdapter<GroupieViewHolder>()
        var adapternewVote = GroupAdapter<GroupieViewHolder>()
        var adapterMovieSearchByKW = GroupAdapter<GroupieViewHolder>()
    }

    interface OnChatLogGoBackListener {
        fun chatLogGoback()
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

        recyclerView.adapter = chatAdapter



        goBackToChatList.setOnClickListener {
            chatAdapter.clear()
            mCallbackToChat.chatLogGoback()
        }
        chatAdapter.clear()

//        setDummyData()
        Util.fetchCurrentUser2 { currentUser ->
            logChatHeaderTextView.text = chatLogHeader
            listenForMessages(chatLogId!!, currentUser)

            sendMessage.setOnClickListener {

                if (selectedList.size == 1) {
                    Log.d(TAG, "Attempt to send message.... to the single")
                    performSendMessageToSingle(selectedList,currentUser)

                } else {
                    Log.d(TAG, "Attempt to send message.... to the group")
                    performSendMessageToGroup(selectedList)
                }

            }

            moreChatLog = view.findViewById(R.id.ic_group_infor_Logchat)
            moreChatLog.setOnClickListener(View.OnClickListener { v -> showMenu(v) })


        }


        return view
    }

    private fun listenForMessages(chatLogId: String, currentUser: User) {
        var ref: DatabaseReference
        if (selectedList.size == 1) {
            ref = FirebaseDatabase.getInstance()
                .getReference("/${Util.TWOPERSONCHATS}/${chatLogId}/messages")

        } else {
            ref = FirebaseDatabase.getInstance()
                .getReference("/${Util.GROUPCHATS}/${chatLogId}/messages")
        }

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, p1: String?) {

                Log.d(TAG, "@@@@@@@")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val messageOrVote = dataSnapshot.getValue(MessageType::class.java)

                if (messageOrVote != null) {
                    if (messageOrVote.messageType == Util.MESSAGE) {
//                        Log.d(TAG, " messageOrVote!!.messageId: ${messageOrVote!!.messageId}")

                        val messRef =
                            FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}")
                        messRef.addChildEventListener(object : ChildEventListener {
                            override fun onChildRemoved(p0: DataSnapshot) {

                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                            }

                            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)
                                if (chatMessage != null && chatMessage.messageId.equals(messageOrVote.messageId)) {
                                    Log.d(TAG, "chatMessage!!.messageId(changed): ${chatMessage!!.messageId}")
                                    if (chatMessage.sendUserId.equals(currentUserUID)) {
                                        chatAdapter.add(
                                            ChatFromItem(
                                                chatMessage.text,
                                                currentUser
                                            )
                                        )
                                    } else {
                                        Util.fetchUser(chatMessage.sendUserId) { chatToUser ->
                                            chatAdapter.add(
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

                                    if (chatMessage.sendUserId == currentUserUID) {
                                        Log.d(TAG, "chatMessage!!.messageId(added): ${chatMessage!!.messageId}")
                                        chatAdapter.add(
                                            ChatFromItem(
                                                chatMessage.text,
                                                currentUser
                                            )
                                        )
//                                        Log.d(TAG, "chatMessage!!.messageId(added): ${chatMessage!!.messageId}")
                                    } else {
                                        Util.fetchUser(chatMessage.sendUserId) { chatToUser ->
                                            chatAdapter.add(
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
                    else if (messageOrVote.messageType == Util.VOTE) {
//                        Log.d(TAG, " messageOrVote!!.messageId: ${messageOrVote!!.messageId}")

                        val messRef =
                            FirebaseDatabase.getInstance().getReference("/${Util.VOTES}")
                        messRef.addChildEventListener(object : ChildEventListener {
                            override fun onChildRemoved(p0: DataSnapshot) {

                            }

                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                            }

                            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                                val voteMessage = dataSnapshot.getValue(VoteMessage::class.java)
                                if (voteMessage != null && voteMessage.voteId.equals(messageOrVote.messageId)) {
                                    Log.d(TAG, "voteMessage!!.messageId(changed): ${voteMessage!!.voteId}")
                                    if (voteMessage.sendUserId.equals(currentUserUID)) {
                                        chatAdapter.add(
                                            VoteFromItem(
                                                currentUser
                                            )
                                        )
                                    } else {
                                        Util.fetchUser(voteMessage.sendUserId) { chatToUser ->
                                            chatAdapter.add(
                                                VoteToItem(
                                                    chatToUser
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                                val voteMessage = dataSnapshot.getValue(VoteMessage::class.java)
                                if (voteMessage != null && voteMessage.voteId.equals(messageOrVote.messageId)) {
                                    Log.d(TAG, "voteMessage!!.messageId(changed): ${voteMessage!!.voteId}")
                                    if (voteMessage.sendUserId.equals(currentUserUID)) {
                                        chatAdapter.add(
                                            VoteFromItem(
                                                currentUser
                                            )
                                        )
                                    } else {
                                        Util.fetchUser(voteMessage.sendUserId) { chatToUser ->
                                            chatAdapter.add(
                                                VoteToItem(
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


    private fun performSendMessageToGroup(selectedList: ArrayList<User>) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (currentUserUID == "") return
        val timestamp = System.currentTimeMillis() / 1000
        val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()
        val chatMessage = ChatMessage(messageRef.key!!, currentUserUID, text, timestamp)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message in the message table(group): ${messageRef.key}")
            }
        val groupRef = FirebaseDatabase.getInstance()
            .getReference("/${Util.GROUPCHATS}/${chatLogId}/messages").push()
        val messageType = MessageType(Util.MESSAGE, messageRef.key!!)
        groupRef.setValue(messageType)
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "Saved messageType in the group table: ${messageRef.key}, $chatLogId"
                )
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(chatAdapter.itemCount - 1)
            }

        addListOnGroupMember(chatLogId!!, text, selectedList, timestamp)
    }


    private fun performSendMessageToSingle(selectedList: ArrayList<User>,currentUser: User) {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()
        if (currentUserUID == "") return
        val timestamp = System.currentTimeMillis() / 1000
        val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()

        val chatMessage = ChatMessage(messageRef.key!!, currentUserUID, text, timestamp)
        messageRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message in the message table(single): ${messageRef.key}")
            }
        val twoPersonRef = FirebaseDatabase.getInstance()
            .getReference("/${Util.TWOPERSONCHATS}/${chatLogId}/messages").push()

        val messageType = MessageType(Util.MESSAGE, messageRef.key!!)
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
            timestamp
        )

        iterator.forEach {
            if (it.uid != currentUserUID) {
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
        timestamp: Long
    ) {

        var iterator = selectedList.iterator()
        var memberRef: DatabaseReference? = null
        updateSenderMessageList(groupID, "", text, timestamp)

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
        timestamp: Long
    ) {

        val memberRef = FirebaseDatabase.getInstance()
            .getReference("/${Util.LISTS}/${currentUserUID}/${chatLogId}")

        chatLogId
        chatLogHeader!!
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
                Log.d(TAG, "crete a new vote ")
                selectedVoteMovieList.clear()
                val api = getString(R.string.glu_KEY)
                val page = getString(R.string.page1)
                val imgFrontPath = getString(R.string.img_front_path)
                VoteDialog.showVoteMovieDialog(this.context!!,selectedVoteMovieList,api, page, imgFrontPath)

            }
            R.id.menu_vote_history -> {
                Log.d(TAG, "go to the vote history")
            }
            else -> {
            }
        }
        return true
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallbackToChat = context as OnChatLogGoBackListener
        } catch (e: ClassCastException) {
        }
    }
//    fun getMovieImgData(movieId : String,recyclerMovieByKWView: RecyclerView){
//        val api = getString(R.string.glu_KEY)
//        val language = getString(R.string.language)
//        var movieImgVoteRequestApi = RetrofitClient.instance.create(MovieImgVoteRequestApi::class.java)
//        movieImgVoteRequestApi.getMovieImgById(movieId,api)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .filter { baseResponse -> baseResponse != null }
//            .subscribe(
//                { baseResponse -> showImgListByIdView(baseResponse,recyclerMovieByKWView) },
//                { throwable ->
//                    //*******************************
//                    if (context == null) {
//                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
//                    }
//                    //*******************************
//                    Toast.makeText(context, "Movies Image Search By Id API Failed", Toast.LENGTH_SHORT).show()
//                    throwable.printStackTrace()
//                }
//            )
//    }
//
//    fun showImgListByIdView(movieImgResponse: MovieImgResponse, recyclerMovieByKWView: RecyclerView){
//        var movieImgList = Vector<MovieImgById>()
//        if (movieImgResponse != null && movieImgResponse.results.size > 0) {
//            for (i in 0 until movieImgResponse.results.size){
//                Log.d(TAG,"result.size: $movieImgResponse.results.size")
//                var movieImageUrl = movieImgResponse.results.get(i).movieImageUrl
//                movieImgList.add(MovieImgById(movieImageUrl))
//            }
//            var movieByKW = MovieByKW("XXXXXX","XXXXXX",movieImgList.get(0).movieImageUrl)
//            adapterMovieSearchByKW.add(VoteItem(movieByKW))
//
//        }
//
//        recyclerMovieByKWView.adapter = adapterMovieSearchByKW
//
//    }


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





