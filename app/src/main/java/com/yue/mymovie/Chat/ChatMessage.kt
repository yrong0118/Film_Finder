package com.yue.mymovie.Chat

data class ChatMessage(val messageId: String, val sendUserId: String, val text: String, val timestamp: Long) {
    constructor() : this("", "","",-1)
}

data class VoteMessage(val voteId: String, val sendUserId: String, val startVoteTimestamp: Long) {
    constructor() : this("", "",-1)
}