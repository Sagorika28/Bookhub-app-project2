package com.sagorika.bookhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.sagorika.bookhub.R
import com.sagorika.bookhub.adapter.FavouriteRecyclerAdapter
import com.sagorika.bookhub.database.BookDatabase
import com.sagorika.bookhub.database.BookEntity

class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter
    var dbBookList = listOf<BookEntity>()

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        //layout manager as GridLayoutManager
        layoutManager = GridLayoutManager(activity as Context, 2)

        //retrieving the fav books from DB and storing it in dbBookList
        dbBookList = RetrieveFavourites(activity as Context).execute().get()

        //checking whether dbBookList and hosting activity are not null then initialising adapter & displaying the recycler view
        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, dbBookList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        return view
    }

    //create AsyncTask class to retrieve list of fav books
    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<BookEntity>>() {

        override fun doInBackground(vararg params: Void?): List<BookEntity> {

            val db = Room.databaseBuilder(context, BookDatabase::class.java, "books-db").build()

            return db.bookDao().getAllBooks()

        }

    }

}