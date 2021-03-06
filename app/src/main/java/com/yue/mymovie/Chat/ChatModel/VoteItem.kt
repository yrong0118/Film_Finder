package com.yue.mymovie.Chat.ChatModel

import android.util.Log
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.Chat.ShowVoteMoveListFragment
import com.yue.mymovie.Movie
import com.yue.mymovie.MovieDetailsFragment
import com.yue.mymovie.R
import kotlinx.android.synthetic.main.selected_movie_new_vote_row.view.*
import kotlinx.android.synthetic.main.selected_movie_new_vote_row.view.imageview_new_vote
import kotlinx.android.synthetic.main.selected_movie_new_vote_row.view.movie_name_textview_new_vote
import kotlinx.android.synthetic.main.selected_movie_new_vote_row_selected.view.*
import kotlinx.android.synthetic.main.vote_movie_list_row.view.*

class VoteItemSelected(val movieByKWVote: MovieByKWVote): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.selected_movie_new_vote_row_selected
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.movie_name_textview_new_vote.text = movieByKWVote.movieName
        val targetImageView = viewHolder.itemView.imageview_new_vote
        if (movieByKWVote.movieImageUrl != ""){
            Picasso.get().load(movieByKWVote.movieImageUrl).into(targetImageView)
        }
        if (movieByKWVote.selected){
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


class VoteMovieItem (val movie: MovieByKW, val fullGrade:Int): Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.vote_movie_list_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.movie_name_textview_vote_movie_list_row.text = movie.movieName
        viewHolder.itemView.movie_full_grade_vote_movie_list_row.text = " Out of ${fullGrade}"
        viewHolder.itemView.movie_grade_vote_movie_list_row.text = movie.movieGrade.toString()
        val targetImageView = viewHolder.itemView.imageview_vote_movie_list_row

        if (movie.movieImageUrl != ""){
            Picasso.get().load(movie.movieImageUrl).into(targetImageView)
        } else {
            Picasso.get().load(R.drawable.no_images_available).into(targetImageView)
        }
        viewHolder.itemView.movie_detail_movie_list_row.setOnClickListener {
            Log.d(ShowVoteMoveListFragment.TAG,"Detail.click id: ${movie.movieId},name:${movie.movieName}")
            ShowVoteMoveListFragment.mCallbacktoDetail.movieVoteShowToDetail(ShowVoteMoveListFragment.selectedList,ShowVoteMoveListFragment.chatLog!!,movie)
        }

    }

}
