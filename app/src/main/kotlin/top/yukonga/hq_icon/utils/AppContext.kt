package top.yukonga.hq_icon.utils

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object AppContext {
    var context: Context? = null

    fun init(context: Context) {
        this.context = context.applicationContext
    }
}