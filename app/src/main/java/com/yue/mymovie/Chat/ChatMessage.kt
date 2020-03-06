package com.yue.mymovie.Chat

data class ChatMessage(val messageId: String, val sendUserId: String, val text: String, val timestamp: Long) {
    constructor() : this("", "","",-1)
}