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
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatModel.ListLatestMessage
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.Util
import com.yue.mymovie.Util.Companion.fetchCurrentUser2


/**
 * A simple [Fragment] subclass.
 */
class ChatListFragment : Fragment() {
    lateinit var addCircle:ImageView

    lateinit var mCallback: AddGroupChatListener
    lateinit var mCallbackChatLogSelect: OnChatLogSelectListener

    interface AddGroupChatListener {
        fun addGroupChat()
    }

    interface OnChatLogSelectListener {
        fun chatLogSelect(selectedUserList:ArrayList<User>, chatLog: Util.ChatLog)
    }

    companion object {
        lateinit var recyclerview_latest_messages : RecyclerView
//        var currentUser: User? = null
        var currentUserUid :String?=""
        val TAG = "ChatList Frafment"
        fun newInstance(): ChatListFragment {
            var args = Bundle()
            var fragment = ChatListFragment()
            fragment.setArguments(args)
            return fragment
        }
        var adapter = GroupAdapter<GroupieViewHolder>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        recyclerview_latest_messages = view.findViewById(R.id.recyclerview_latest_messages)
        currentUserUid =Util.getCurrentUserUid()
        recyclerview_latest_messages.adapter = adapter


//        currentUser = fetchCurrentUser()
        verifyUserIsLoggedIn()

        fetchCurrentUser2 { currentUser ->

            listenForLatestMessages()
            addCircle = view.findViewById(R.id.ic_add_circle_chat)
            addCircle.setOnClickListener(View.OnClickListener { v -> showMenu(v) })

            adapter.setOnItemClickListener { item, view ->
                item as LastMessageItem
                mCallbackChatLogSelect.chatLogSelect(item.selectedUserList,item.chatLog)
            }

            adapter.clear()
        }


        return view

    }

    private fun listenForLatestMessages() {
        val ref = FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/$currentUserUid")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val listLateMessage = p0.getValue(ListLatestMessage::class.java) ?: return
                adapter.add(LastMessageItem(listLateMessage.text,listLateMessage.chatLog,listLateMessage.selectedUserList,listLateMessage.imageUri,listLateMessage.timestamp,listLateMessage.readOrNot))
                adapter.notifyDataSetChanged()

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val listLateMessage = p0.getValue(ListLatestMessage::class.java) ?: return
                adapter.add(LastMessageItem(listLateMessage.text,listLateMessage.chatLog,listLateMessage.selectedUserList,listLateMessage.imageUri,listLateMessage.timestamp,listLateMessage.readOrNot))
                adapter.notifyDataSetChanged()
//                recyclerview_latest_messages.adapter = adapter
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

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
//        val uid = FirebaseAuth.getInstance().uid
        if (currentUserUid == null) {
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
            mCallbackChatLogSelect = context as OnChatLogSelectListener
        } catch (e: ClassCastException) {
            //
        }
    }
}

