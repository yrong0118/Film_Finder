package com.yue.mymovie.Chat


import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.Chat.ChatModel.*
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.R
import com.yue.mymovie.Util
import com.yue.mymovie.Util.Companion.getLogChatHead
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*
import java.util.concurrent.CountDownLatch


/**
 * A simple [Fragment] subclass.
 */
class NewMessageFragment : Fragment() {

    lateinit var recyclerview_newmessage : RecyclerView
//    lateinit var mCallback:  MultipleChoiceListener
    lateinit var selectedBtn:ImageView
    lateinit var cancleBtn : TextView
    lateinit var confirmBtn: TextView
    lateinit var mCallback: ConfirmToChatLognListener

    interface ConfirmToChatLognListener {

        fun confirmToChatLogListener(selectedUserList:ArrayList<User>,chatLog : Util.ChatLog)
    }

    companion object {
        val TAG = "NewMessage Frafment"
        fun newInstance(): NewMessageFragment {
            var args = Bundle()
            var fragment = NewMessageFragment()
            fragment.setArguments(args)
            return fragment
        }

        var currentUserUid = Util.getCurrentUserUid()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_new_message, container, false)
        recyclerview_newmessage = view.findViewById(R.id.recyclerview_newmessage)
        confirmBtn = view.findViewById(R.id.ic_confirm_chat_list)
        cancleBtn = view.findViewById(R.id.ic_cancle_chat_list)

        var manager = LinearLayoutManager(this.context)
        manager.orientation = LinearLayoutManager.VERTICAL

        var adapter=  GroupAdapter<GroupieViewHolder>()
//        adapter.add(UserItem())

        var res :ArrayList<ChooseUse> = arrayListOf()

        var selectectedContact :ArrayList<User> = arrayListOf()

        fetchUsers(adapter,inflater,container!!,res,selectectedContact)

        recyclerview_newmessage.setLayoutManager(manager);

        recyclerview_newmessage.adapter = adapter

        Util.fetchCurrentUser2 { currentUser ->
            confirmBtn.setOnClickListener {
                var chatLog = Util.ChatLog("","")
                if(selectectedContact.size > 1){
                    chatLog = createGroupData(selectectedContact,currentUser.username)

                } else {
                    chatLog.chatLogId = getTwoPersonChatId(currentUserUid, selectectedContact.get(0).uid)
                    chatLog.chatLogHeader = selectectedContact.get(0).username
//                    CheckSingleData(chatLog.chatLogId){
//                        if (!it.contains(chatLog.chatLogId)){
//                            createSingleData(chatLog.chatLogId,selectectedContact.get(0).uid,currentUserUid)
//                        }
//                    }
                }

                mCallback.confirmToChatLogListener(selectectedContact,chatLog!!)

            }
        }


        return view
    }

    fun CheckSingleData(chatLogId : String, chatLogIdSet: (Set<String>) -> Unit = {


        val chatIdRef = FirebaseDatabase.getInstance().getReference("${Util.LISTS}/${currentUserUid}")

        chatIdRef.orderByChild("groupOrTwoPersonChatId").equalTo(chatLogId).addChildEventListener(object:
            ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val latestMessage = dataSnapshot.getValue(ListLatestMessage::class.java)
                println(dataSnapshot.getKey() + " was " + latestMessage!!.chatLog.chatLogId + " has create the single chat.")
                it.plus(latestMessage.chatLog.chatLogId)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }){

    }

    private fun searchAllSingleGroupId(toUserId : String){


        val twoPersonChatId = getTwoPersonChatId(currentUserUid, toUserId)

        var chatLogIdSet = setOf<String>()

        val chatIdRef = FirebaseDatabase.getInstance().getReference("${Util.LISTS}/${currentUserUid}")

        chatIdRef.orderByChild("groupOrTwoPersonChatId").equalTo(twoPersonChatId).addChildEventListener(object:
            ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

                val latestMessage = dataSnapshot.getValue(ListLatestMessage::class.java)
                println(dataSnapshot.getKey() + " was " + latestMessage!!.chatLog.chatLogId + " has create the single chat.")
                chatLogIdSet.plus(latestMessage.chatLog.chatLogId)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }

    private fun createSingleData(twoPersonChatId : String,toUserId: String,froUserId: String, selectectedContact:ArrayList<User>){
        val reference = FirebaseDatabase.getInstance().getReference("/${Util.TWOPERSONCHATS}/$twoPersonChatId")
        var memberList = arrayListOf<String>()
        memberList.add(toUserId)
        memberList.add(froUserId)

        val twoPersonChat = TwoPersonChat(twoPersonChatId,memberList)
        reference.setValue(twoPersonChat)
            .addOnSuccessListener {
                Log.d(TAG, "Saved the twoPersonChat: ${reference.key}")
            }

        val referenceCreater = FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/${currentUserUid}/${twoPersonChatId}")
        Util.fetchUser(toUserId){toUser ->
            val listLatestMessage = ListLatestMessage(Util.ChatLog(twoPersonChatId, toUser.username),selectectedContact,
                toUser.profileImageUrl,"",true,
                Util.getTimestamp()
            )
            referenceCreater.setValue(listLatestMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved the group on the creater list,twoPersonChatId: ${twoPersonChatId}")
                }
        }

    }

    private fun getTwoPersonChatId(currentUser: String, toUserId: String): String {
        val curArray = currentUser.toCharArray()
        val toUserArray = toUserId.toCharArray()
        if (curArray.size < toUserArray.size){
            return "$currentUser$toUserId"
        } else if (curArray.size > toUserArray.size){
            return "$toUserId$currentUser"
        }else {
            var index = 0
            curArray.forEach {
                if (it < toUserArray[index]){
                    return "$currentUser$toUserId"
                } else if(it > toUserArray[index]) {
                    return "$toUserId$currentUser"
                }
                index++


            }
        }
        return ""
    }

    private fun createGroupData(selctectedContact:ArrayList<User>, currentUserName: String): Util.ChatLog {
        var chatLog = Util.ChatLog()
        chatLog.chatLogHeader = getLogChatHead(selctectedContact,currentUserName)
        val reference = FirebaseDatabase.getInstance().getReference("/${Util.GROUPCHATS}").push()
        val groups = GroupsChat(reference.key!!,currentUserUid, getGroupMember(selctectedContact))
        reference.setValue(groups)
            .addOnSuccessListener {
                Log.d(TAG, "Saved the Group: ${reference.key}")
            }
        val referenceCreater = FirebaseDatabase.getInstance().getReference("/${Util.LISTS}/${currentUserUid}/${reference.key!!}")

        val listLatestMessage = ListLatestMessage(Util.ChatLog(reference.key!!,chatLog.chatLogHeader),selctectedContact,"","",true,
            Util.getTimestamp()
        )

        referenceCreater.setValue(listLatestMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved the group on the creater list: ${referenceCreater.key}")
            }
        chatLog.chatLogId = reference.key!!


        return chatLog
    }

    private fun getGroupMember(selectectedContact: ArrayList<User>): ArrayList<String> {
        var  iterator = selectectedContact.iterator()
        val result : ArrayList<String> = arrayListOf()
        iterator.forEach{
            result.add(it.uid)
        }
        return result
    }


    private fun fetchusersAdapter(adapter: GroupAdapter<GroupieViewHolder>,inflater: LayoutInflater,container: ViewGroup?,res:ArrayList<ChooseUse>,selectectedContact:ArrayList<User>) {
         res.forEach{
             Log.d("NewMessage", "each user name: " + it.username)
             if (container!= null){
                 adapter.add(UserItem(it))
             }
         }

        adapter.setOnItemClickListener { item, view ->

            var userItem = item as UserItem
            Log.d(TAG,"choose contacts:${userItem.user.selected}")
            val view = inflater.inflate(R.layout.user_row_new_message, container, false)
            selectedBtn = view.findViewById(R.id.ic_radio_button_choose_contacts_new_Message)

            var  iterator = selectectedContact.iterator()

            if (userItem.user.selected){
                userItem.user.selected = false
                if(selectectedContact!= null){
                    iterator.forEach {
                        if ( it.uid.equals(userItem.user.uid)){
                            iterator.remove()
                        }
                    }
                    if (!iterator.hasNext()) {
                        confirmBtn.background = getResources().getDrawable(
                            R.drawable.button_unclickable)
                        confirmBtn.text = getString(R.string.confirm)
                        confirmBtn.isClickable = false
                    } else {
                        confirmBtn.isClickable = true
                        confirmBtn.text = "${getString(R.string.confirm)} (${selectectedContact.size})"
                    }
                    Log.d(TAG,"selectedContacts length = ${selectectedContact.size}")
                }

            } else {
                userItem.user.selected = true
                confirmBtn.background = getResources().getDrawable(
                    R.drawable.button_clickable)
                selectectedContact.add(User(userItem.user.uid,userItem.user.username,userItem.user.email,userItem.user.profileImageUrl))
                confirmBtn.text = "${getString(R.string.confirm)} (${selectectedContact.size})"
                Log.d(TAG,"selectedContacts length = ${selectectedContact.size}")
            }

            recyclerview_newmessage.adapter = adapter
        }

    }


    private fun fetchUsers(adapter: GroupAdapter<GroupieViewHolder>, inflater: LayoutInflater, container: ViewGroup,res : ArrayList<ChooseUse>,selectectedContact:ArrayList<User>) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != currentUserUid) {
                           res.add(ChooseUse(user.uid,user.username,user.email, user.profileImageUrl,false))
                    }
                }

                fetchusersAdapter(adapter,inflater,container,res,selectectedContact)

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            //mCallback initialize
            mCallback = context as ConfirmToChatLognListener
        } catch (e: ClassCastException) {
            //
        }
    }
}

class UserItem(val user: ChooseUse): Item<GroupieViewHolder>() {
//    lateinit var selectedButton:ImageView
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.username_textview_new_message.text = user.username
        if (user.profileImageUrl != ""){
            Picasso.get().load(user.profileImageUrl).placeholder(R.drawable.unnamed).into(viewHolder.itemView.imageview_new_message)
        } else {
            Picasso.get().load(R.drawable.unnamed).into(viewHolder.itemView.imageview_new_message)
        }

        if (user.selected) {
            viewHolder.itemView.ic_radio_button_choose_contacts_new_Message.setImageResource(R.drawable.ic_radio_button_checked)
        }else {
            viewHolder.itemView.ic_radio_button_choose_contacts_new_Message.setImageResource(R.drawable.ic_radio_button_unchecked)
        }

    }

}


