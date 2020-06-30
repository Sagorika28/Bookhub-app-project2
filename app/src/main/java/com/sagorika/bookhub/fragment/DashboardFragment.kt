package com.sagorika.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sagorika.bookhub.R
import com.sagorika.bookhub.adapter.DashboardRecyclerAdapter
import com.sagorika.bookhub.model.Book
import com.sagorika.bookhub.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    //   lateinit var btnCheckInternet: Button
    lateinit var recyclerAdaper: DashboardRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    val bookInfoList = arrayListOf<Book>()

    //variable for comparing books to sort
    var ratingComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookRating.compareTo(book2.bookRating, true) == 0) {
            book1.bookName.compareTo(book2.bookName, true)
        } else {
            book1.bookRating.compareTo(book2.bookRating, true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        //telling the compiler that this fragment has a menu
        setHasOptionsMenu(true)


        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        //    btnCheckInternet = view.findViewById(R.id.btnCheckInternet)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        //to show the progress bar when fragment is being loaded
        progressLayout.visibility = View.VISIBLE

        /*
        This button was added to check whether internet connectivity of device has been established or not
        btnCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)) {
                //Internet available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("OK") { text, listener ->
                    //nothing
                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                    //nothing
                }
                dialog.create()
                dialog.show()

            } else {
                //Internet not available
                val dialog = AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("OK") { text, listener ->
                    //nothing
                }
                dialog.setNegativeButton("Cancel") { text, listener ->
                    //nothing
                }
                dialog.create()
                dialog.show()
            }
        }*/


        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {
                    // try block for JSON exception
                    try {

                        //to hide the progress layout when data has been loaded
                        progressLayout.visibility = View.GONE

                        val success = it.getBoolean("success")
                        if (success) {
                            val data = it.getJSONArray("data")

                            //extracting JSONObjects from JSONArray
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)

                                ////sending the bookInfoList to adapter
                                recyclerAdaper =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                //initialise adapter and layoutManager and attach them to their resp. files
                                recyclerDashboard.adapter = recyclerAdaper
                                recyclerDashboard.layoutManager = layoutManager

                                /*
                                for adding dividers

                                recyclerDashboard.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerDashboard.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )*/
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred :( ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    //to make sure the app doesn't crash when we try to open fav without waiting for dashboard to load
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred ! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "abb428ebfb485a"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        } else {

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //store id of the clicked item
        var id = item?.itemId

        //check which item id is clicked by comparing the item id clicked to the item id of menu options
        if (id == R.id.action_sort) {

            //sort the books (increasing order)
            Collections.sort(bookInfoList, ratingComparator)

            //rearranging the lists in the descending order
            bookInfoList.reverse()

        }

        //notify the adapter about the changes made to reflect them on the screen
        recyclerAdaper.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }
}