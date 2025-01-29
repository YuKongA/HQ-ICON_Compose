package top.yukonga.hq_icon.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.hq_icon.R
import java.io.File

class Download {
    suspend fun downloadImage(context: Context, url: String, fileName: String, resolution: String, corner: String) =
        withContext(Dispatchers.IO) {

            val realUrl = url.replace("512x512bb.jpg", "${resolution}x${resolution}bb.png")
            val cornerName = if (corner == "1") "Rounded" else "Original"
            val realFileName = fileName + "_" + resolution + "_" + cornerName + ".png"

            val bitmap = LoadIcon().loadIcon(realUrl)
            val finallyBitmap = LoadIcon().roundCorners(bitmap, corner)

            val downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDirectory, fileName + "_" + resolution + "_" + cornerName + ".png")

            if (file.exists()) file.delete()

            try {
                saveImageToStorage(context, finallyBitmap, realFileName)
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        context,
                        context.getString(R.string.download_successful) + ": Pictures/HQ ICON/$realFileName",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (_: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, context.getString(R.string.download_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }

    private suspend fun saveImageToStorage(context: Context, bitmap: Bitmap, filename: String) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/${context.getString(R.string.app_name)}"
            )
        }

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
        }
    }

}