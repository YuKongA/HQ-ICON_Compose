package top.yukonga.hqicon.utils

import android.net.Uri.encode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class Search {
    private val client = OkHttpClient()

    suspend fun search(term: String, country: String, entity: String, limit: Int): String = withContext(Dispatchers.IO) {
        val url = "https://itunes.apple.com/search?term=${encode(term)}&country=$country&entity=$entity&limit=$limit"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            return@withContext response.body?.string() ?: ""
        }
    }
}