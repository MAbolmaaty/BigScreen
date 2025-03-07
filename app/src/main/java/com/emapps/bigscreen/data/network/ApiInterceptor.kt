package com.emapps.bigscreen.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

class ApiInterceptor() : Interceptor {

    companion object {
        private val TAG = ApiInterceptor::class.simpleName
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuffer = Buffer()
        request.body?.writeTo(requestBuffer)
        var requestBody: String? = null
        try {
            if (requestBuffer.size > 0) {
                requestBody = requestBuffer.readUtf8()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val response = chain.proceed(request)

        Log.d(
            TAG, String.format(
                "BigScreenApi: (%s)%s(%s)",
                request.method, request.url, response.code
            )
        )
        Log.d(TAG, String.format(
            "Request Body: %n%s",
            requestBody ?: "n/a"
        ))

        var wrappedBody = response.body?.let {
            val body = it.string()
            Log.d(TAG, String.format(
                "Response Body: %n%s",
                body
            ))
            ResponseBody.create(it.contentType(), body)
        }
        return response.newBuilder().body(wrappedBody).build()
    }
}