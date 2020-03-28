package com.yue.mymovie.Chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.Chat.ChatModel.MovieByKWVote
import com.yue.mymovie.Chat.ChatModel.VoteItemSelected
import com.yue.mymovie.LoginOrRegister.User

import com.yue.mymovie.R
import com.yue.mymovie.Util

class VoteMovieActionFragment : Fragment() {

    companion object {
        val TAG = "VoteActionMoveList"
        var selectedUserList = arrayListOf<User>()
        var chatLogId: String? = ""
        var voteId:String? = ""
        var chatLog: Util.ChatLog? = null
        var waitUSerIdList= arrayListOf<String>()
        var movieCandidateList = arrayListOf<MovieByKW>()
        var selectedMovieList = arrayListOf<MovieByKW>()


        fun newInstance(list: ArrayList<User>, chatlog: Util.ChatLog, vId: String, waituserlist: ArrayList<String>, moviecandidatelist: ArrayList<MovieByKW>): VoteMovieActionFragment {
            chatLogId = chatlog!!.chatLogId
            selectedUserList = list
            voteId = vId
            chatLog = chatlog
            waitUSerIdList = waituserlist
            movieCandidateList = moviecandidatelist
            var args = Bundle()
            var fragment = VoteMovieActionFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    lateinit var goBackToShowVoteBtn: ImageView
    lateinit var mCallbackToShowVote: OnMovieVoteActionGoBackListener
    lateinit var recyclerViewMovieVoteAction : RecyclerView
    lateinit var confirmBtn: Button
    var voteMovieAdapter = GroupAdapter<GroupieViewHolder>()

    interface OnMovieVoteActionGoBackListener {
        fun movieVoteActionGoBack(list: ArrayList<User>, chatlog: Util.ChatLog, vId: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_vote_movie_action, container, false)
        goBackToShowVoteBtn = view.findViewById(R.id.ic_go_back_vote_action)
        recyclerViewMovieVoteAction = view.findViewById(R.id.recyclerview_vote_action)
        confirmBtn = view.findViewById(R.id.vote_button_vote_action)
        recyclerViewMovieVoteAction.adapter = voteMovieAdapter

        showImgListByIdView(movieCandidateList,recyclerViewMovieVoteAction)

        confirmBtn.setOnClickListener{
            Log.d(TAG,"choose the num of movies to vote: ${selectedMovieList.size}")
        }

        goBackToShowVoteBtn.setOnClickListener {
            movieCandidateList.clear()
            voteMovieAdapter.clear()
            mCallbackToShowVote.movieVoteActionGoBack(selectedUserList, chatLog!!, voteId!!)

        }
        return view
    }


    fun showImgListByIdView(selectedMovieChoseList: ArrayList<MovieByKW>, recyclerMovieByKWView: RecyclerView){
        voteMovieAdapter.clear()
        for (i in 0 until selectedMovieChoseList.size) {
            var curr = selectedMovieChoseList[i]
            voteMovieAdapter.add(VoteItemSelected(MovieByKWVote(curr.movieName,curr.movieId,curr.movieImageUrl,false)))
        }
        recyclerMovieByKWView.adapter = voteMovieAdapter

        voteMovieAdapter.setOnItemClickListener { item, view ->
            Log.d(TAG,"click the movie in the vote action")

            item as VoteItemSelected

            var  iterator = selectedMovieList.iterator()

            if (item.movie.selected){
                item.movie.selected = false
                if(selectedMovieList!= null){
                    iterator.forEach {
                        if ( it.movieId.equals(item.movie.movieId)){
                            iterator.remove()
                            Log.d(TAG,"selectedMovieList length = ${selectedMovieList.size}")
                            Log.d(TAG,"item movieName = ${item.movie.movieName} + ${item.movie.selected}")
                        }
                    }

                }
                voteMovieAdapter.notifyDataSetChanged()

            } else {
                item.movie.selected = true
                selectedMovieList.add(MovieByKW(item.movie.movieName,item.movie.movieId,item.movie.movieImageUrl,0))
                Log.d(TAG,"selectedContacts length = ${selectedMovieList.size}")
                Log.d(TAG,"item movieName = ${item.movie.movieName} + ${item.movie.selected}")
                voteMovieAdapter.notifyDataSetChanged()
            }

        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            //mCallback initialize
            mCallbackToShowVote = context as OnMovieVoteActionGoBackListener

        } catch (e: ClassCastException) {
        }
    }




}
