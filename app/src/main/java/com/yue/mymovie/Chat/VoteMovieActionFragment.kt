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
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.Chat.ChatModel.MovieByKWVote
import com.yue.mymovie.Chat.ChatModel.VoteItemSelected
import com.yue.mymovie.Chat.VoteModel.Firebase
import com.yue.mymovie.Chat.VoteModel.VoteMovieGrade
import com.yue.mymovie.Chat.VoteModel.WaitVoteUser
import com.yue.mymovie.LoginOrRegister.User

import com.yue.mymovie.R
import com.yue.mymovie.Util

class VoteMovieActionFragment : Fragment() {

    companion object {
        val TAG = "VoteActionMoveList"
        var selectedList = arrayListOf<User>()
        var chatLogId: String? = ""
        var voteId:String? = ""
        var chatLog: Util.ChatLog? = null
        var waitUserIdList= arrayListOf<WaitVoteUser>()
        var movieCandidateList = arrayListOf<MovieByKW>()
        var movieCandidateSet = mutableSetOf<String>()
        var voteMovieGradeList= arrayListOf<VoteMovieGrade>()
        var selectedMovieList = arrayListOf<MovieByKW>()
        var selectedMovieSet = mutableSetOf<String>()
        var voted = false


        fun newInstance(_selectedUserList: ArrayList<User>, _chatLog: Util.ChatLog, _voteId: String, _waitUserIdList: ArrayList<WaitVoteUser>, _movieCandidateList: ArrayList<MovieByKW>): VoteMovieActionFragment {
            chatLogId = _chatLog!!.chatLogId
            selectedList = _selectedUserList
            voteId = _voteId
            chatLog = _chatLog
            waitUserIdList = _waitUserIdList
            movieCandidateList = _movieCandidateList
            voted = false
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
        fun movieVoteActionGoBack(list: ArrayList<User>, chatlog: Util.ChatLog, voteId: String)
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


        goBackToShowVoteBtn.setOnClickListener {
            movieCandidateList.clear()
            movieCandidateSet.clear()
            voteMovieAdapter.clear()
            mCallbackToShowVote.movieVoteActionGoBack(selectedList, chatLog!!, voteId!!)

        }

        Util.fetchCurrentUser2 { currentUser ->



            confirmBtn.setOnClickListener{

                Log.d(TAG,"selectedMovieSet.size :${selectedMovieSet.size}")

                //delete waiteVoteUserId

                val opWVUserRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId!!}/waiteVoteUserId")

                opWVUserRef.addListenerForSingleValueEvent(object:ValueEventListener{

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG,"Update waiting user List Failed:$error")

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        for(waitUser in dataSnapshot.children){
                            waitUser.ref.removeValue()
                        }
                        Log.d(TAG,"Update waiting user List Successfully")
                    }
                })

                //delete movieVoteGrade

                val opMVRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("/${Util.VOTES}/${voteId!!}/movieVoteGrade")

                opMVRef.addListenerForSingleValueEvent(object:ValueEventListener{

                    override fun onCancelled(error: DatabaseError) {
                        Log.d(TAG,"Update waiting movie grade List Failed:$error")

                    }

                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        for(voteVote in dataSnapshot.children){
                            voteVote.ref.removeValue()
                        }
                        Log.d(TAG,"Update waiting movie grade List Successfully")
                    }
                })


                var waitVoteUserList = getWaitVoteUserListById(waitUserIdList,currentUser)
                Log.d(TAG,"Update wait user List.size: ${waitVoteUserList.size}")


                Firebase.addWaitVoteUserList(voteId!!, waitVoteUserList)

                movieCandidateSet.clear()
                selectedMovieSet.clear()
                var movieList = arrayListOf<VoteMovieGrade>()

                for (i in 0 until selectedMovieList.size){
                    selectedMovieSet.add(selectedMovieList[i].movieId)
                }

                Firebase.getMovieGrade (voteMovieGradeList, voteId!!){
                    Log.d(TAG, "voteMovieGradeList size is : ${it.size}")

                    for (i in 0 until it.size) {
//                        Log.d(ShowVoteMoveListFragment.TAG, "movie id is : ${it[i].MovieId}")
//                        Log.d(ShowVoteMoveListFragment.TAG, "movie grade is : ${it[i].grade}")

                        if(!movieCandidateSet.contains(it[i].MovieId)){
                            movieCandidateSet.add(it[i].MovieId)
                            Log.d(TAG,"movieCandidateSet.size: ${movieCandidateSet.size}")
                            if (selectedMovieSet.contains(it[i].MovieId)){
                                it[i].grade += 1
                                Log.d(TAG,"movieid: ${it[i].MovieId}, new grade: ${it[i].grade}")
                            } else {
                                Log.d(TAG,"movieid: ${it[i].MovieId}, remain grade: ${it[i].grade}")
                            }
                            movieList.add(VoteMovieGrade(it[i].MovieId,it[i].grade))

                            Log.d(TAG,"movieCandidateList.size: ${movieCandidateList.size}")

                            if (movieCandidateSet.size.equals(movieCandidateList.size)) {
                                Log.d(TAG,"Update movie grade List.size: ${movieList.size}")
                                Firebase.addMovieVoteList(voteId!!, movieList)
                                break
                            }
                        }
                    }

                }
                voteMovieAdapter.clear()

                mCallbackToShowVote.movieVoteActionGoBack(selectedList, chatLog!!,voteId!!)

            }

        }

        return view
    }


    private fun getWaitVoteUserListById(waitUserIdList: ArrayList<WaitVoteUser>, currentUser: User): ArrayList<WaitVoteUser> {
//        var userSet = mutableSetOf<String>()
        var list = arrayListOf<WaitVoteUser>()
        var iterator = waitUserIdList.iterator()

        iterator.forEach {
            if (it.UserId.equals(currentUser.uid)){
                iterator.remove()
            }else {
                list.add(it)
            }
        }

        return list
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

            if (item.movieByKWVote.selected){
                item.movieByKWVote.selected = false
                if(selectedMovieList!= null){
                    iterator.forEach {
                        if ( it.movieId.equals(item.movieByKWVote.movieId)){
                            iterator.remove()
                            Log.d(TAG,"selectedMovieList length = ${selectedMovieList.size}")
                            Log.d(TAG,"item movieName = ${item.movieByKWVote.movieName} + ${item.movieByKWVote.selected}")
                        }
                    }

                }
                voteMovieAdapter.notifyDataSetChanged()

            } else {
                item.movieByKWVote.selected = true
                selectedMovieList.add(MovieByKW(item.movieByKWVote.movieName,item.movieByKWVote.movieId,item.movieByKWVote.movieImageUrl,0))
                Log.d(TAG,"selectedContacts length = ${selectedMovieList.size}")
                Log.d(TAG,"item movieName = ${item.movieByKWVote.movieName} + ${item.movieByKWVote.selected}")
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
