package top.yukonga.hq_icon.utils

import android.net.Uri.encode
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request
import java.io.IOException
import javax.net.ssl.SSLHandshakeException

class Search {
    suspend fun search(term: String, country: String, entity: String, limit: Int): String = withContext(Dispatchers.IO) {
        val url = "https://itunes.apple.com/search?term=${encode(term)}&country=$country&entity=$entity&limit=$limit"
        val request = Request.Builder().url(url).build()

        try {
            Utils().client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    return@withContext response.body?.string() ?: ""
                } else {
                    Log.e("Search", "Search request failed: ${response.code} ${response.message}")
                    return@withContext ""
                }
            }
        } catch (e: SSLHandshakeException) {
            Log.e("Search", "SSL Handshake failed for $url", e)
            return@withContext ""
        } catch (e: IOException) {
            Log.e("Search", "Network IO error for $url", e)
            return@withContext ""
        } catch (e: Exception) {
            Log.e("Search", "Unexpected error during search for $url", e)
            return@withContext ""
        }
    }
}