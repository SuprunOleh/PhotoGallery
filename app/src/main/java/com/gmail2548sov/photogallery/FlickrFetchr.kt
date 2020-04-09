package com.gmail2548sov.photogallery

import android.net.Uri
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class FlickrFetchr {

    companion object{
        private const val TAG = "FlickrFetchr"
        private const val API_KEY = "f4a0467a3b9bcf3c382a24420571bf42"
    }


    @Throws(IOException::class)
    fun getUrlBytes(urlSpec: String): ByteArray? {
        val url = URL(urlSpec)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        return try {
            val out = ByteArrayOutputStream()
            val `in` = connection.inputStream
            if (connection.getResponseCode() !== HttpURLConnection.HTTP_OK) {
                throw IOException(
                    connection.getResponseMessage().toString() +
                            ": with " +
                            urlSpec
                )
            }
            var bytesRead = 0
            val buffer = ByteArray(1024)
            while (`in`.read(buffer).also({ bytesRead = it }) > 0) {
                out.write(buffer, 0, bytesRead)
            }
            out.close()
            out.toByteArray()
        } finally {
            connection.disconnect()
        }
    }

    @Throws(IOException::class)
    fun getUrlString(urlSpec: String): String? {
        return String(getUrlBytes(urlSpec)!!)
    }

    fun fetchItems(): List<GalleryItem> {

        val items: ArrayList<GalleryItem> = ArrayList()
        try {
            val url: String = Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("method", "flickr.photos.getRecent")
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build().toString()
            val jsonString = getUrlString(url)
            Log.i(TAG, "Received JSON: $jsonString")

            val jsonBody = JSONObject(jsonString)
            parseItems(items, jsonBody)

        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch items", ioe)
        } catch (je: JSONException) {
            Log.e (TAG, "Failed to parse JSON", je)
        }
        return items
    }


    @Throws(IOException::class, JSONException::class)
    private fun parseItems(
        items: MutableList<GalleryItem>,
        jsonBody: JSONObject
    ) {
        val photosJsonObject = jsonBody.getJSONObject("photos")
        val photoJsonArray = photosJsonObject.getJSONArray("photo")
        for (i in 0 until photoJsonArray.length()) {
            val photoJsonObject = photoJsonArray.getJSONObject(i)

            val caption = photoJsonObject.getString("title")
            val id = photoJsonObject.getString("id")
            Log.d ("sov1", id+", " + i)
            if (!photoJsonObject.has("url_s")) {
                continue
            }
            val urls = photoJsonObject.getString("url_s")
            Log.d ("sov1", urls+", " + i)
            val item = GalleryItem(caption,id,urls)
            items.add(item)
        }
    }


}