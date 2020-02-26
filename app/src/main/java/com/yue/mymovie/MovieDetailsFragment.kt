package com.yue.mymovie


import android.content.ClipDescription
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.annotations.SerializedName
import com.squareup.picasso.Picasso
import com.yue.mymovie.retrofit.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_movie_details.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MovieDetailsFragment : Fragment() {
    lateinit var movieTitle: TextView
    lateinit var movieBGImg: ImageView
    lateinit var movieImg : ImageView
    lateinit var movieRating: TextView
    lateinit var movieFav:ImageView
    lateinit var star:ImageView
    lateinit var movieDescription: TextView
    lateinit var recyclerVdeioView:RecyclerView
    lateinit var mapBtn: Button
    lateinit var readMore: TextView
    lateinit var readLess: TextView


    companion object {
        fun newInstance(selectedMovieId : String): MovieDetailsFragment {
            var args = Bundle()
//            args.putSerializable(NEWS, news)
//            getData(selectedMovieId)

            var fragment = MovieDetailsFragment()
            fragment.setArguments(args)
            return fragment
        }

    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        fragmentManager = context as FragmentManager
//    }

//    override fun onStart() {
//        super.onStart()
//        var args:Bundle = arguments!!
//        setMovieTitle(args.getString("movie_id")!!)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View = inflater.inflate(R.layout.fragment_movie_details, container, false)
        val bundle = arguments
        val movieId = bundle!!.getString("movie_id")

        movieTitle = view.findViewById(R.id.movie_detail_title)
        movieBGImg = view.findViewById(R.id.movie_detail_background)
        movieImg = view.findViewById(R.id.movie_detail_img)
        movieRating = view.findViewById(R.id.movie_detail_rating)
        movieDescription = view.findViewById(R.id.movie_detail_description)
        mapBtn = view.findViewById(R.id.movie_detail_map)
        star = view.findViewById(R.id.star)
        star.setImageResource(R.drawable.ic_star)
        movieFav = view.findViewById(R.id.movie_liked)
        readMore = view.findViewById(R.id.read_more)
        readLess = view.findViewById(R.id.read_less)
//
        // Inflate the layout for this fragment

        recyclerVdeioView = view.findViewById(R.id.tailer_pad)
//        recyclerVdeioView.setHasFixedSize(true)



        getData(movieId!!)

        getVideoData(movieId!!,recyclerVdeioView)


        var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this.context,LinearLayoutManager.HORIZONTAL,false)
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL)
        recyclerVdeioView.setLayoutManager(linearLayoutManager)

//        if (movieDescription.lineCount <= 3){
//            readMore.visibility = View.INVISIBLE
//        }

        readMore.setOnClickListener{
            readMore.setVisibility(View.INVISIBLE)
            readLess.visibility = View.VISIBLE
            movieDescription.maxLines = Integer.MAX_VALUE
        }

        readLess.setOnClickListener{
            readMore.setVisibility(View.VISIBLE)
            readLess.visibility = View.INVISIBLE
            movieDescription.maxLines = 2
        }

        return view
    }



    fun setMovieTitle(title: String){
        var tv :TextView = activity!!.findViewById(R.id.movie_detail_title)
        tv.setText(title)
    }

    fun getData(selectedMovieId : String) {
        var movieDetailRequestApi = RetrofitClient.instance.create(MovieDetailRequestApi::class.java)
        val api = getString(R.string.glu_KEY)
        val language = getString(R.string.language)
        movieDetailRequestApi.getMovieDetailById(selectedMovieId,language,api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> detail(baseResponse) },
                { throwable ->
                    //*******************************
                    if (context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(this.context, "Movies API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }
    fun detail(response: BaseResponse){
        val filmId = response.filmId
        val filmName = response.filmName
        val filmImg= getString(R.string.img_front_path)+ response.filmImg
        val rating=response.rating
        val description=response.description
        val releaseDate = response.releaseDate

        Movie(filmName,filmId,filmImg,releaseDate,"",description,rating, emptyList())

        movieTitle.setText(filmName)

        Picasso
            .get()
            .load(filmImg)
            .into(movieBGImg)

        Picasso
            .get()
            .load(filmImg)
            .into(movieImg)

        movieRating.setText(rating)
        movieDescription.setText(description)

    }

    fun getVideoData(selectedMovieId : String,recyclerVdeioView: RecyclerView){
        val api = getString(R.string.glu_KEY)
        val language = getString(R.string.language)
        var movieVedioesRequestApi = RetrofitClient.instance.create(MovieVedioesRequestApi::class.java)
        movieVedioesRequestApi.getVedioesById(selectedMovieId,language,api)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter { baseResponse -> baseResponse != null }
            .subscribe(
                { baseResponse -> showTailerView(baseResponse,recyclerVdeioView) },
                { throwable ->
                    //*******************************
                    if (context == null) {
                        throw IllegalStateException("Fragment " + this + " not attached to a context.");
                    }
                    //*******************************
                    Toast.makeText(context, "Movies Video API Failed", Toast.LENGTH_SHORT).show()
                    throwable.printStackTrace()
                }
            )
    }
    fun showTailerView(vediosResponse: VediosResponse,recyclerVdeioView: RecyclerView){
        var video = Vector<Video>()
        if (vediosResponse != null && vediosResponse.results.size > 0) {
            for (i in 0 until vediosResponse.results.size){
                if (vediosResponse.results.get(i).filmVedioSite == "YouTube") {
                    var key = vediosResponse.results.get(i).filmVedioKey
                    var site = vediosResponse.results.get(i).filmVedioSite
                    var size = vediosResponse.results.get(i).filmVedioSize
                    var type = vediosResponse.results.get(i).filmVedioType
//                    var url = "<iframe src=\"https://www.youtube.com/embed/$key\" frameborder=\"0\"></iframe>"
                    var url = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/$key\" frameborder=\"0\" allowfullscreen></iframe>"
                    video.add(Video(url,site,size,type))
                }
            }
        }

        if (video.size > 0){
            var movieTailerAdapter = MovieTailerAdapter(video)

            recyclerVdeioView.setAdapter(movieTailerAdapter);
        }

    }

}

