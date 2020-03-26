package com.yue.mymovie.Chat.VoteModel

import com.yue.mymovie.Chat.ChatModel.MovieVote
import java.sql.Timestamp

data class ChatVote(val voteId: String, val sendUserId: String, var waitVoteUserId: ArrayList<String>, val startVoteTimeStamp: Long, val endVoteTimestamp: Long) {
    constructor() : this("", "", arrayListOf<String>(),-1,-1)
    //movieList(MovieId, VoteGread)
}

//data class ChatVote(val voteId: String, val sendUserId: String, var waitVoteUserId: ArrayList<String>, var movieVoteUserId: ArrayList<MovieVote>, val startVoteTimeStamp: Long, val endVoteTimestamp: Long) {
//    constructor() : this("", "", arrayListOf<String>(), arrayListOf<MovieVote>(),-1,-1)
//    //movieList(MovieId, VoteGread)
//}