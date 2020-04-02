//package com.yue.mymovie.Chat
//
//import android.content.Context
//import android.content.DialogInterface
//import android.util.Log
//import android.view.LayoutInflater
//import android.widget.EditText
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AlertDialog
//import androidx.recyclerview.widget.RecyclerView
//import com.xwray.groupie.GroupAdapter
//import com.xwray.groupie.GroupieViewHolder
//import com.yue.mymovie.Chat.ChatModel.*
//import com.yue.mymovie.Chat.ChatModel.ChatLogVote.Companion.performSendVoteToGroup
//
//import com.yue.mymovie.R
//import com.yue.mymovie.Util
//import com.yue.mymovie.retrofit.*
//import io.reactivex.android.schedulers.AndroidSchedulers
//import io.reactivex.schedulers.Schedulers
//import java.util.*
//import kotlin.collections.ArrayList
//
//class VoteDialog {
//
//    companion object{
//        val TAG = "ChatVoteDialog"
//        var selectedList = ChatLogFragment.selectedList
//        var movieVoteItemSelectedList :ArrayList<MovieByKW> = arrayListOf()
//        var selectedMovieList = arrayListOf<MovieByKW>()
//        var chatLogId = ChatLogFragment.chatLogId
//        var selectedVoteMovieList: ArrayList<MovieByKW> = arrayListOf()
//        var context: Context? = null
//        var api: String = ""
//        var page: String = ""
//        var imgFrontPath: String = ""
//
//        fun showVoteMovieDialog(cxt: Context,movieList:ArrayList<MovieByKW>,key:String, p: String, path: String) {
//            context = cxt
//            api = key
//            page = p
//            imgFrontPath = path
//            val builder = AlertDialog.Builder(context!!)
//            val dialog = builder.create()
//            builder.setTitle("Creat New Movie Vote")
//
//            val inflater = LayoutInflater.from(context!!)
//            val new_vote_layout = inflater.inflate(R.layout.new_vote, null)
//            val searchMovieKeyWordTextView = new_vote_layout.findViewById<EditText>(R.id.search_movie_name_new_vote)
//            val addNewMovieBtn = new_vote_layout.findViewById<TextView>(R.id.add_movie_new_vote)
//            val selectDate = new_vote_layout.findViewById<TextView>(R.id.select_date_new_vote)
//            val recyclerViewNewVote = new_vote_layout.findViewById<RecyclerView>(R.id.recyclerview_newVote)
//            recyclerViewNewVote.adapter = ChatLogFragment.adapternewVote
//
////            addNewMovieBtn.setOnClickListener {
////                var searchMovieKeyWordText= searchMovieKeyWordTextView.text.toString().trim()
////                showSearchMovieListDialog(searchMovieKeyWordText)
////                dialog.dismiss()
////
////            }
//
//
//            for (i in 0 until selectedVoteMovieList.size){
//                ChatLogFragment.adapternewVote.add(VoteItem(selectedVoteMovieList.get(i)))
//            }
////        adapternewVote.add(VoteItem(MovieByKW( "Sonic the Hedgehog","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//aQvJ5WPzZgYVDrxLX4R6cLJCEaQ.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Birds of Prey (and the Fantabulous Emancipation of One Harley Quinn)","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//o1D5kEPPOGReel6CipiKOY5tPJt.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Sonic the Hedgehog","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//aQvJ5WPzZgYVDrxLX4R6cLJCEaQ.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Birds of Prey (and the Fantabulous Emancipation of One Harley Quinn)","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//o1D5kEPPOGReel6CipiKOY5tPJt.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Sonic the Hedgehog","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//aQvJ5WPzZgYVDrxLX4R6cLJCEaQ.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Birds of Prey (and the Fantabulous Emancipation of One Harley Quinn)","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//o1D5kEPPOGReel6CipiKOY5tPJt.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Sonic the Hedgehog","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//aQvJ5WPzZgYVDrxLX4R6cLJCEaQ.jpg")))
////        adapternewVote.add(VoteItem(MovieByKW( "Birds of Prey (and the Fantabulous Emancipation of One Harley Quinn)","454626","https://image.tmdb.org/t/p/w600_and_h900_bestv2//o1D5kEPPOGReel6CipiKOY5tPJt.jpg")))
//            builder.setView(new_vote_layout)
//
////        set Button
//
//            Util.fetchCurrentUser2 { currentUser ->
//                val timestamp = Util.getTimestamp()
//
////                builder.setPositiveButton("CONFIRM",
////                    DialogInterface.OnClickListener { dialogInterface, i ->
////                        dialogInterface.dismiss()
//////
////                        ChatLogFragment.adapterMovieSearchByKW.clear()
////                        ChatLogFragment.adapternewVote.clear()
////                        selectedVoteMovieList.clear()
////                    })
//
//                builder.setPositiveButton(
//                    "CONFIRM"
//                ) { dialogInterface, i ->
//                    dialogInterface.dismiss()
//                    if (selectedList.size == 1) {
//                        Log.d(TAG, "Attempt to send message.... to the single")
//    //                      performSendVoteToSingle(selectedList, currentUser,timestamp,timestamp)
//
//                    } else {
//                        val waitinglist = selectedList
//                        Log.d(TAG, "Attempt to send message.... to the group")
//                        Log.d(TAG, "movieVoteItemSelectedList length : ${movieVoteItemSelectedList.size}")
//                        performSendVoteToGroup(waitinglist, movieVoteItemSelectedList,currentUser,timestamp, timestamp)
//                    }
//                    ChatLogFragment.adapterMovieSearchByKW.clear()
//                    ChatLogFragment.adapternewVote.clear()
//                    selectedVoteMovieList.clear()
//                }
//
//
//                builder.setNegativeButton(
//                    "SEARCH MOVIE"
//                ) { dialogInterface, i ->
//
//                    var searchMovieKeyWordText= searchMovieKeyWordTextView.text.toString().trim()
//                    showSearchMovieListDialog(searchMovieKeyWordText)
//                    dialog.dismiss()
//                }
//
//
//                builder.setNeutralButton(
//                    "CANCEL"
//                ) { dialogInterface, i ->
//                    ChatLogFragment.adapterMovieSearchByKW.clear()
//                    ChatLogFragment.adapternewVote.clear()
//                    selectedVoteMovieList.clear()
//                    dialogInterface.dismiss()
//                }
//
//
//                fetchMovieAdapter(ChatLogFragment.adapternewVote)
//
//                builder.show()
//            }
//
//        }
//
//        private fun fetchMovieAdapter(adapternewVote: GroupAdapter<GroupieViewHolder>){
//            val inflater = LayoutInflater.from(context)
//            val new_vote_layout = inflater.inflate(R.layout.selected_movie_new_vote_row, null)
//
//
//        }
//
//        private fun showSearchMovieListDialog(movieSearchKeyWord:String) {
//            val dialog = AlertDialog.Builder(context!!)
//            dialog.setTitle("Plese Choose One Movie!")
////        dialog.setMessage("Please use email to register")
//
//            val inflater = LayoutInflater.from(context)
//            val new_vote_layout = inflater.inflate(R.layout.new_vote_search_movie, null)
//            val recyclerMovieByKWView = new_vote_layout.findViewById<RecyclerView>(R.id.recyclerview_movie_list_dialog)
//            getMovieListData(movieSearchKeyWord,recyclerMovieByKWView)
//            dialog.setView(new_vote_layout)
//
//            dialog.setNegativeButton("BACK"
//            ) { dialogInterface, i ->
//                dialogInterface.dismiss()
//                showVoteMovieDialog(context!!,selectedVoteMovieList, api, page, imgFrontPath)
//                ChatLogFragment.adapterMovieSearchByKW.clear()
//                selectedMovieList.clear()
//            }
//            dialog.setNeutralButton("CANCEL"){ dialogInterface, i ->
//                dialogInterface.dismiss()
//                ChatLogFragment.adapterMovieSearchByKW.clear()
//                ChatLogFragment.adapternewVote.clear()
//                selectedVoteMovieList.clear()
//                selectedMovieList.clear()
//
//            }
//
//            dialog.setPositiveButton(
//                "CONFIRM"
//            ) { dialogInterface, i ->
//                for (i in 0 until selectedMovieList.size) {
//                    movieVoteItemSelectedList.add(selectedMovieList[i])
//                }
//                for (i in 0 until movieVoteItemSelectedList.size){
//                    ChatLogFragment.adapternewVote.add(VoteItem(movieVoteItemSelectedList.get(i)))
//                }
//                dialogInterface.dismiss()
//                showVoteMovieDialog(context!!,selectedVoteMovieList, api, page, imgFrontPath)
//                ChatLogFragment.adapterMovieSearchByKW.clear()
//                selectedMovieList.clear()
//            }
//
//            dialog.show()
//        }
//
//
//
//
//    }
//
//
//
//
//
//}