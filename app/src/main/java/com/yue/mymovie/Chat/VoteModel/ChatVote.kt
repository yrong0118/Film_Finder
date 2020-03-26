package com.yue.mymovie.Chat.VoteModel

import java.sql.Timestamp

data class ChatVote(val voteId: String, val sendUserId: String, var waitVoteUserId: ArrayList<String>, val startVoteTimeStamp: Long, val endVoteTimestamp: Long) {
    constructor() : this("", "", arrayListOf<String>(),-1,-1)
    //movieList(MovieId, VoteGread)
}