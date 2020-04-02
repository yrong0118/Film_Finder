package com.yue.mymovie.Chat.ChatModel

import android.util.Log
import android.view.LayoutInflater
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.Chat.VoteModel.ChatVote
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.addMovieVoteList
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.addWaitVoteUserList
import com.yue.mymovie.Chat.VoteModel.VoteMovieGrade
import com.yue.mymovie.Chat.VoteModel.WaitVoteUser
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
        val chatLogId = ChatLogFragment.chatLogId
        val timestamp = Util.getTimestamp()
        fun performSendVoteToGroup(selectedList: ArrayList<User>,selectedMovieList: ArrayList<MovieByKW>, currentUser: User,startVoteTimeStamp: Long,endVoteTimestamp: Long) {
            // how do we actually send a message to firebase...
            if (currentUserUID == "") return
            val voteMovieIdList = getMovieVoteList(selectedMovieList)
            var waitVoteUserList = getWaitVoteUserList(selectedList)
            val voteRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}").push()
            val chatVote = ChatVote(voteRef.key!!, currentUserUID,startVoteTimeStamp,endVoteTimestamp)
            voteRef.setValue(chatVote)
                    //*******************************************
                .addOnSuccessListener {
                    Log.d(TAG, "waitList Size: ${waitVoteUserList.size}")
                    Log.d(TAG, "voteMovieIdList: ${voteMovieIdList.size}")
                    Log.d(TAG, "Saved our chat message in the message table(group): ${voteRef.key}")

                    addWaitVoteUserList(voteRef.key!!,waitVoteUserList)

                    addMovieVoteList(voteRef.key!!,voteMovieIdList)

//                    for (i in  0 until waitVoteUserList.size){
//                        val voteWaitingListRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteRef.key}/waiteVoteUserId").push()
//                        voteWaitingListRef.setValue(waitVoteUserList[i]).addOnSuccessListener {
//                            Log.d(TAG, "Saved our WaitVoteUserID list in the vote table(group): ${voteWaitingListRef.key}")
//                        }
//                    }
//
//
//                    for (i in 0 until voteMovieIdList.size) {
//                        val voteMovieGradeRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteRef.key}/movieVoteGrade").push()
//                        voteMovieGradeRef.setValue(voteMovieIdList[i]).addOnSuccessListener {
//                            Log.d(TAG, "Saved our MovieId list in the vote table(group): ${voteMovieGradeRef.key}")
//                        }
//                    }

                }
            val groupRef = FirebaseDatabase.getInstance()
                .getReference("/${Util.GROUPCHATS}/${chatLogId}/messages").push()
            val messageType = MessageType(Util.VOTE, voteRef.key!!,timestamp)
            groupRef.setValue(messageType)
                .addOnSuccessListener {
                    Log.d(
                        TAG,
                        "Saved messageType in the group table: ${voteRef.key}, $chatLogId"
                    )
//                    val inflater = LayoutInflater.from(context)
//                    val chatLayout = inflater.inflate(R.layout.fragment_chat_log, null)
//                    chatLayout.recyclerview_chat_log.scrollToPosition(ChatLogFragment.chatAdapter.itemCount - 1)
                }

            addListOnGroupMember(chatLogId!!, text, selectedList, startVoteTimeStamp,currentUser)
        }


        private fun addListOnGroupMember(
            groupID: String,
            text: String,
            selectedList: ArrayList<User>,
            timestamp: Long,
            currentUser:User
        ) {

            var iterator = selectedList.iterator()
            var memberRef: DatabaseReference? = null
            updateSenderMessageList(groupID, "", text, timestamp,currentUser)

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
            timestamp: Long,
            currentUser:User
        ) {

            val memberRef = FirebaseDatabase.getInstance()
                .getReference("/${Util.LISTS}/${currentUser.uid}/${chatLogId}")
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


        private fun getMovieVoteList(voteMovieList: ArrayList<MovieByKW>): ArrayList<VoteMovieGrade> {
            var list = arrayListOf<VoteMovieGrade>()
            val set = setOf<String>()
            for (i in 0 until voteMovieList.size){
                val current = voteMovieList.get(i).movieId
                if (!set.contains(current)) {
                    set.plus(voteMovieList.get(i).movieId)
                    list.add(VoteMovieGrade(current,0))
                }
            }
            return list
        }

        private fun getWaitVoteUserList(selectedList: ArrayList<User>): ArrayList<WaitVoteUser> {
            var list = arrayListOf<WaitVoteUser>()
            for (i in 0 until selectedList.size) {
                list.add(WaitVoteUser(selectedList[i].uid))
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


    }
}