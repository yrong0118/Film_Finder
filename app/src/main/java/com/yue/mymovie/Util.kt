package com.yue.mymovie

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yue.mymovie.Chat.ChatListFragment
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.LoginOrRegister.User
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Future

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



    }


    class ChatLog (
        var chatLogId : String,
        var chatLogHeader :String

    ){
        constructor():this("", "")
    }



}
