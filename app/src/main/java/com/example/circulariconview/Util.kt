import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import com.arthenica.mobileffmpeg.FFmpeg
import java.io.File
import java.io.FileOutputStream

object GifCreator {
    private const val TAG = "GifCreator"

    private fun saveFrameToFile(context: Context, bitmap: Bitmap, frameIndex: Int): String {
        val frameDir = File(context.cacheDir, "frames")
        if (!frameDir.exists()) frameDir.mkdirs()

        val frameFile = File(frameDir, "frame_$frameIndex.png")
        return try {
            FileOutputStream(frameFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
            frameFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "❌ Lỗi lưu frame $frameIndex: ${e.message}")
            ""
        }
    }

    private fun generateFrames(context: Context, bitmap: Bitmap): List<String> {
        val totalFrames = 60  // 2 giây * 30 FPS = 60 frames
        val framePaths = mutableListOf<String>()
        val width = bitmap.width
        val height = bitmap.height

        for (i in 0 until totalFrames) {
            val angle = (360f / totalFrames) * i // Góc xoay chia nhỏ hơn
            val rotatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(rotatedBitmap)
            val matrix = Matrix().apply { postRotate(angle, width / 2f, height / 2f) }
            canvas.drawBitmap(bitmap, matrix, null)

            val framePath = saveFrameToFile(context, rotatedBitmap, i)
            if (framePath.isNotEmpty()) framePaths.add(framePath)
            rotatedBitmap.recycle()
        }

        Log.d(TAG, "✅ Đã tạo ${framePaths.size} frames")
        return framePaths
    }

    fun createGifFromFrames(context: Context, bitmap: Bitmap, outputGifName: String) {
        val framePaths = generateFrames(context, bitmap)
        if (framePaths.isEmpty()) {
            Log.e(TAG, "❌ Không có frame để tạo GIF!")
            return
        }

        val frameDir = File(context.cacheDir, "frames").absolutePath
        val outputGifPath = File(context.filesDir, outputGifName).absolutePath

        Log.d(TAG, "Frame dir: $frameDir")
        Log.d(TAG, "Output GIF path: $outputGifPath")

        val command = arrayOf(
            "-framerate", "30",                   // 30 FPS
            "-i", "$frameDir/frame_%d.png",       // Ảnh đầu vào
            "-loop", "0",                         // Vòng lặp vô hạn
            "-y", outputGifPath                   // Ghi đè file
        )

        Log.d(TAG, "FFMPEG command: ${command.joinToString(" ")}")
        val resultCode = FFmpeg.execute(command)
        if (resultCode == 0) {
            Log.d(TAG, "✅ GIF đã tạo tại: $outputGifPath")
        } else {
            Log.e(TAG, "❌ Lỗi tạo GIF, code: $resultCode")
        }

        File(context.cacheDir, "frames").deleteRecursively()
    }
}