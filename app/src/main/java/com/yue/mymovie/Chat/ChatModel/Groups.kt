package com.yue.mymovie.Chat.ChatModel

import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.Util
import java.sql.Timestamp

class GroupsChat (
    val groupId : String,
    var groupCreater: String,
    var groupMember :ArrayList<String>

){
    constructor():this("","", arrayListOf())
}

class TwoPersonChat (
    val TwoPersonChatId : String,
    var groupMember :ArrayList<String>

){
    constructor():this("", arrayListOf())
}


class MessageType (
    val messageType : String,
    var messageId: String
){
    constructor():this("","")
}


class ChatTtpe (
    val ChatType : String,
    var GroupOrTwoPersonId: String
){
    constructor():this("","")
}

class ListLatestMessage(
    val chatLog : Util.ChatLog,
    val selectedUserList: ArrayList<User>,
    val imageUri : String,
    val text: String,
    val readOrNot:Boolean,
    val timestamp: Long
){
    constructor():this( Util.ChatLog(), arrayListOf<User>(),"","",true,-1)
}