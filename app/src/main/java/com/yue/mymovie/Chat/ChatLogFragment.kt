package com.yue.mymovie.Chat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.yue.mymovie.R

/**
 * A simple [Fragment] subclass.
 */
class ChatLogFragment : Fragment() {

    companion object {

        val TAG = "NewMessage Frafment"
        fun newInstance(): ChatLogFragment {
            var args = Bundle()
            var fragment = ChatLogFragment()
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }


}
