package coffee.app.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class PhotoManager(private val context: Context) {
    
    private val photosDir: File
        get() = File(context.filesDir, "photos").also { it.mkdirs() }
    
    fun savePhoto(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            val compressed = compress(bitmap)
            val filename = "${UUID.randomUUID()}.jpg"
            val file = File(photosDir, filename)
            FileOutputStream(file).use { out ->
                compressed.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
    
    fun loadPhoto(path: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            null
        }
    }
    
    fun deletePhoto(path: String) {
        try {
            File(path).delete()
        } catch (_: Exception) { }
    }
    
    private fun compress(bitmap: Bitmap): Bitmap {
        val maxSize = 1920
        val (width, height) = if (bitmap.width > bitmap.height) {
            maxSize to (bitmap.height * maxSize / bitmap.width)
        } else {
            (bitmap.width * maxSize / bitmap.height) to maxSize
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}