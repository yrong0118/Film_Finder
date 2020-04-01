package com.yue.mymovie.Chat.VoteModel


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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.Chat.ChatModel.MovieByKW
import com.yue.mymovie.Chat.ChatModel.MovieByKWVote
import com.yue.mymovie.Chat.ChatModel.MovieImgById
import com.yue.mymovie.Chat.ChatModel.VoteItemSelected
import com.yue.mymovie.Chat.VoteDialog
import com.yue.mymovie.LoginOrRegister.User

import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.retrofit.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ShowSearchMovieListFragment : Fragment() {

    companion object {

        val TAG = "ShowSearchMovieListFragment"
        var selectedList = arrayListOf<User>()
        var movieVoteItemSelectedList :ArrayList<MovieByKW> = arrayListOf()
        var selectedMovieList = arrayListOf<MovieByKW>()
        var chatLog = Util.ChatLog()
        var selectedVoteMovieList: ArrayList<MovieByKW> = arrayListOf()
        var api: String = ""
        var page: String = ""
        var imgFrontPath: String = ""
        var searchMovieKeyWordText = ""

        fun newInstance(_selectedList: ArrayList<User>,_chatLog: Util.ChatLog,_searchMovieKeyWordText:String): ShowSearchMovieListFragment{
            selectedList = _selectedList
            chatLog = _chatLog
            searchMovieKeyWordText = _searchMovieKeyWordText
            var args = Bundle()
            var fragment = ShowSearchMovieListFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

    interface OnNewVoteGoBackListener {
        fun newVoteGoBack(selectedList: ArrayList<User>, chatLog: Util.ChatLog)
    }


    lateinit var recyclerViewNewVote: RecyclerView
    lateinit var confirmBtn: Button
    lateinit var goBack: ImageView
    lateinit var mCallbackToChat: OnNewVoteGoBackListener
    lateinit var mCallbackToSearchMovie:OnNewVoteSearchMovieListener
    var adapterMovieSearchByKW = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_search_movie_list, container, false)

        recyclerViewNewVote = view.findViewById(R.id.recyclerview_vote_movie_choose)
        confirmBtn = view.findViewById(R.id.vote_button_vote_movie_choose)
        goBack = view.findViewById(R.id.ic_go_back_vote_movie_choose)
        recyclerViewNewVote.adapter = adapterMovieSearchByKW

        confirmBtn.setOnClickListener {
            if () {}
            else {
                adapterMovieSearchByKW.clear()
            }
        }

        goBack.setOnClickListener {
            adapterMovieSearchByKW.clear()
        }

        showSearchMovieListDialog(searchMovieKeyWordText)
        return view
    }

    fun getMovieListData(movieSearchKeyWord : String,recyclerMovieByKWView: RecyclerView){
        var movieSelectedVoteRequestApi = RetrofitClient.instance.create(
            MovieSelectedVoteRequestApi::class.java)
        movieSelectedVoteRequestApi.getMovieListByKeyword(movieSearchKeyWord,
            VoteDialog.page,
            VoteDialog.api
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> showMovieListByKwView(baseResponse,recyclerMovieByKWView) },
                { throwable ->
                    //*******************************
                    if (VoteDialog.context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(VoteDialog.context, "Keywords Movies Search API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }


    fun showMovieListByKwView(movieSelectedResponse: MovieSelectedResponse, recyclerMovieByKWView: RecyclerView){
        var movieIdList = Vector<MovieByKW>()
        if (movieSelectedResponse != null && movieSelectedResponse.results.size > 0) {
            for (i in 0 until movieSelectedResponse.results.size){
                Log.d(ChatLogFragment.TAG,"result.size: $movieSelectedResponse.results.size")
                var movieName = movieSelectedResponse.results.get(i).movieName
                var movieId = movieSelectedResponse.results.get(i).movieId
                var movieImageUrl = ""
                var movieByKW = MovieByKW(movieName,movieId,movieImageUrl,0)
                var movieByKWVote = MovieByKWVote(movieName,movieId,movieImageUrl,false)
                movieIdList.add(movieByKW)
                getMovieImgData(movieByKWVote,movieId,recyclerMovieByKWView)

            }
        }
    }

    fun getMovieImgData(movieByKWVote: MovieByKWVote, movieId : String, recyclerMovieByKWView: RecyclerView){
        var movieImgVoteRequestApi = RetrofitClient.instance.create(MovieImgVoteRequestApi::class.java)
        movieImgVoteRequestApi.getMovieImgById(movieId, VoteDialog.api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> showImgListByIdView(movieByKWVote, baseResponse,recyclerMovieByKWView) },
                { throwable ->
                    //*******************************
                    if (VoteDialog.context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(VoteDialog.context, "Movies Image Search By Id API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }

    fun showImgListByIdView(movieByKW: MovieByKWVote, movieImgResponse: MovieImgResponse, recyclerMovieByKWView: RecyclerView){
        var movieImgList = Vector<MovieImgById>()
        if (movieImgResponse != null && movieImgResponse.results.size > 0) {
            for (i in 0 until movieImgResponse.results.size){
                Log.d(ChatLogFragment.TAG,"result.size: $movieImgResponse.results.size")
                var movieImageUrl = VoteDialog.imgFrontPath + movieImgResponse.results.get(i).movieImageUrl
                movieImgList.add(MovieImgById(movieImageUrl))
            }
            movieByKW.movieImageUrl = movieImgList.get(0).movieImageUrl
        }
        ChatLogFragment.adapterMovieSearchByKW.add(VoteItemSelected(movieByKW))
        recyclerMovieByKWView.adapter = ChatLogFragment.adapterMovieSearchByKW

        ChatLogFragment.adapterMovieSearchByKW.setOnItemClickListener { item, view ->
            Log.d(ChatLogFragment.TAG,"click the movie in the vote")

            item as VoteItemSelected

            var  iterator = VoteDialog.selectedMovieList.iterator()

            if (item.movie.selected){
                item.movie.selected = false
                if(VoteDialog.selectedMovieList != null){
                    iterator.forEach {
                        if ( it.movieId.equals(item.movie.movieId)){
                            iterator.remove()
                            Log.d(VoteDialog.TAG,"selectedContacts length = ${VoteDialog.selectedMovieList.size}")
                            Log.d(VoteDialog.TAG,"item movieName = ${item.movie.movieName} + ${item.movie.selected}")
                        }
                    }

                }
                ChatLogFragment.adapterMovieSearchByKW.notifyDataSetChanged()

            } else {
                item.movie.selected = true
                VoteDialog.selectedMovieList.add(MovieByKW(item.movie.movieName,item.movie.movieId,item.movie.movieImageUrl,0))
                Log.d(VoteDialog.TAG,"selectedContacts length = ${VoteDialog.selectedMovieList.size}")
                Log.d(VoteDialog.TAG,"item movieName = ${item.movie.movieName} + ${item.movie.selected}")
                ChatLogFragment.adapterMovieSearchByKW.notifyDataSetChanged()
            }

        }

    }

}
