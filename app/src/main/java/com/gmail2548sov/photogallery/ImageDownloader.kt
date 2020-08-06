package com.gmail2548sov.photogallery

import android.R.attr
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


class ImageDownloader<T> (private val mResponseHandler: Handler) : HandlerThread(TAG) {

    companion object {
        private final val MESSAGE_DOWNLOAD: Int = 0
        private const val TAG = "ImageDownloader"
    }

    private var mHasQuit = false
    private lateinit var mRequestHandler: Handler
    //private lateinit var mResponseHandler: Handler
    private val mRequestMap: ConcurrentMap<T, String> = ConcurrentHashMap()
    private lateinit var mImageDownloadListener: ImageDownloadListener<T>

    interface ImageDownloadListener<T> {
        fun onImageDownloaded(target:T, image: Bitmap)
    }
     fun setImageDownloadListener(listener: ImageDownloadListener<T>) {
         mImageDownloadListener = listener
     }



    override fun quit(): Boolean {
        mHasQuit = true
        return super.quit()
    }

    fun queueImage(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
        if (url == null) {
            mRequestMap.remove(target)
        } else {
            mRequestMap.put(target, url)
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget()
        }
    }


    override fun onLooperPrepared() {
        mRequestHandler = object: Handler(){
            override fun handleMessage(msg: Message) {
                if (msg.what== MESSAGE_DOWNLOAD) {
                    val target:T = msg.obj as T
                    Log.i(TAG, "Got a request of URL: ${mRequestMap.get(target)}")
                    handleRequest(target)

                }
            }
        }
    }

    fun handleRequest(target: T) {
        try {
            val url: String = mRequestMap.get(target)?:return
            Log.i("qqq",url.toString())
            val bitmapBytes: ByteArray? = FlickrFetchr().getUrlBytes(url)
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0, bitmapBytes!!.size)
            Log.i(TAG, "Bitmap created")

            mResponseHandler.post(Runnable {
                if (mRequestMap[target] !== url ||
                    mHasQuit
                ) {
                    return@Runnable
                }
                mRequestMap.remove(target)
                mImageDownloadListener.onImageDownloaded(
                    target,bitmap)
            })


        } catch (ioe: IOException) {
            Log.e (TAG, "Error download image", ioe)
        }

    }


    fun clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD)
        mRequestMap.clear()
    }

}