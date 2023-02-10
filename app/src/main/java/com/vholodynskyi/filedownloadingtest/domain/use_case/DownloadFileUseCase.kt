package com.vholodynskyi.filedownloadingtest.domain.use_case

import android.os.Environment
import android.util.Log
import com.vholodynskyi.filedownloadingtest.common.Resource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import javax.inject.Inject

class DownloadFileUseCase @Inject constructor() {

    val percentage = MutableStateFlow(0.0)
    private val downloadsDirectory = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS
    )

    operator fun invoke(fileUrl: String): Flow<Resource<String>> = flow {
        var inputStream: BufferedInputStream? = null
        var outputStream: FileOutputStream? = null
        var connection: HttpURLConnection? = null

        if (fileUrl.isBlank()) {
            emit(Resource.Error<String>("Url is incorrect"))
            return@flow
        }

        try {
            emit(Resource.Loading<String>())

            val downloadTarget = targetFile(fileName(fileUrl))
            connection = URL(fileUrl).openConnection() as HttpURLConnection
            inputStream = BufferedInputStream(connection.inputStream)
            outputStream = FileOutputStream(downloadTarget.path)

            val size = connection.contentLength
            val buffer = ByteArray(4096)
            var downloadedFileSize = 0L
            var currentRead = 0

            while (currentRead != -1 && currentCoroutineContext().isActive) {
                downloadedFileSize += currentRead
                outputStream.write(buffer, 0, currentRead)
                currentRead = inputStream.read(buffer, 0, buffer.size)
                val p = roundOffDecimal(downloadedFileSize * 1.0/size)
                percentage.emit(p)
                // for testing only
                Log.d("DownloadingTest", "chunk $currentRead")
                delay(1)
                // for testing only
            }

            emit(Resource.Success<String>(downloadTarget.path))
        } catch (e: IOException) {
            emit(Resource.Error<String>(e.localizedMessage ?: "Couldn't reach server"))
        } finally {
            withContext(NonCancellable) {
                Log.d("DownloadingTest", "NonCancellable")
                connection?.disconnect()
                inputStream?.close()
                outputStream?.close()
            }
        }
    }.flowOn(Dispatchers.IO)

    private fun fileName(fileUrl: String): String = fileUrl.substring(
        fileUrl.lastIndexOf("/") + 1, fileUrl.length
    ) + "${System.currentTimeMillis()}"

    private fun targetFile(fileName: String): File = File(downloadsDirectory, fileName)
}

fun roundOffDecimal(number: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}