package com.yue.mymovie.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.Movie
import com.yue.mymovie.R
import com.yue.mymovie.Util

class ChatActivity: AppCompatActivity(), ChatListFragment.AddGroupChatListener,NewMessageFragment.ConfirmToChatLognListener,
    ChatListFragment.OnChatLogSelectListener, ChatLogFragment.OnChatLogGoBackListener,
    ShowVoteMoveListFragment.OnMovieVoteShowGoBackListener ,ChatlogToVoteShowListener,
    VoteMovieActionFragment.OnMovieVoteActionGoBackListener,
    ShowVoteMoveListFragment.GotoVoteActionListener {


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

    override fun confirmToChatLogListener(selectedUserList:ArrayList<User>,chatLog:Util.ChatLog) {
        var chatLogFragment = ChatLogFragment.newInstance(selectedUserList,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun chatLogSelect(selectedUserList: ArrayList<User>, chatLog: Util.ChatLog) {
        var chatLogFragment = ChatLogFragment.newInstance(selectedUserList,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun chatLogGoback() {
        var chatListFragment = ChatListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatListFragment)
            .addToBackStack(chatListFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun movieVoteShowGoBack(list: ArrayList<User>, chatLog: Util.ChatLog) {

        var chatLogFragment = ChatLogFragment.newInstance(list,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun catLogToVoteShow(list: ArrayList<User>, chatLog: Util.ChatLog, voteID: String) {
        var  showVoteMoveListFragment = ShowVoteMoveListFragment.newInstance(list,chatLog,voteID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,showVoteMoveListFragment)
            .addToBackStack(showVoteMoveListFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun movieVoteActionGoBack(list: ArrayList<User>, chatLog: Util.ChatLog, voteID: String) {
        var  showVoteMoveListFragment = ShowVoteMoveListFragment.newInstance(list,chatLog,voteID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,showVoteMoveListFragment)
            .addToBackStack(showVoteMoveListFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }


    override fun gotoVoteAction(
        list: ArrayList<User>,
        chatLog: Util.ChatLog,
        voteID: String,
        waitUserList: ArrayList<String>,
        movieCandidateList: ArrayList<MovieByKW>
    ) {
        var  voteMovieActionFragment = VoteMovieActionFragment.newInstance(list,chatLog,voteID,waitUserList,movieCandidateList)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,voteMovieActionFragment)
            .addToBackStack(voteMovieActionFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }





}
