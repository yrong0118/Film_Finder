package com.yue.mymovie.Chat.ChatModel

import android.util.Log
import android.view.LayoutInflater
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.Chat.VoteDialog
import com.yue.mymovie.Chat.VoteModel.ChatVote
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.R
import com.yue.mymovie.Util
import kotlinx.android.synthetic.main.fragment_chat_log.view.*
import java.sql.Timestamp

class ChatLogVote {
    companion object{
        val text = "[link] VOTE"
        val currentUserUID = Util.getCurrentUserUid()
        val TAG = "ChatLogVote"
        var chatLogId = ChatLogFragment.chatLogId

        fun performSendVoteToGroup(selectedList: ArrayList<User>,currentUser: User,startVoteTimeStamp: Long,endVoteTimestamp: Long) {
            // how do we actually send a message to firebase...
            if (currentUserUID == "") return
            val voteMovieIdList = getMovieVoteList(ChatLogFragment.selectedVoteMovieList)
            var waitVoteUserList = getWaitVoteUserList(selectedList)
            val voteRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}").push()
            val chatVote = ChatVote(voteRef.key!!, currentUserUID,waitVoteUserList,startVoteTimeStamp,endVoteTimestamp)
            voteRef.setValue(chatVote)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message in the message table(group): ${voteRef.key}")

                    for (i in 0 until voteMovieIdList.size) {
                        val voteMovieRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteRef.key}")
                        voteMovieRef.setValue(0).addOnSuccessListener {
                            Log.d(TAG, "Saved our MovieId list in the vote table(group): ${voteMovieRef.key}")
                        }
                    }


                }
            val groupRef = FirebaseDatabase.getInstance()
                .getReference("/${Util.GROUPCHATS}/${chatLogId}/messages").push()
            val messageType = MessageType(Util.VOTE, voteRef.key!!)
            groupRef.setValue(messageType)
                .addOnSuccessListener {
                    Log.d(
                        TAG,
                        "Saved messageType in the group table: ${voteRef.key}, $chatLogId"
                    )
                    val inflater = LayoutInflater.from(VoteDialog.context)
                    val chatLayout = inflater.inflate(R.layout.fragment_chat_log, null)
                    chatLayout.recyclerview_chat_log.scrollToPosition(ChatLogFragment.chatAdapter.itemCount - 1)
                }

            addListOnGroupMember(chatLogId!!, text, selectedList, startVoteTimeStamp)
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
                    Util.ChatLog(groupID!!, ChatLogFragment.chatLogHeader!!),
                    selectedList,
                    "",
                    text,
                    false,
                    timestamp
                )
                memberRef!!.setValue(latestMessage)
                    .addOnSuccessListener {
                        Log.d(
                            ChatLogFragment.TAG,
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
                .getReference("/${Util.LISTS}/${ChatLogFragment.currentUserUID}/${chatLogId}")

            chatLogId
            ChatLogFragment.chatLogHeader!!
            var latestMessage = ListLatestMessage(Util.ChatLog(chatLogId, ChatLogFragment.chatLogHeader!!),
                ChatLogFragment.selectedList, imageUri, text, true, timestamp)


            memberRef!!.setValue(latestMessage)
                .addOnSuccessListener {
                    Log.d(
                        ChatLogFragment.TAG,
                        "add the groupOrSingleId to the list of member: ${memberRef!!.key}, ${chatLogId}"
                    )
                }

        }




//        fun performSendVoteToSingle(selectedList: ArrayList<User>,currentUser: User,startVoteTimeStamp: Long,endVoteTimestamp: Long) {
//            // how do we actually send a message to firebase...
//            if (currentUserUID == "") return
//            val timestamp = System.currentTimeMillis() / 1000
//            val messageRef = FirebaseDatabase.getInstance().getReference("/${Util.MESSAGES}").push()
//
//            val chatMessage = ChatMessage(messageRef.key!!, currentUserUID, text, timestamp)
//            messageRef.setValue(chatMessage)
//                .addOnSuccessListener {
//                    Log.d(TAG, "Saved our chat message in the message table(single): ${messageRef.key}")
//                }
//            val twoPersonRef = FirebaseDatabase.getInstance()
//                .getReference("/${Util.TWOPERSONCHATS}/${chatLogId}/messages").push()
//
//            val messageType = MessageType(Util.MESSAGE, messageRef.key!!)
//            twoPersonRef.setValue(messageType)
//                .addOnSuccessListener {
//                    Log.d(
//                        TAG,
//                        "Saved messageType in the twoPersonChat table: ${messageRef.key}, $chatLogId"
//                    )
//                    edittext_chat_log.text.clear()
//                    recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
//                }
//            addListOnSingleMember(chatLogId!!, text, selectedList, timestamp,currentUser)
//
//        }

        private fun getMovieVoteList(voteMovieList: ArrayList<MovieByKW>): ArrayList<String> {
            var list = arrayListOf<String>()
            val set = setOf<String>()
            for (i in 0 until voteMovieList.size){
                val current = voteMovieList.get(i).movieId
                if (!set.contains(current)) {
                    set.plus(voteMovieList.get(i).movieId)
                    list.plus(current)
                }
            }
            return list
        }

        private fun getMovieVoteMap(voteMovieList: ArrayList<MovieByKW>): Map<String,Int> {

            val map = mapOf<String,Int>()
            for (i in 0 until voteMovieList.size){
                val current = map.get(voteMovieList.get(i).movieId)
                if (current == null) {
                    map.plus(voteMovieList.get(i).movieId to 0)
                }
            }
            return map
        }

        private fun getWaitVoteUserSet(selectedList: ArrayList<User>): Set<String> {
            var set = setOf<String>()
            for (i in 0 until selectedList.size) {
                set.plus(selectedList.get(i).uid)
            }
            return set
        }

        private fun getWaitVoteUserList(selectedList: ArrayList<User>): ArrayList<String> {
            var list = arrayListOf<String>()
            for (i in 0 until selectedList.size) {
                list.plus(selectedList.get(i).uid)
            }
            return list
        }
    }
}