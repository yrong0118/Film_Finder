package com.yue.mymovie.Chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.yue.mymovie.Movie
import com.yue.mymovie.R

class ChatActivity: AppCompatActivity(), ChatListFragment.AddGroupChatListener {
//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        var chatListFragment = ChatListFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.chat_container, chatListFragment).commit()

    }

    override fun addGroupChat() {
        var newMessageFragment = NewMessageFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,newMessageFragment)
            .addToBackStack(newMessageFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}
