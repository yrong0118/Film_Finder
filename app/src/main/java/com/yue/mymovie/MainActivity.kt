package com.yue.mymovie

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.yue.mymovie.Chat.ChatActivity
import com.yue.mymovie.Chat.ShowVoteMoveListFragment
import com.yue.mymovie.LoginOrRegister.LoginOrRegisterActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.px2sp

class MainActivity : AppCompatActivity(), RecyclerViewAdapter.OnItemSelectListener,
    MovieDetailsFragment.MovieDetailToList, MovieDetailsFragment.MovieReview {
    override fun movieReview(movieId: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        val content_url = Uri.parse("https://www.themoviedb.org/movie/${movieId}")
        intent.data = content_url
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    lateinit var movieListFragment:MovieListFragment
    lateinit var savedMoveFragment:SavedMoveFragment
    lateinit var bottomBar : BottomNavigationView


//    lateinit var fragmentManager: FragmentManager
//    lateinit var fragmentTransaction: FragmentTransaction

    //https://blog.csdn.net/double2hao/article/details/50983820

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val path = intent.getStringExtra(Util.fromPath)
        val selectedMovieId = intent.getStringExtra("movie_id")

        //verifyUserIsLoggedIn()

        movieListFragment = MovieListFragment.newInstance()
        savedMoveFragment = SavedMoveFragment.newInstance()

        bottomBar = findViewById(R.id.bottomBar)


        var movieListFragment = MovieListFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.container, movieListFragment).commit()





        bottomBar.setOnNavigationItemSelectedListener { item->
            when(item.itemId){
                R.id.action_find ->{
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container,movieListFragment)
                        .addToBackStack(movieListFragment.toString())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.action_save ->{
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container,savedMoveFragment)
                        .addToBackStack(savedInstanceState.toString())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.action_chat_vote ->{
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }

            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, LoginOrRegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onItemSelected(selectedMovie: Movie) {
//        lateinit var movieDetailsFragment:MovieDetailsFragment
        var movieDetailsFragment=MovieDetailsFragment.newInstance()
        val bundle = Bundle()
        bundle.putString("movie_id", selectedMovie.id)//这里的values就是我们要传的值
        bundle.putString(Util.fromPath,MovieListFragment.TAG)
        movieDetailsFragment.setArguments(bundle)


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,movieDetailsFragment)
//            .addToBackStack(movieDetailsFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    override fun movieDetailToList() {
        var movieListFragment = MovieListFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container,movieListFragment)
//            .addToBackStack(showSearchMovieListFragmentFragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}


//bottomBar = findViewById(R.id.bottom_navigation)
//bottomBar.setOnNavigationItemSelectedListener { item ->
//    when
//
//    true