package com.yue.mymovie.Chat.VoteModel

import android.util.Log
import com.google.firebase.database.*
import com.yue.mymovie.Chat.ChatModel.ChatLogVote
import com.yue.mymovie.Chat.ShowVoteMoveListFragment
import com.yue.mymovie.Util
import java.sql.Timestamp

data class ChatVote(val voteId: String, val sendUserId: String, val startVoteTimeStamp: Long, val endVoteTimeStamp: Long) {
    constructor() : this("", "", -1,-1)
    //movieList(MovieId, VoteGread)
}

class WaitVoteUser (
    var UserId: String
){
    constructor():this("")
}

class VoteMovieGrade (
    var MovieId: String,
    var grade: Int
){
    constructor():this("",0)
}

class VoteTimestamp (
    var timestamp: Long
){
    constructor():this(-1)
}

//data class ChatVote(val voteId: String, val sendUserId: String, var waitVoteUserId: ArrayList<String>, var movieVoteUserId: ArrayList<MovieVote>, val startVoteTimeStamp: Long, val endVoteTimestamp: Long) {
//    constructor() : this("", "", arrayListOf<String>(), arrayListOf<MovieVote>(),-1,-1)
//    //movieList(MovieId, VoteGread)
//}

class Firebase{
    companion object{
        fun addMovieVoteList(voteId: String, movieVoteGradeList: ArrayList<VoteMovieGrade>){
            for (i in 0 until movieVoteGradeList.size) {
                val voteMovieGradeRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId}/movieVoteGrade").push()
                voteMovieGradeRef.setValue(movieVoteGradeList[i]).addOnSuccessListener {
                    Log.d(ChatLogVote.TAG, "Saved our MovieId list in the vote table(group): ${voteMovieGradeRef.key}")
                }
            }

        }

        //                 for (i in  0 until waitVoteUserList.size){
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



        fun addWaitVoteUserList(voteId: String, waitVoteUserList: ArrayList<WaitVoteUser>){
            for (i in  0 until waitVoteUserList.size){
                val voteWaitingListRef = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId}/waiteVoteUserId").push()
                voteWaitingListRef.setValue(waitVoteUserList[i]).addOnSuccessListener {
                    Log.d(ChatLogVote.TAG, "Saved our WaitVoteUserID list in the vote table(group): ${voteWaitingListRef.key}")
                }
            }
        }

        fun getMovieGrade(voteMovieGradeList: ArrayList<VoteMovieGrade>, voteId: String,getList: (ArrayList<VoteMovieGrade>) -> Unit){
            var ref = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId}/movieVoteGrade")
            ref.addChildEventListener(object: ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    val voteMovieGrade = dataSnapshot.getValue(VoteMovieGrade::class.java)
                    if(voteMovieGrade != null) {
                        voteMovieGradeList.add(voteMovieGrade)
                        getList(voteMovieGradeList!!)
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                }
            })
        }

        fun getvoteDate(voteId: String,voteDateType: String, getVoteDate: (Long) -> Unit){
            var ref = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId}/${voteDateType}")
            ref.addValueEventListener(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val voteTimestamp = dataSnapshot.getValue(Long::class.java)
                    if(voteTimestamp != null) {
                        getVoteDate(voteTimestamp)
                        Log.d(ShowVoteMoveListFragment.TAG,"${voteTimestamp}")
                    }
                }

            })
//            ref.addChildEventListener(object: ChildEventListener {
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//
//                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//                }
//
//                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//                }
//
//                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
//
//                }
//
//                override fun onChildRemoved(p0: DataSnapshot) {
//                }
//            })
        }

        fun getRestUserList(waitUserIdList: ArrayList<WaitVoteUser>, voteId: String, getList: (ArrayList<WaitVoteUser>) -> Unit){
            var ref = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId}/waiteVoteUserId")
            ref.addChildEventListener(object: ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    val waitUser = dataSnapshot.getValue(WaitVoteUser::class.java)
                    if(waitUser != null) {
                        waitUserIdList.add(waitUser)
                        getList(waitUserIdList)

                    }

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }
            })
        }
    }
}