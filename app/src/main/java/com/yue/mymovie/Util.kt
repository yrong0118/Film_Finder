package com.yue.mymovie

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yue.mymovie.Chat.ChatListFragment
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.Chat.ShowVoteMoveListFragment
import com.yue.mymovie.Chat.VoteModel.VoteMovieGrade
import com.yue.mymovie.LoginOrRegister.User
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Util {

    companion object{
//        var currentUser: User? = null
        val MESSAGES = "Messages"
        val VOTES = "votes"
        val MESSAGE = "Message"
        val VOTE = "vote"
        val GROUPCHATS= "GroupChats"
        val TWOPERSONCHATS= "TwoPersonChats"
        val LISTS = "Lists"
        val USERS = "users"
        val MOVIELIST = "movieVoteLists"
        val timestamp = "timestamp"
        fun isStringEmpty(str:String):Boolean{
            return (str == null || str.length == 0)
        }

        fun getCurrentUserUid(): String {
            return FirebaseAuth.getInstance().uid!!
        }

//        fun fetchCurrentUser():User{
//            val uid = FirebaseAuth.getInstance().uid
//            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//
//            val latch: CountDownLatch = CountDownLatch(1)
//
//            if (currentUser != null) {
//                return currentUser!!
//            }
//
//            ref.addListenerForSingleValueEvent(object: ValueEventListener {
//
//                override fun onDataChange(p0: DataSnapshot) {
//                    currentUser = p0.getValue(User::class.java)!!
//                    Log.d("LatestMessages", "Current user ${ChatListFragment.currentUser?.profileImageUrl}")
//                    latch.countDown()
//                }
//
//                override fun onCancelled(p0: DatabaseError) {
//
//                }
//            })
//
//            latch.await()
//            return currentUser!!
//        }


        fun getTimestamp():Long{
            return System.currentTimeMillis() / 1000
        }
        fun gelog( getList: (ArrayList<VoteMovieGrade>) -> Unit){
            var ref = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${ShowVoteMoveListFragment.voteId}/movieVoteGrade")
            ref.addChildEventListener(object: ChildEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    val voteMovieGrade = dataSnapshot.getValue(VoteMovieGrade::class.java)
                    if(voteMovieGrade != null) {
                        ShowVoteMoveListFragment.voteMovieGradeList!!.add(voteMovieGrade)
                        getList(ShowVoteMoveListFragment.voteMovieGradeList!!)
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }

        fun fetchCurrentUser2(onUserFectch:(User)->Unit){

            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

//            val future: Future<User> = CompletableFuture()

            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    val curUser = p0.getValue(User::class.java)
                    Log.d("LatestMessages", "Current user ${curUser?.profileImageUrl}")
                    onUserFectch(curUser!!)

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })

        }


        fun fetchUser(userUid: String,onUserFectch: (User) -> Unit){
//            var user = null as User
//            val uid = FirebaseAuth.getInstance().uid
            val ref = FirebaseDatabase.getInstance().getReference("/users/$userUid")
            ref.addListenerForSingleValueEvent(object: ValueEventListener {

                override fun onDataChange(p0: DataSnapshot) {
                    var user = p0.getValue(User::class.java)
                    Log.d("LatestMessages", "Current user ${user?.profileImageUrl}")
                    if (user!= null){
                        onUserFectch(user)
                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        }


        fun getLogChatHead(selectedList: ArrayList<User>, currentUserName: String):String{
            if (selectedList.size == 1) {
                return selectedList.get(0).username
            }

            var res = ""
            var count  = 0
            selectedList.forEach{
                res += it.username + ", "
                count ++
            }
            res += currentUserName
            Log.d(ChatLogFragment.TAG,res)
            return res

        }

        @JvmStatic
        fun getdate(tag:String,day:Int): String {
            //2020-01-15
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            //Getting current date
            val cal = Calendar.getInstance()
            //Displaying current date in the desired format
//            System.out.println("Current Date: " + sdf.format(cal.getTime()))
            //Number of Days to add/substract
            cal.add(Calendar.DAY_OF_MONTH, day)
            //Date after adding/subtract the days to the current date
            val newDate = sdf.format(cal.getTime())
            //Displaying the new Date after addition of Days to current date
            Log.d(tag,"Date after Addition: $newDate")
            return newDate
        }



    }


    class ChatLog (
        var chatLogId : String,
        var chatLogHeader :String

    ){
        constructor():this("", "")
    }



}
