package rocks.gorjan.gokixp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Log
import java.io.File

/**
 * The user's start-menu picture picked from the device's own photos.
 *
 * The picked image is cut to the largest centred square and shrunk to [SIZE_PX], so any photo
 * (portrait, landscape, huge or tiny) ends up the same size. The result is stored in the app's own
 * private folder; the original photo is never kept or changed.
 */
object UserAvatar {
    const val SIZE_PX = 256
    const val DIR = "user_pictures"

    /** Prefix that marks a picture stored in the app's own folder (as opposed to a bundled asset). */
    const val FILE_PREFIX = "file:"

    data class Crop(val x: Int, val y: Int, val side: Int)

    /** The largest centred square inside a [width] x [height] image. */
    fun squareCrop(width: Int, height: Int): Crop {
        val side = minOf(width, height).coerceAtLeast(1)
        return Crop((width - side) / 2, (height - side) / 2, side)
    }

    /**
     * Power-of-two shrink factor for decoding: as large as possible while the shorter side stays
     * at least [target] pixels, so a 12-megapixel photo is not fully decoded into memory.
     */
    fun sampleSizeFor(width: Int, height: Int, target: Int = SIZE_PX): Int {
        var sample = 1
        val shorter = minOf(width, height)
        while (shorter / (sample * 2) >= target) sample *= 2
        return sample
    }

    /** The stored path ("file:user_pictures/...") for a file name inside [DIR]. */
    fun storedPath(fileName: String): String = "$FILE_PREFIX$DIR/$fileName"

    fun isStoredFile(path: String): Boolean = path.startsWith(FILE_PREFIX)

    /** Resolves a stored path to a file inside [filesDir], or null if it points anywhere else. */
    fun resolve(filesDir: File, path: String): File? {
        if (!isStoredFile(path)) return null
        val name = path.removePrefix("$FILE_PREFIX$DIR/")
        if (name.isEmpty() || name.contains('/') || name.contains("..")) return null
        return File(File(filesDir, DIR), name)
    }

    /**
     * Imports [uri] and returns the stored path, or null when it is not a usable image.
     * Photo rotation (EXIF) is applied by the system decoder.
     */
    fun importFromUri(context: Context, uri: Uri): String? {
        return try {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val decoded = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val sample = sampleSizeFor(info.size.width, info.size.height)
                decoder.setTargetSampleSize(sample)
            }
            val crop = squareCrop(decoded.width, decoded.height)
            val square = Bitmap.createBitmap(decoded, crop.x, crop.y, crop.side, crop.side)
            val scaled = if (square.width == SIZE_PX) square
            else Bitmap.createScaledBitmap(square, SIZE_PX, SIZE_PX, true)

            val dir = File(context.filesDir, DIR).apply { mkdirs() }
            val file = File(dir, "user_${System.currentTimeMillis()}.png")
            file.outputStream().use { scaled.compress(Bitmap.CompressFormat.PNG, 100, it) }

            if (scaled !== square) scaled.recycle()
            if (square !== decoded) square.recycle()
            decoded.recycle()
            storedPath(file.name)
        } catch (e: Exception) {
            Log.e("UserAvatar", "Could not import the picked picture", e)
            null
        }
    }

    /** Removes pictures in [DIR] other than the one in [keepPath], so old choices do not pile up. */
    fun deleteOthers(filesDir: File, keepPath: String?) {
        val keep = keepPath?.let { resolve(filesDir, it) }
        File(filesDir, DIR).listFiles()?.forEach { if (it != keep) it.delete() }
    }
}
