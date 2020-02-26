package com.yue.mymovie

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class RecyclerViewAdapter(val mContext: Context, val mData:List<Movie>,val mCallback:OnItemSelectListener): RecyclerView.Adapter<RecyclerViewAdapter.Factory.MyViewHolde>(){


//    var mCallback: RecyclerViewAdapter.OnItemSelectListener? = null

    interface OnItemSelectListener {
        fun onItemSelected(selectedMovie: Movie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolde {
        var mInflater : LayoutInflater = LayoutInflater.from(parent.context)
        var view: View = mInflater.inflate(R.layout.card_view_movie_item,parent,false)
        return MyViewHolde(view)
    }

    override fun onBindViewHolder(holder: MyViewHolde, position: Int) {
        val currentMovies = mData[position]
        if (!Util.isStringEmpty(currentMovies.title)){
            holder.movieTitle.setText(mData.get(position).title)
//            if (!Util.isStringEmpty((currentMovies.thumbnail) as String))
//            holder.movieImg.setImageResource(R.drawable.no_images_available)

            Picasso
                .get()
                .load(mData.get(position).imageUrl)
                .into(holder.movieImg)
        }

        holder.itemView.setOnClickListener { v: View? ->

            mCallback.onItemSelected(mData.get(position))
//            holder.movieImg.setImageResource(R.drawable.no_images_available)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    companion object Factory{
        class MyViewHolde(itemView: View): RecyclerView.ViewHolder(itemView) {
            val movieTitle: TextView
            val movieImg: ImageView
            val cardView: CardView
            init {
                movieTitle = itemView.findViewById(R.id.movie_title) as TextView
                movieImg = itemView.findViewById(R.id.movie_img) as ImageView
                cardView = itemView.findViewById(R.id.cardView_movie) as CardView
            }
        }
    }

//    companion object Factory{
//        class MyViewHolde(itemView: View): RecyclerView.ViewHolder(itemView) {
//            lateinit var movieTitle: TextView
//            lateinit var movieImg: ImageView
//            fun MyViewHolde(itemView:View){
//
//                movieTitle = itemView.findViewById(R.id.movie_title) as TextView
//                movieImg = itemView.findViewById(R.id.movie_img) as ImageView
//            }
//        }
//    }


//    class MyViewHolde(itemView: View): RecyclerView.ViewHolder(itemView) {
//        lateinit var movieTitle: TextView
//        lateinit var movieImg: ImageView
//        companion object Factory{
//
//            fun MyViewHolde(itemView:View){
//                movieTitle = itemView.findViewById(R.id.movie_title) as TextView
//                movieImg = itemView.findViewById(R.id.movie_img) as ImageView
//            }
//
//        }
//    }

//    class MyViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        lateinit var movieTitle: TextView
//        lateinit var movieImg: ImageView
//
//        constructor() : this() {
//
//            movieTitle = itemView.findViewById(R.id.movie_title) as TextView
//            movieImg = itemView.findViewById(R.id.movie_img) as ImageView
//        }
//
//    }
}