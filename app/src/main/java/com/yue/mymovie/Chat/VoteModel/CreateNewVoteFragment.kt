package com.yue.mymovie.Chat.VoteModel

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.Chat.ChatModel.ChatLogVote
import com.yue.mymovie.Chat.ChatModel.ChatLogVote.Companion.performSendVoteToGroup
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.Chat.ChatModel.VoteItem
import com.yue.mymovie.LoginOrRegister.User

import com.yue.mymovie.R
import com.yue.mymovie.Util

class CreateNewVoteFragment : Fragment() {

    companion object {

        val TAG = "CreateNewVoteFragment"
        var selectedList = arrayListOf<User>()
        var movieVoteItemSelectedList :ArrayList<MovieByKW> = arrayListOf()
        var selectedMovieList = arrayListOf<MovieByKW>()
        var chatLog = Util.ChatLog()
        var selectedVoteMovieList: ArrayList<MovieByKW> = arrayListOf()

        fun newInstance(_selectedList: ArrayList<User>,_chatLog: Util.ChatLog):CreateNewVoteFragment{
            selectedList = _selectedList
            chatLog = _chatLog
            var args = Bundle()
            var fragment = CreateNewVoteFragment()
            fragment.setArguments(args)
            return fragment
        }


        fun newInstance(_selectedList: ArrayList<User>,_selectedMovieList: ArrayList<MovieByKW>,_chatLog: Util.ChatLog):CreateNewVoteFragment{
            selectedList = _selectedList
            chatLog = _chatLog
            selectedMovieList = _selectedMovieList
            var args = Bundle()
            var fragment = CreateNewVoteFragment()
            fragment.setArguments(args)
            return fragment
        }
    }

    lateinit var searchMovieKeyWordTextView : EditText
    lateinit var addNewMovieBtn: Button
    lateinit var selectDateEdt:EditText
    lateinit var recyclerViewNewVote:RecyclerView
    lateinit var confirmBtn:Button
    lateinit var goBack:ImageView
    lateinit var mCallbackToChat: OnNewVoteGoBackListener
    lateinit var mCallbackToSearchMovie:OnNewVoteSearchMovieListener
    var adapternewVote = GroupAdapter<GroupieViewHolder>()

    interface OnNewVoteGoBackListener {
        fun newVoteGoBack(selectedList: ArrayList<User>, chatLog: Util.ChatLog)
    }

    interface OnNewVoteSearchMovieListener {
        fun newVoteserchMovie(selectedList: ArrayList<User>, selectedMovieList: ArrayList<MovieByKW>, chatLog: Util.ChatLog,searchKeyWord:String)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"FrangmentCyle: onCreate")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG,"FrangmentCyle: onDetach")
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"FrangmentCyle: onDestroy")
    }
    override fun onStart() {
        super.onStart()

        Log.d(TAG,"FrangmentCyle: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"FrangmentCyle: onResume")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.d(TAG,"FrangmentCyle: onCreateView")
        val view =  inflater.inflate(R.layout.fragment_create_new_vote, container, false)

        searchMovieKeyWordTextView = view.findViewById(R.id.search_movie_name_new_vote)
        addNewMovieBtn = view.findViewById(R.id.add_movie_new_vote)
        selectDateEdt = view.findViewById(R.id.select_date_new_vote)
        recyclerViewNewVote = view.findViewById(R.id.recyclerview_newVote)
        confirmBtn = view.findViewById(R.id.vote_button_new_vote)
        goBack = view.findViewById(R.id.ic_go_back_new_vote)
        recyclerViewNewVote.adapter = adapternewVote

        for (i in 0 until selectedMovieList.size){
            adapternewVote.add(VoteItem(selectedMovieList.get(i)))
        }
        adapternewVote.notifyDataSetChanged()
        Util.fetchCurrentUser2 { currentUser ->
            val timestamp = Util.getTimestamp()
            confirmBtn.setOnClickListener{
                if(selectedMovieList.size == 0) {
                    Toast.makeText(this.context,"Please Search the Movies to Vote First!",Toast.LENGTH_SHORT).show()
                } else {
                    val waitinglist = selectedList
                    performSendVoteToGroup(waitinglist, selectedMovieList,currentUser,timestamp, timestamp)
                    Log.d(TAG, "Attempt to send message.... to the group")
                    Log.d(TAG, "movieVoteItemSelectedList length : ${selectedMovieList.size}")
                    adapternewVote.clear()
                    selectedVoteMovieList.clear()
                    mCallbackToChat.newVoteGoBack(selectedList, chatLog)
                }
            }

            addNewMovieBtn.setOnClickListener{
                var searchMovieKeyWordText= searchMovieKeyWordTextView.text.toString().trim()
                searchMovieKeyWordTextView.text.clear()
                adapternewVote.clear()
                mCallbackToSearchMovie.newVoteserchMovie(selectedList, selectedMovieList,chatLog,searchMovieKeyWordText)
            }

            goBack.setOnClickListener {
                adapternewVote.clear()
                selectedVoteMovieList.clear()
                mCallbackToChat.newVoteGoBack(selectedList, chatLog)

            }


        }
        return view
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallbackToSearchMovie = context as OnNewVoteSearchMovieListener
        mCallbackToChat = context as OnNewVoteGoBackListener
        Log.d(TAG,"FrangmentCyle: onAttach")

    }




}
