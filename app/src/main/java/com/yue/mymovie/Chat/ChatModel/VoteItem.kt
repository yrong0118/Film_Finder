package com.yue.mymovie.Chat.ChatModel

import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.Movie
import com.yue.mymovie.R
import kotlinx.android.synthetic.main.selected_movie_new_vote_row.view.*
import kotlinx.android.synthetic.main.selected_movie_new_vote_row.view.imageview_new_vote
import kotlinx.android.synthetic.main.selected_movie_new_vote_row.view.movie_name_textview_new_vote
import kotlinx.android.synthetic.main.selected_movie_new_vote_row_selected.view.*

class VoteItemSelected(val movie: MovieByKWVote): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.selected_movie_new_vote_row_selected
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.movie_name_textview_new_vote.text = movie.movieName
        val targetImageView = viewHolder.itemView.imageview_new_vote
        if (movie.movieImageUrl != ""){
            Picasso.get().load(movie.movieImageUrl).into(targetImageView)
        }
        if (movie.selected){
            viewHolder.itemView.ic_radio_button_choose_movie_new_vote.setImageResource(R.drawable.ic_radio_button_checked)
        }else {
            viewHolder.itemView.ic_radio_button_choose_movie_new_vote.setImageResource(R.drawable.ic_radio_button_unchecked)
        }
    }

}

class VoteItem(val movie: MovieByKW): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.selected_movie_new_vote_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.movie_name_textview_new_vote.text = movie.movieName
        val targetImageView = viewHolder.itemView.imageview_new_vote
        if (movie.movieImageUrl != ""){
            Picasso.get().load(movie.movieImageUrl).into(targetImageView)
        }

    }

}

