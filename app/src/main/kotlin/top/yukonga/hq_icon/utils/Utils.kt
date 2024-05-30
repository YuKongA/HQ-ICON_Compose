package top.yukonga.hq_icon.utils

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient

class Utils {

    val client = OkHttpClient()

    val json = Json { ignoreUnknownKeys = true }
}