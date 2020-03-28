package com.yue.mymovie.Chat

import android.util.Log
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.Chat.ChatLogFragment.Companion.chatAdapter
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.R
import com.yue.mymovie.Util
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import kotlinx.android.synthetic.main.vote_from_row.view.*
import kotlinx.android.synthetic.main.vote_to_row.view.*
import java.sql.Timestamp

interface ChatlogToVoteShowListener {
    fun catLogToVoteShow(list: ArrayList<User>, chatLog: Util.ChatLog,voteID: String)
}


class ChatFromItem(val text:String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text =text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
        } else{
            Picasso.get().load(R.drawable.unnamed).into(targetImageView)
        }

    }


    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text:String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text =text
        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
        }else{
            Picasso.get().load(R.drawable.unnamed).into(targetImageView)
        }


    }


    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}


class VoteFromItem(val selectedList: ArrayList<User>, val chatLog: Util.ChatLog, val user: User, val voteId:String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_from_row_vote
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
        }
        val voteView:TextView = viewHolder.itemView.text_from_row_vote

        voteView.setOnClickListener {
            Log.d("VoteItem","click VoteFromItem")
            ChatLogFragment.mCallbackToVoteShow.catLogToVoteShow(selectedList,chatLog,voteId)
            chatAdapter.clear()
        }

    }


    override fun getLayout(): Int {
        return R.layout.vote_from_row
    }
}


class VoteToItem(val selectedList: ArrayList<User>, val chatLog: Util.ChatLog, val user: User, val voteId:String): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_to_row_vote
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
        }

        val voteView = viewHolder.itemView.text_to_row_vote
        voteView.setOnClickListener {
            Log.d("VoteItem","click VoteToItem")
            ChatLogFragment.mCallbackToVoteShow.catLogToVoteShow(selectedList,chatLog,voteId)
            chatAdapter.clear()
        }

    }


    override fun getLayout(): Int {
        return R.layout.vote_to_row
    }
}


class LastMessageItem(val text : String, val  chatLog : Util.ChatLog, val selectedUserList : ArrayList<User>, val uri: String, val timestamp: Long, val readOrNot: Boolean ):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = text
        val targetImageView = viewHolder.itemView.imageview_latest_message
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
        }
        viewHolder.itemView.username_textview_latest_message.text = chatLog.chatLogHeader
    }

}