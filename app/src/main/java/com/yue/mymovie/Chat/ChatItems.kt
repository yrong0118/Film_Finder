package com.yue.mymovie.Chat

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.R
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*
import java.sql.Timestamp

class ChatFromItem(val text:String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text =text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
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
        }

    }


    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}

class LastMessageItem(val text : String, val  username : String, val uri: String, val timestamp: Long, val readOrNot: Boolean ):Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textview_latest_message.text = text
        val targetImageView = viewHolder.itemView.imageview_latest_message
        if (uri != ""){
            Picasso.get().load(uri).into(targetImageView)
        }
        viewHolder.itemView.username_textview_latest_message.text = username
    }

}