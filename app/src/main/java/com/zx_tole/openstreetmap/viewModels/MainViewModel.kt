package com.zx_tole.openstreetmap.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class MainViewModel: ViewModel() {
    fun loadJSONFromAsset(context: Context, assetName: String): String? {
        val json: String? = try {
            val inputStream:InputStream  = context.assets.open(assetName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            Timber.e(ex)
            return null
        }

        json?.let {
            val obj = JSONObject(json)
        }

        val gson = Gson()
        

        return json
    }
}