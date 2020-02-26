package com.yue.mymovie


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * A simple [Fragment] subclass.
 */
class SavedMoveFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved_move, container, false)
    }

    companion object {
        fun newInstance():SavedMoveFragment{
            val  fragment = SavedMoveFragment()
            val arg = Bundle()
            fragment.arguments = arg
            return fragment
        }
    }


}
