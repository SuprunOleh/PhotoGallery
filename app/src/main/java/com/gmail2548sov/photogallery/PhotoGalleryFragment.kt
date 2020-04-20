package com.gmail2548sov.photogallery

import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_photo_gallery.view.*
import kotlinx.android.synthetic.main.item_photo.view.*

class PhotoGalleryFragment : Fragment() {

    lateinit var mPhotoRecyclerView: RecyclerView
    var mGalleryItems: List<GalleryItem> = ArrayList()

    lateinit var mImageDownloader: ImageDownloader<PhotoHolder>


    companion object {
        fun newInstance(): PhotoGalleryFragment {
            return PhotoGalleryFragment()



        }
        private const val TAG = "PhotoGalleryFragment"



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        mPhotoRecyclerView = view.photo_recycler_view

        mPhotoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        setupAdapter()
        Log.d ("c222", "111")

        return view
    }

    fun setupAdapter() {

        if (isAdded) {
            mPhotoRecyclerView.adapter = PhotoAdapter(mGalleryItems)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        FetchItemsTask().execute()

        mImageDownloader = ImageDownloader()
        mImageDownloader.start()
        mImageDownloader.looper
        Log.i (TAG, "Background thread started")
    }

    override fun onDestroy() {
        super.onDestroy()
        mImageDownloader.quit()
        Log.i (TAG, "Background thread destroyed")
    }


    inner class FetchItemsTask : AsyncTask<Void, Void, List<GalleryItem>>() {
        override fun doInBackground(vararg params: Void?): List<GalleryItem> {
            return FlickrFetchr().fetchItems()
        }

        override fun onPostExecute(items: List<GalleryItem>) {
            mGalleryItems = items
            setupAdapter()
        }
    }

    inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {

            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.item_photo, parent, false)
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int {
            return mGalleryItems.size
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            var drawable_foto = resources.getDrawable(R.drawable.foto)
            holder.bindGalleryItem(drawable_foto)
            mImageDownloader.queueImage(holder, mGalleryItems.get(position).mUrl)

        }

    }

    inner class PhotoHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView = view.item_image_view as ImageView

        fun bindGalleryItem(drawable: Drawable) {
            imageView.setImageDrawable(drawable)

        }

    }

}