package com.gmail2548sov.photogallery

import android.graphics.BitmapFactory
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap


class ImageDownloader<T> : HandlerThread(TAG) {

    companion object {
        private final val MESSAGE_DOWNLOAD: Int = 0
        private const val TAG = "ImageDownloader"
    }

    private var mHasQuit = false
    private lateinit var mRequestHandler: Handler
    private val mRequestMap: ConcurrentMap<T, String> = ConcurrentHashMap()


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
            val bitmapBytes: ByteArray? = FlickrFetchr().getUrlBytes(url)
            val bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0, bitmapBytes!!.size)
            Log.i(TAG, "Bitmap created")
        } catch (ioe: IOException) {
            Log.e (TAG, "Error download image", ioe)
        }

    }






}