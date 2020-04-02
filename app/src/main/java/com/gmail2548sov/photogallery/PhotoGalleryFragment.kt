package com.gmail2548sov.photogallery

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_photo_gallery.view.*
import java.io.IOException

class PhotoGalleryFragment : Fragment() {

    lateinit var mPhotoRecyclerView: RecyclerView


    companion object {
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()
        }

        val TAG: String = "PhotoGalleryFragment"

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = view.photo_recycler_view

        mPhotoRecyclerView.layoutManager = LinearLayoutManager(context)

        //mPhotoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()
    }


    inner class FetchItemsTask: AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            try {
                val result = FlickrFetchr()
                    .fetchItems()
                Log.i(TAG, "Fetched contents of URL: $result")
            } catch (ioe: IOException) {
                Log.e(TAG, "Failed to fetch URL: ", ioe)
            }
            return null


        }

    }


}