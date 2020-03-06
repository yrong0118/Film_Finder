package com.yue.mymovie

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yue.mymovie.Chat.ChatListFragment
import com.yue.mymovie.LoginOrRegister.User
import java.util.concurrent.CountDownLatch


//the first way to is to use lambda, I cannot user return on the fun fetchCurrentUser2

/*
fun fetchCurrentUser2(onUserFectch:(User)->Unit){

    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

//            val future: Future<User> = CompletableFuture()

    ref.addListenerForSingleValueEvent(object: ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            val curUser = p0.getValue(User::class.java)!!
            Log.d("LatestMessages", "Current user ${ChatListFragment.currentUser?.profileImageUrl}")
            onUserFectch(curUser)

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    })

}

//when I use this fun I can use:
currentUser is the var I want to get, cause onUserFetch is no return, so I dont need to write on the right of the ->
fetchCurrentUser2 { currentUser: User ->

    //when I want to use currentUser, I need to write the stuff inside that can make sure the current is not null
}

*/



//the second way is use the block thread:


/*
fun fetchCurrentUser():User{
    val uid = FirebaseAuth.getInstance().uid
    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

    //if I have more than one stuff to finish, I can sent this count number

    val latch: CountDownLatch = CountDownLatch(1)
//            val future: Future<User> = CompletableFuture()

    if (Util.currentUser != null) {
        return Util.currentUser!!
    }

    ref.addListenerForSingleValueEvent(object: ValueEventListener {

        override fun onDataChange(p0: DataSnapshot) {
            Util.currentUser = p0.getValue(User::class.java)!!
            Log.d("LatestMessages", "Current user ${ChatListFragment.currentUser?.profileImageUrl}")
            //减1
            latch.countDown()
        }

        override fun onCancelled(p0: DatabaseError) {

        }
    })
    //run till latch == 0
    latch.await()
    return Util.currentUser!!
}
*/
