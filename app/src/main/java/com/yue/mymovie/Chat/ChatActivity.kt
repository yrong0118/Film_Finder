package com.yue.mymovie.Chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.Chat.VoteModel.CreateNewVoteFragment
import com.yue.mymovie.Chat.VoteModel.ShowSearchMovieListFragment
import com.yue.mymovie.Chat.VoteModel.WaitVoteUser
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.R
import com.yue.mymovie.Util

class ChatActivity: AppCompatActivity(), ChatListFragment.AddGroupChatListener,NewMessageFragment.ConfirmToChatLognListener,
    ChatListFragment.OnChatLogSelectListener, ChatLogFragment.OnChatLogGoBackListener,
    ShowVoteMoveListFragment.OnMovieVoteShowGoBackListener ,ChatlogToVoteShowListener,
    VoteMovieActionFragment.OnMovieVoteActionGoBackListener,
    ShowVoteMoveListFragment.GotoVoteActionListener, CreateNewVoteFragment.OnNewVoteGoBackListener,
    CreateNewVoteFragment.OnNewVoteSearchMovieListener, ChatLogFragment.OnChatLogNewVote,
    ShowSearchMovieListFragment.OnShowSearchMovieGoBackListener,
    ShowSearchMovieListFragment.OnShowSearchMovieConfirmListener {

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
//            .addToBackStack(newMessageFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun confirmToChatLogListener(selectedUserList:ArrayList<User>,chatLog:Util.ChatLog) {
        var chatLogFragment = ChatLogFragment.newInstance(selectedUserList,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
//            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun chatLogSelect(selectedUserList: ArrayList<User>, chatLog: Util.ChatLog) {
        var chatLogFragment = ChatLogFragment.newInstance(selectedUserList,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
//            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun chatLogGoback() {
        var chatListFragment = ChatListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatListFragment)
//            .addToBackStack(chatListFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun movieVoteShowGoBack(list: ArrayList<User>, chatLog: Util.ChatLog) {

        var chatLogFragment = ChatLogFragment.newInstance(list,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
//            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun catLogToVoteShow(list: ArrayList<User>, chatLog: Util.ChatLog, voteID: String) {
        var  showVoteMoveListFragment = ShowVoteMoveListFragment.newInstance(list,chatLog,voteID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,showVoteMoveListFragment)
//            .addToBackStack(showVoteMoveListFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun movieVoteActionGoBack(list: ArrayList<User>, chatLog: Util.ChatLog, voteID: String) {
        var  showVoteMoveListFragment = ShowVoteMoveListFragment.newInstance(list,chatLog,voteID)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,showVoteMoveListFragment)
//            .addToBackStack(showVoteMoveListFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }


    override fun gotoVoteAction(
        list: ArrayList<User>,
        chatLog: Util.ChatLog,
        voteID: String,
        waitUserList: ArrayList<WaitVoteUser>,
        movieCandidateList: ArrayList<MovieByKW>
    ) {
        var  voteMovieActionFragment = VoteMovieActionFragment.newInstance(list,chatLog,voteID,waitUserList,movieCandidateList)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,voteMovieActionFragment)
//            .addToBackStack(voteMovieActionFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun newVoteGoBack(selectedUserList:ArrayList<User>,chatLog:Util.ChatLog) {
        var chatLogFragment = ChatLogFragment.newInstance(selectedUserList,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
//            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }


    override fun newVoteserchMovie(
        selectedList: ArrayList<User>,
        selectedMovieList: ArrayList<MovieByKW>,
        chatLog: Util.ChatLog,
        searchKeyWord: String
    ) {
        var showSearchMovieListFragmentFragment = ShowSearchMovieListFragment.newInstance(selectedList,selectedMovieList,chatLog,searchKeyWord)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,showSearchMovieListFragmentFragment)
//            .addToBackStack(showSearchMovieListFragmentFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun chatLogNewVote(selectedList: ArrayList<User>, chatLog: Util.ChatLog) {
        var createNewVoteFragment = CreateNewVoteFragment.newInstance(selectedList,chatLog!!)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,createNewVoteFragment)
//            .addToBackStack(showSearchMovieListFragmentFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun showSearchMovieGoBack(selectedList: ArrayList<User>, selectedMovieList: ArrayList<MovieByKW>,chatLog: Util.ChatLog) {
        var createNewVoteFragment = CreateNewVoteFragment.newInstance(selectedList,selectedMovieList,chatLog!!)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,createNewVoteFragment)
//            .addToBackStack(showSearchMovieListFragmentFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun showSearchMovieConfirm(selectedList: ArrayList<User>,chatLog: Util.ChatLog) {
        var chatLogFragment = ChatLogFragment.newInstance(selectedList,chatLog)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.chat_container,chatLogFragment)
//            .addToBackStack(chatLogFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }


}
