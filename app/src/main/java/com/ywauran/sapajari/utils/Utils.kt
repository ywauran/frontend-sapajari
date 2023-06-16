package com.ywauran.sapajari.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale


fun tmpFile(context: Context): File {
    val timeStamp = SimpleDateFormat("dd-MMM", Locale.US).format(System.currentTimeMillis())
    return File.createTempFile(
        timeStamp, ".jpg",
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    )
}

fun fileNameByTimeStamp(context: Context): File {
    val timeStamp: String = SimpleDateFormat(
        "dd-MMM",
        Locale.US
    ).format(System.currentTimeMillis())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun Uri.uriToFile(context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val file = fileNameByTimeStamp(context)

    val inputStream = contentResolver.openInputStream(this) as InputStream
    val outputStream: OutputStream = FileOutputStream(file)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return file
}