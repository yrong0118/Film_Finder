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


/*
import java.io.IOException

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.DialogInterface.OnClickListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button

class MyActivity : Activity() {

    */
/** Called when the activity is first created.  *//*


    internal var ctx: Context? = null
    internal var b: Button? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        b = findViewById(R.id.test1) as Button
        ctx = this
        b!!.setOnClickListener {
            // TODO Auto-generated method stub

            displayAlert()
        }


    }

    fun displayAlert() {

        val btnOnClick = OnClickListener { dialog, which ->
            if (which == DialogInterface.BUTTON_POSITIVE) {


                AlertDialog.Builder(ctx).setMessage("BUTTON_POSITIVE")
                    .setTitle("Alert Postive ")
                    .setCancelable(true)
                    .setNeutralButton(
                        android.R.string.ok
                    ) { dialog, whichButton -> finish() }
                    .show()


            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
                AlertDialog.Builder(ctx).setMessage("BUTTON_NEGATIVE")
                    .setTitle("Alert Negative")
                    .setCancelable(true)
                    .setNeutralButton(
                        android.R.string.ok
                    ) { dialog, whichButton -> finish() }
                    .show()

            } else if (which == DialogInterface.BUTTON_NEUTRAL) {
                // More Info
                AlertDialog.Builder(ctx).setMessage("BUTTON_NEUTRAL")
                    .setTitle("Alert Neutral")
                    .setCancelable(true)
                    .setNeutralButton(
                        android.R.string.ok
                    ) { dialog, whichButton -> finish() }
                    .show()
            }
        }


        val promptInstall = AlertDialog.Builder(ctx).setTitle("First Alert")
            .setMessage(
                "mY First Alert"
            )
            .setPositiveButton("OK", btnOnClick).setNegativeButton(
                "Cancel", btnOnClick
            ).setNeutralButton(
                "More Info",
                btnOnClick
            ).create()

        promptInstall.show()
    }
}*/

// https://api.yelp.com/v3/businesses/search?latitude=38.90218613387796&longitude=-77.0493872836232&radius=1500&categories=movietheaters&location=2224%20F%20St%20NW