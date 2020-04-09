package com.gmail2548sov.photogallery

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_photo_gallery.view.*
import kotlinx.android.synthetic.main.item_photo.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class PhotoGalleryFragment : Fragment() {

    lateinit var mPhotoRecyclerView: RecyclerView
    lateinit var mGalleryItems: List<GalleryItem>


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


        mPhotoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        //setupAdapter()


        return view
    }

    fun setupAdapter(){

        if (isAdded()) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mGalleryItems)
        }
        }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()
    }


    inner class FetchItemsTask: AsyncTask<Void, Void, List<GalleryItem>>() {
        override fun doInBackground(vararg params: Void?): List<GalleryItem> {
            return FlickrFetchr().fetchItems()
        }

        override fun onPostExecute(items: List<GalleryItem>) {
            mGalleryItems = items
            setupAdapter()

        }
    }




    inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val textView = TextView(activity)
            return PhotoHolder(textView)
        }

        override fun getItemCount(): Int {
            return mGalleryItems.size
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            holder.bindGalleryItem(mGalleryItems[position])


        }


    }

    inner class PhotoHolder(val view: View) : RecyclerView.ViewHolder(view)
    {
        val mtextview = view as TextView

        fun bindGalleryItem(item: GalleryItem) {
            mtextview.text = item.toString()

        }

    }






}