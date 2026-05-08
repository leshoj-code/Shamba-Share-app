package com.ojiambo.shambashare.network

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryConfig {

    fun init(context: Context) {
        val config = mapOf(
            "cloud_name" to "dajo9dvfv",
            "api_key"    to "983574557549958",
            "api_secret" to "vCwkVTxeR2qEzI2LaRFer2X99wk"
        )
        MediaManager.init(context, config)
    }

    fun uploadImage(
        context:   Context,
        imageUri:  android.net.Uri,
        onSuccess: (String) -> Unit,
        onError:   (String) -> Unit
    ) {
        MediaManager.get()
            .upload(imageUri)
            .option("folder", "shambashare/equipment")
            .option("resource_type", "image")
            .callback(object : com.cloudinary.android.callback.UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"]?.toString() ?: ""
                    onSuccess(url)
                }
                override fun onError(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {
                    onError(error.description)
                }
                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {}
            })
            .dispatch(context)
    }
}

