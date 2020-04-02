package com.yue.mymovie.Chat.VoteModel


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
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.yue.mymovie.Chat.ChatLogFragment
import com.yue.mymovie.Chat.ChatModel.*
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

        val TAG = "ShowSearchMovieListFr"
        var selectedList = arrayListOf<User>()
        var movieVoteItemSelectedList :ArrayList<MovieByKW> = arrayListOf()
        val selectedMovieCandidateList = arrayListOf<MovieByKW>()
        val selectedMovieList = arrayListOf<MovieByKW>()
        var chatLog = Util.ChatLog()
        var selectedVoteMovieList: ArrayList<MovieByKW> = arrayListOf()
        var api: String = ""
        var page: String = ""
        var imgFrontPath: String = ""
        var searchMovieKeyWordText = ""

        fun newInstance(_selectedList: ArrayList<User>,selectedMovieList:ArrayList<MovieByKW>,_chatLog: Util.ChatLog,_searchMovieKeyWordText:String): ShowSearchMovieListFragment{
            selectedList = _selectedList
            chatLog = _chatLog
            searchMovieKeyWordText = _searchMovieKeyWordText
            var args = Bundle()
            var fragment = ShowSearchMovieListFragment()
            fragment.setArguments(args)
            return fragment
        }

    }


    interface OnShowSearchMovieGoBackListener {
        fun showSearchMovieGoBack(selectedList: ArrayList<User>, selectedMovieList: ArrayList<MovieByKW>, chatLog: Util.ChatLog)
    }

    interface OnShowSearchMovieConfirmListener {
        fun showSearchMovieConfirm(selectedList: ArrayList<User>,chatLog: Util.ChatLog)
    }

    lateinit var recyclerMovieByKWView: RecyclerView
    lateinit var confirmBtn: Button
    lateinit var goBack: ImageView
    lateinit var mCallbackToNewVote: OnShowSearchMovieGoBackListener
    lateinit var mCallbackToChatlog:OnShowSearchMovieConfirmListener
    var adapterMovieSearchByKW = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show_search_movie_list, container, false)
        recyclerMovieByKWView = view.findViewById(R.id.recyclerview_vote_movie_choose)
        confirmBtn = view.findViewById(R.id.vote_button_vote_movie_choose)
        goBack = view.findViewById(R.id.ic_go_back_vote_movie_choose)
        api = getString(R.string.glu_KEY)
        page = getString(R.string.page1)
        imgFrontPath = getString(R.string.img_front_path)
        recyclerMovieByKWView.adapter = adapterMovieSearchByKW

        confirmBtn.setOnClickListener {
            if (selectedMovieCandidateList.size == 0) {
                Toast.makeText(this.context,"Please choose the Movies You Want to Choose First",Toast.LENGTH_SHORT).show()
            }
            else {
                for (i in 0 until selectedMovieCandidateList.size) {
                    selectedMovieList.add(selectedMovieCandidateList[i])
                }
                selectedMovieCandidateList.clear()
                adapterMovieSearchByKW.clear()
                mCallbackToNewVote.showSearchMovieGoBack(selectedList, selectedMovieList,chatLog)
                Log.d(TAG,"selectedMovie size when confirm= ${selectedMovieList.size}")
//                mCallbackToChatlog.showSearchMovieConfirm(selectedList, chatLog)

            }
        }

        goBack.setOnClickListener {
            selectedMovieCandidateList.clear()
            adapterMovieSearchByKW.clear()
            mCallbackToNewVote.showSearchMovieGoBack(selectedList, selectedMovieList,chatLog)
        }
        getMovieListData()
        return view
    }

    fun getMovieListData(){
        var movieSelectedVoteRequestApi = RetrofitClient.instance.create(
            MovieSelectedVoteRequestApi::class.java)
        val movieSearchKeyWord = searchMovieKeyWordText
        movieSelectedVoteRequestApi.getMovieListByKeyword(movieSearchKeyWord, page, api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> showMovieListByKwView(baseResponse) },
                { throwable ->
                    //*******************************
                    if (this.context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(this.context, "Keywords Movies Search API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }


    fun showMovieListByKwView(movieSelectedResponse: MovieSelectedResponse){
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
                getMovieImgData(movieByKWVote,movieId)

            }
        }
    }

    fun getMovieImgData(movieByKWVote: MovieByKWVote, movieId : String){
        var movieImgVoteRequestApi = RetrofitClient.instance.create(MovieImgVoteRequestApi::class.java)
        movieImgVoteRequestApi.getMovieImgById(movieId, api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> showImgListByIdView(movieByKWVote, baseResponse) },
                { throwable ->
                    //*******************************
                    if (this.context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(this.context, "Movies Image Search By Id API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }

    fun showImgListByIdView(movieByKWVote: MovieByKWVote, movieImgResponse: MovieImgResponse){
        var movieImgList = Vector<MovieImgById>()
        if (movieImgResponse != null && movieImgResponse.results.size > 0) {
            for (i in 0 until movieImgResponse.results.size){
                Log.d(TAG,"result.size: $movieImgResponse.results.size")
                var movieImageUrl = imgFrontPath + movieImgResponse.results.get(i).movieImageUrl
                movieImgList.add(MovieImgById(movieImageUrl))
            }
            movieByKWVote.movieImageUrl = movieImgList.get(0).movieImageUrl
        }
        adapterMovieSearchByKW.add(VoteItemSelected(movieByKWVote))
        recyclerMovieByKWView.adapter = adapterMovieSearchByKW

        adapterMovieSearchByKW.setOnItemClickListener { item, view ->
            Log.d(ChatLogFragment.TAG,"click the movie in the vote")

            item as VoteItemSelected

            var  iterator = selectedMovieCandidateList.iterator()

            if (item.movieByKWVote.selected){
                item.movieByKWVote.selected = false
                if(selectedMovieCandidateList.size != 0){
                    iterator.forEach {
                        if ( it.movieId.equals(item.movieByKWVote.movieId)){
                            iterator.remove()
                            Log.d(TAG,"selectedContacts length = ${selectedMovieCandidateList.size}")
                            Log.d(TAG,"item movieName = ${item.movieByKWVote.movieName} + ${item.movieByKWVote.selected}")
                        }
                    }

                }
                adapterMovieSearchByKW.notifyDataSetChanged()

            } else {
                item.movieByKWVote.selected = true
                selectedMovieCandidateList.add(MovieByKW(item.movieByKWVote.movieName,item.movieByKWVote.movieId,item.movieByKWVote.movieImageUrl,0))
                Log.d(TAG,"selectedContacts length = ${selectedMovieCandidateList.size}")
                Log.d(TAG,"item movieName = ${item.movieByKWVote.movieName} + ${item.movieByKWVote.selected}")
                adapterMovieSearchByKW.notifyDataSetChanged()
            }

        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallbackToNewVote = context as OnShowSearchMovieGoBackListener
        mCallbackToChatlog = context as OnShowSearchMovieConfirmListener

    }
}
