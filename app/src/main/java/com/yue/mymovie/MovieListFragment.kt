package com.yue.mymovie


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yue.mymovie.Util.Companion.getdate
import org.jetbrains.anko.doAsync
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MovieListFragment : Fragment() {
    lateinit var mry: RecyclerView

    lateinit var mCallback: RecyclerViewAdapter.OnItemSelectListener




    companion object {
        val TAG = "MovieListFragment"
        fun newInstance():MovieListFragment{
            val  fragment = MovieListFragment()
            val args = Bundle()
            fragment.setArguments(args)
//            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        var view = inflater.inflate(R.layout.fragment_movie_list, container, false)

        val contentType=getString(R.string.Content_Type)
//        val primaryReleaseDateGte = "2020-01-15"
//        val primaryReleaseDateLte = "2020-02-22"
        val primaryReleaseDateGte = getdate(TAG,-15)
        val primaryReleaseDateLte = getdate(TAG,15)

        val api_key = getString(R.string.glu_KEY)
        val authorization =  getString(R.string.authorization)
        val language = getString(R.string.language)
        val imaFrontPAth = getString(R.string.img_front_path)
        val n = getString(R.string.page)

        mry = view.findViewById(R.id.recycleview_movie)
        mry.layoutManager = GridLayoutManager(this.context,3)

        doAsync{
            try{
                val movieGluManagerComingSoon = MovieGluManagerComingSoon()
                val moviesList = movieGluManagerComingSoon.retrieveMovie(
                    contentType,
                    primaryReleaseDateGte,
                    primaryReleaseDateLte,
                    api_key,
                    authorization,
                    language,
                    imaFrontPAth,
                    n,
                    context!!)

                activity?.runOnUiThread{
                    val myAdapter:RecyclerViewAdapter = RecyclerViewAdapter(context!!,moviesList,mCallback)
                    mry.adapter = myAdapter
//                    mry.adapter = RecyclerViewAdapter(context!!,moviesList,mCallback)
                }

            }catch (e: Exception){
                activity?.runOnUiThread {
                    e.printStackTrace()
                    // Display some kind of error message
                    Toast.makeText(
                        this@MovieListFragment.activity,
                        " No Reviews!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            mCallback = context as RecyclerViewAdapter.OnItemSelectListener
        } catch (e: ClassCastException) {
            //
        }
    }



}
