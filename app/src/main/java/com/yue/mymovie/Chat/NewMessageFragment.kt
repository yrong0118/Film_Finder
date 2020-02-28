package com.yue.mymovie.Chat


import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.yue.mymovie.LoginOrRegister.User
import com.yue.mymovie.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


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

        fun confirmToChatLogListener(list:ArrayList<User>)
    }

    companion object {

        val TAG = "NewMessage Frafment"
        fun newInstance(): NewMessageFragment {
            var args = Bundle()
            var fragment = NewMessageFragment()
            fragment.setArguments(args)
            return fragment
        }
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
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
        var res :ArrayList<ChooseUse> = arrayListOf()
        var selctectedContact :ArrayList<User> = arrayListOf()

        fetchUsers(adapter,inflater,container!!,res,selctectedContact)

        recyclerview_newmessage.setLayoutManager(manager);

        recyclerview_newmessage.adapter = adapter

        confirmBtn.setOnClickListener {
            mCallback.confirmToChatLogListener(selctectedContact)
        }
        return view
    }

    private fun fetchusersAdapter(adapter: GroupAdapter<GroupieViewHolder>,inflater: LayoutInflater,container: ViewGroup?,res:ArrayList<ChooseUse>,selectectedContact:ArrayList<User>) {
         res.forEach{
             Log.d("NewMessage", it.toString())
             if (container!= null){
                 adapter.add(UserItem(it,inflater,container!!))
             }
         }

        adapter.setOnItemClickListener { item, view ->

            var userItem = item as UserItem
            Log.d(TAG,"choose contacts:${userItem.user.selected}")
            val view = inflater.inflate(com.yue.mymovie.R.layout.user_row_new_message, container, false)
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
        val toId = FirebaseAuth.getInstance().uid
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid != toId) {
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

class UserItem(val user: ChooseUse, val inflater: LayoutInflater, val container: ViewGroup): Item<GroupieViewHolder>() {
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


