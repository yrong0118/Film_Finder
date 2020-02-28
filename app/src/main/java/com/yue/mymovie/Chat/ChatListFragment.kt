package com.yue.mymovie.Chat
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.yue.mymovie.LoginOrRegister.LoginOrRegisterActivity
import com.yue.mymovie.MainActivity

import com.yue.mymovie.R
import android.view.LayoutInflater
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yue.mymovie.LoginOrRegister.User


/**
 * A simple [Fragment] subclass.
 */
class ChatListFragment : Fragment() {
    lateinit var addCircle:ImageView

    lateinit var mCallback: AddGroupChatListener

    interface AddGroupChatListener {
        fun addGroupChat()
    }


    companion object {

        var currentUser: User? = null
        val TAG = "ChatList Frafment"
        fun newInstance(): ChatListFragment {
            var args = Bundle()
            var fragment = ChatListFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        fetchCurrentUser()
        verifyUserIsLoggedIn()

        addCircle = view.findViewById(R.id.ic_add_circle_chat)

        addCircle.setOnClickListener(View.OnClickListener { v -> showMenu(v) })
        return view
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    @SuppressLint("RestrictedApi")
    private fun showMenu(view: View) {
        val menu: PopupMenu = PopupMenu(this.context!!, view)
        menu.setOnMenuItemClickListener(this::onMenuItemClick)
        val inflater = menu.getMenuInflater()
        inflater.inflate(R.menu.nav_menu, menu.menu)
        val menuHelper = MenuPopupHelper(this.context!!, menu.getMenu() as MenuBuilder, view)
        menuHelper.setForceShowIcon(true)
        menuHelper.show()
    }

    fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
//                val intent = Intent(this.activity, MainActivity::class.java)
//                startActivity(intent)
                Log.d(TAG, "Go to NewMessage Fragment")
                mCallback.addGroupChat()
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this.activity, LoginOrRegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            else -> {
            }
        }
        return true
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this.activity, LoginOrRegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater:MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallback = context as AddGroupChatListener
        } catch (e: ClassCastException) {
            //
        }
    }
}

