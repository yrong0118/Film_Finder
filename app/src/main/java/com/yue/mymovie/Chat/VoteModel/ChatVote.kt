package com.yue.mymovie.Chat.VoteModel

import java.sql.Timestamp

data class ChatVote(val voteId: String, val sendUserId: String, val startVoteTimeStamp: Long, val endVoteTimestamp: Long) {
    constructor() : this("", "", -1,-1)
    //movieList(MovieId, VoteGread)
}

class WaitVoteUser (
    var UserId: String
){
    constructor():this("")
}

class VoteMovieGrade (
    var MovieId: String,
    var grade: Int
){
    constructor():this("",0)
}

//data class ChatVote(val voteId: String, val sendUserId: String, var waitVoteUserId: ArrayList<String>, var movieVoteUserId: ArrayList<MovieVote>, val startVoteTimeStamp: Long, val endVoteTimestamp: Long) {
//    constructor() : this("", "", arrayListOf<String>(), arrayListOf<MovieVote>(),-1,-1)
//    //movieList(MovieId, VoteGread)
//}