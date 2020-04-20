package com.gmail2548sov.photogallery

import android.os.HandlerThread
import android.util.Log

class ImageDownloader<T>: HandlerThread(TAG) {

    private var mHasQuit = false
    override fun quit(): Boolean {
        mHasQuit = true
        return super.quit()
    }

    fun queueImage(target:T, url:String) {
        Log.i (TAG, "Got a URL: $url")
    }


    companion object {
        private const val TAG = " "
    }
}