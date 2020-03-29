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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.yue.mymovie.Chat.ChatModel.VoteMovieItem
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.getMovieGrade
import com.yue.mymovie.Chat.VoteModel.Firebase.Companion.getRestUserList
import com.yue.mymovie.Chat.VoteModel.WaitVoteUser

import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.retrofit.BaseResponse
import com.yue.mymovie.retrofit.MovieDetailRequestApi
import com.yue.mymovie.retrofit.RetrofitClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ShowVoteMoveListFragment : Fragment() {

    companion object {
        val TAG = "ShowVoteMoveList"
        var selectedList = arrayListOf<User>()
        var chatLogId: String? = ""
        var voteId:String? = ""
        var chatLog: Util.ChatLog? = null
        var voteMovieGradeList= arrayListOf<VoteMovieGrade>()
        var movieCandidateSet = mutableSetOf<String>()
        var waitUserVoteList= arrayListOf<WaitVoteUser>()
        var waitUserIdSet = mutableSetOf<String>()
        var voteMovieByKWList= arrayListOf<MovieByKW>()
        var start = 0
        var startUser = 0

        fun newInstance(list: ArrayList<User>, chatlog: Util.ChatLog, vId: String): ShowVoteMoveListFragment {
            chatLogId = chatlog!!.chatLogId
            selectedList = list
            voteId = vId
            chatLog = chatlog
            var args = Bundle()
            var fragment = ShowVoteMoveListFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    interface OnMovieVoteShowGoBackListener {
        fun movieVoteShowGoBack(list: ArrayList<User>, chatLog: Util.ChatLog)
    }

    interface GotoVoteActionListener {
        fun gotoVoteAction(list: ArrayList<User>, chatlog: Util.ChatLog, voteId: String, waitUserList: ArrayList<WaitVoteUser>, movieCandidateList: ArrayList<MovieByKW>)
    }

    lateinit var goBackToChatLogBtn: ImageView
    lateinit var mCallbackToChat: OnMovieVoteShowGoBackListener
    lateinit var mCallbackToVoteAction:GotoVoteActionListener
    lateinit var recyclerViewMovieVote : RecyclerView
    lateinit var voteBtn: Button


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
        goBackToChatLogBtn.setOnClickListener {
            voteMovieAdapter.clear()
            mCallbackToChat.movieVoteShowGoBack(selectedList,chatLog!!)
        }

        voteBtn.setOnClickListener{
            Log.d(TAG,"waitUSerIdList.size: $waitUserVoteList.size}")
            mCallbackToVoteAction.gotoVoteAction(selectedList, chatLog!!, voteId!!,waitUserVoteList,voteMovieByKWList)
            voteMovieAdapter.clear()
        }

        movieCandidateSet.clear()
        var movieList = arrayListOf<VoteMovieGrade>()
        getMovieGrade(movieList, voteId!!){
            Log.d(TAG ,"voteMovieGradeList size is : ${it.size}")

            for (i in start until it.size){
                if (!movieCandidateSet.contains(it[i].MovieId)){
                    movieCandidateSet.add(it[i].MovieId)
                    voteMovieGradeList.add(it[i])
                    Log.d(TAG ,"movie id is : ${it[i].MovieId}")
                    Log.d(TAG ,"movie grade is : ${it[i].grade}")
                    var currMovieByKW = MovieByKW("",it[i].MovieId,"",it[i].grade)
                    getMovieData(currMovieByKW)
                }
            }
            start ++
        }

        waitUserIdSet.clear()
        var userList = arrayListOf<WaitVoteUser>()
        getRestUserList(userList, voteId!!){
            Log.d(TAG ,"waitUSerIdList size is : ${it.size}")
            for (i in startUser until it.size){
                if (!waitUserIdSet.contains(it[i].UserId)){
                    waitUserIdSet.add(it[i].UserId)
                    waitUserVoteList.add(it[i])
                    Log.d(TAG ,"movie id is : ${it[i]}")
                }

            }
            startUser ++
        }
        return view
    }

    fun getMovieData(selectedMovie : MovieByKW) {
        voteMovieAdapter.clear()
        var movieDetailRequestApi = RetrofitClient.instance.create(MovieDetailRequestApi::class.java)
        val api = getString(R.string.glu_KEY)
        val language = getString(R.string.language)
        movieDetailRequestApi.getMovieDetailById(selectedMovie.movieId,language,api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> detail(baseResponse,selectedMovie) },
                { throwable ->
                    //*******************************
                    if (context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(this.context, "Movies API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }
    fun detail(response: BaseResponse,selectedMovie: MovieByKW){
        val filmName = response.filmName
        val filmImg= getString(R.string.img_front_path)+ response.filmImg

        selectedMovie.movieName = filmName
        selectedMovie.movieImageUrl = filmImg

        voteMovieByKWList.add(selectedMovie)
        voteMovieAdapter.add(VoteMovieItem(selectedMovie,selectedList.size))

    }

//    fun getMovieGrade( getList: (ArrayList<VoteMovieGrade>) -> Unit){
//        var ref = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${ShowVoteMoveListFragment.voteId}/movieVoteGrade")
//        ref.addChildEventListener(object: ChildEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
//
//            }
//
//            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
//                val voteMovieGrade = dataSnapshot.getValue(VoteMovieGrade::class.java)
//                if(voteMovieGrade != null) {
//                    voteMovieGradeList!!.add(voteMovieGrade)
//                    getList(voteMovieGradeList!!)
//                }
//            }
//
//            override fun onChildRemoved(p0: DataSnapshot) {
//
//            }
//        })
//    }





    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallbackToChat = context as OnMovieVoteShowGoBackListener
            mCallbackToVoteAction = context as GotoVoteActionListener
        } catch (e: ClassCastException) {
        }
    }





}
