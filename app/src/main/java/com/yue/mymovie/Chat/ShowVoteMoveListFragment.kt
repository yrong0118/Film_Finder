package com.yue.mymovie.Chat

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.Chat.VoteModel.VoteMovieGrade
import com.yue.mymovie.LoginOrRegister.User
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yue.mymovie.Chat.ChatModel.VoteMovieItem
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.getMovieGrade
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.getRestUserList
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.getvoteDate
import com.yue.mymovie.Chat.VoteModel.WaitVoteUser

import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.Util.Companion.convertTime
import com.yue.mymovie.Util.Companion.getTimestamp
import com.yue.mymovie.retrofit.BaseResponse
import com.yue.mymovie.retrofit.MovieDetailRequestApi
import com.yue.mymovie.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ShowVoteMoveListFragment : Fragment() {

    companion object {
        val TAG = Util.showVoteMoveListFragment
        var api = ""
        var language = ""
        var imgFrontPath= ""
        var chatLogId: String? = ""
        var voteId:String? = ""
        var chatLog: Util.ChatLog? = null
        var selectedList = arrayListOf<User>()
        lateinit var mCallbacktoDetail: OnMovieVoteShowToDetailListener

        fun newInstance(list: ArrayList<User>, chatlog: Util.ChatLog, _voteId: String): ShowVoteMoveListFragment {
            Log.d(TAG,"new instance VoteId is ${_voteId}")
            chatLogId = chatlog!!.chatLogId
            selectedList = list
            voteId = _voteId
            chatLog = chatlog
            var args = Bundle()
            var fragment = ShowVoteMoveListFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    var voteMovieGradeList= arrayListOf<VoteMovieGrade>()
    var movieCandidateSet = mutableSetOf<String>()
    var waitUserVoteList= arrayListOf<WaitVoteUser>()
    var waitUserIdSet = mutableSetOf<String>()
    var voteMovieByKWList= arrayListOf<MovieByKW>()

    interface OnMovieVoteShowGoBackListener {
        fun movieVoteShowGoBack(list: ArrayList<User>, chatLog: Util.ChatLog)
    }

    interface GotoVoteActionListener {
        fun gotoVoteAction(list: ArrayList<User>, chatlog: Util.ChatLog, voteId: String, waitUserList: ArrayList<WaitVoteUser>, movieCandidateList: ArrayList<MovieByKW>)
    }

    interface OnMovieVoteShowToDetailListener {
        fun movieVoteShowToDetail(list: ArrayList<User>, chatLog: Util.ChatLog,movie:MovieByKW)
    }


    lateinit var goBackToChatLogBtn: ImageView
    lateinit var mCallbackToChat: OnMovieVoteShowGoBackListener
    lateinit var mCallbackToVoteAction:GotoVoteActionListener
    lateinit var recyclerViewMovieVote : RecyclerView
    lateinit var voteBtn: Button
    lateinit var startVoteDateTV: TextView
    lateinit var endVoteDateTV: TextView

    var voteMovieAdapter = GroupAdapter<GroupieViewHolder>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_show_vote_move_list, container, false)

        goBackToChatLogBtn = view.findViewById(R.id.ic_go_back_show_vote)
        recyclerViewMovieVote = view.findViewById(R.id.recyclerview_show_vote)
        recyclerViewMovieVote.adapter = voteMovieAdapter
        voteBtn = view.findViewById(R.id.vote_button_show_vote)
        api = getString(R.string.glu_KEY)
        language = getString(R.string.language)
        imgFrontPath= getString(R.string.img_front_path)
        startVoteDateTV = view.findViewById(R.id.start_vote)
        endVoteDateTV = view.findViewById(R.id.end_vote)
        var startVote : Long = -1
        var endVote : Long = -1

        goBackToChatLogBtn.setOnClickListener {
            voteMovieAdapter.clear()
            movieCandidateSet.clear()
            waitUserIdSet.clear()
            waitUserVoteList.clear()
            mCallbackToChat.movieVoteShowGoBack(selectedList,chatLog!!)
        }

        voteBtn.setOnClickListener{
            val timestamp = getTimestamp()
            if (endVote < timestamp) {
                Toast.makeText(this.context,"Vote has expired! You Cannot Vote nymore",Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG,"waitUserIdList.size: $waitUserVoteList.size}")
                voteMovieAdapter.clear()
                movieCandidateSet.clear()
                waitUserIdSet.clear()
                Log.d(TAG,"new Instance: voteMovieByKWList size: ${voteMovieByKWList.size}}")
                mCallbackToVoteAction.gotoVoteAction(selectedList, chatLog!!, voteId!!,waitUserVoteList,voteMovieByKWList)
            }

        }
        var movieList = arrayListOf<VoteMovieGrade>()
        getMovieGrade(movieList, voteId!!){

            Log.d(TAG ,"voteMovieGradeList size is : ${it.size}")

            if (!movieCandidateSet.contains(it[it.size-1].MovieId)){
                movieCandidateSet.add(it[it.size-1].MovieId)
                voteMovieGradeList.add(it[it.size-1])
                Log.d(TAG ,"movie id is : ${it[it.size-1].MovieId}")
                Log.d(TAG ,"movie grade is : ${it[it.size-1].grade}")
                var currMovieByKW = MovieByKW("",it[it.size-1].MovieId,"",it[it.size-1].grade)
                Log.d(TAG,"current movie id: ${currMovieByKW.movieId} before geting pic")
                getMovieData(currMovieByKW)
            }
        }

        getvoteDate(voteId!!,"startVoteTimeStamp"){
            startVote = it
            val date = convertTime(it)
            startVoteDateTV.setText(date)
        }

        getvoteDate(voteId!!,"endVoteTimeStamp"){
            endVote = it
            val date = convertTime(it)
            endVoteDateTV.setText(date)
        }

        var userList = arrayListOf<WaitVoteUser>()
        getRestUserList(userList, voteId!!){
            Log.d(TAG ,"waitUSerIdList size is : ${it.size}")
            if (!waitUserIdSet.contains(it[it.size-1].UserId)){
                waitUserIdSet.add(it[it.size-1].UserId)
                waitUserVoteList.add(it[it.size-1])
                Log.d(TAG ,"movie id is : ${it[it.size-1].UserId}")
            }

        }
        return view
    }

    fun getMovieData(selectedMovie : MovieByKW) {

        var movieDetailRequestApi = RetrofitClient.instance.create(MovieDetailRequestApi::class.java)

        movieDetailRequestApi.getMovieDetailById(selectedMovie.movieId,language,api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> detail(baseResponse,selectedMovie) },
                { throwable ->
                    //*******************************
                    if (this.context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(this.context, "Movies API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }
    fun detail(response: BaseResponse,selectedMovie: MovieByKW){
        val filmImg= imgFrontPath + response.filmImg
        val filmName = response.filmName
        selectedMovie.movieName = filmName
        selectedMovie.movieImageUrl = filmImg
        voteMovieByKWList.add(selectedMovie)
        Log.d(TAG,"selectedMovie name: ${selectedMovie}, size of movie list: ${voteMovieByKWList.size}")
        voteMovieAdapter.add(VoteMovieItem(selectedMovie,selectedList.size))
        Log.d(TAG,"add adapter : curr movie name: ${selectedMovie.movieName}")
        voteMovieAdapter.notifyDataSetChanged()

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallbackToChat = context as OnMovieVoteShowGoBackListener
            mCallbackToVoteAction = context as GotoVoteActionListener
            mCallbacktoDetail = context as OnMovieVoteShowToDetailListener
        } catch (e: ClassCastException) {
        }
    }





}
