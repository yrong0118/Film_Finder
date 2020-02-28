package com.yue.mymovie.Chat

data class ChatMessage(val id: String, val text: String, val fromId: String, val toId: ArrayList<String>, val timestamp: Long) {
    constructor() : this("", "", "", arrayListOf(), -1)
}