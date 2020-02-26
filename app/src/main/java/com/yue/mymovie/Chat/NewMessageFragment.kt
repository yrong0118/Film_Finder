package com.yue.mymovie.Chat


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import kotlinx.android.synthetic.main.fragment_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

/**
 * A simple [Fragment] subclass.
 */
class NewMessageFragment : Fragment() {

    lateinit var recyclerview_newmessage : RecyclerView

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
        var manager = LinearLayoutManager(this.context)
        manager.orientation = LinearLayoutManager.VERTICAL

        var adapter=  GroupAdapter<GroupieViewHolder>()

//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())

        recyclerview_newmessage.setLayoutManager(manager);
        recyclerview_newmessage.adapter = adapter

        fetchUsers(adapter)

        return view
    }

    private fun fetchUsers(adapter: GroupAdapter<GroupieViewHolder>) {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d(TAG, it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }

                adapter.setOnItemClickListener { item, view ->

//                    val userItem = item as UserItem
//                    var view = this.inflater.inflate(R.layout.fragment_new_message, container, false)
//                    val intent = Intent(view.context, ChatLogActivity::class.java)
////          intent.putExtra(USER_KEY,  userItem.user.username)
//                    intent.putExtra(USER_KEY, userItem.user)
//                    startActivity(intent)
//
//                    finish()

                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username
        if (user.profileImageUrl != ""){
            Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageview_new_message)
        } else {
            Picasso.get().load(R.drawable.unnamed).into(viewHolder.itemView.imageview_new_message)
        }

    }

}
