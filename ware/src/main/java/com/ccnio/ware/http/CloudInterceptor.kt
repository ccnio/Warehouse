//package com.xiaomi.fitness.app
//
//import android.os.Build
//import com.xiaomi.common.crypt.CloudUtil
//import com.xiaomi.fitness.account.manager.AccountManager
//import com.xiaomi.fitness.account.token.TokenManager
//import com.xiaomi.fitness.common.extensions.application
//import com.xiaomi.fitness.common.utils.AppVersionUtils
//import com.xiaomi.fitness.net.url.ApiHolder
//import com.xiaomi.fitness.net.url.SecretData
//import okhttp3.*
//import okhttp3.ResponseBody.Companion.toResponseBody
//import retrofit2.Invocation
//import java.io.IOException
//import java.util.*
//import javax.inject.Inject
//
///**
// * 处理请求加密相关逻辑
// */
//class CloudInterceptor @Inject constructor(
//    private val mApiHolder: ApiHolder,
//    private val mTokenManager: TokenManager,
//    private val mAccountManager: AccountManager
//) : Interceptor {
//
//    companion object {
//        private const val HANDLE_PARAMS_TAG = "HandleParams"
//
//        private val mEncryptionParams = hashSetOf("signature", "rc4_hash__", "_nonce")
//    }
//
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//        val requestBuilder = request.newBuilder()
//        val url = request.url
//        val secret = mApiHolder.getSecret(url)
//        if (!encryptResponse(secret)) {
//            requestBuilder.header("X-XIAOMI-PROTOCAL-FLAG-CLI", "PROTOCAL-HTTP2")
//        }
//        val faceHost = url.host
//        if (faceHost.contains("watch-appstore")) {
//            requestBuilder.header("watch-appstore-common", getFaceHeaderParameter())
//        }
//        if (secret == null) {
//            return chain.proceed(request)
//        }
//        val method = request.method
//        var path = url.encodedPath
//        val pathPrefix = secret.pathPrefix
//        path = subpath(pathPrefix, path)
//        val serviceToken = mTokenManager.getServiceToken(secret.sid)
//        val nonce = CloudUtil.generateNonce(serviceToken?.timeDiff ?: 0)
//        val security = serviceToken?.security ?: ""
//        if ("GET" == method) {
//            handleGetParams(requestBuilder, request, path, secret, nonce, security)
//        } else if ("POST" == method) {
//            handlePostParams(requestBuilder, request, path, secret, nonce, security)
//        }
//        requestBuilder.header(HANDLE_PARAMS_TAG, "true")
//        val response = chain.proceed(requestBuilder.build())
//        if (!response.isSuccessful || !encryptResponse(secret)) {
//            return response
//        }
//        val responseBody = response.body!!
//        val content = responseBody.string()
//        return try {
//            val decryptedContent: String = decryptResponse(content, nonce, security)
//            response.newBuilder()
//                .body(decryptedContent.toResponseBody(responseBody.contentType()))
//                .build()
//        } catch (e: Exception) {
//            throw IOException("decrypt failed:$content")
//        }
//    }
//
//    private fun subpath(pathPrefix: String?, path: String): String {
//        var path1 = path
//        if (pathPrefix.isNullOrEmpty()) {
//            val index = path1.indexOf('/')
//            if (index != -1) {
//                path1 = path1.substring(index)
//            }
//        } else {
//            val index = path1.indexOf(pathPrefix, 0)
//            if (index != -1) {
//                path1 = path1.substring(index + pathPrefix.length)
//            }
//        }
//        return path1
//    }
//
//    private fun handleGetParams(
//        requestBuilder: Request.Builder,
//        request: Request,
//        path: String,
//        secret: SecretData?,
//        nonce: String,
//        security: String
//    ) {
//        val url = request.url
//        val urlBuilder = HttpUrl.Builder()
//            .scheme(url.scheme)
//            .host(url.host)
//            .encodedPath(url.encodedPath)
//        try {
//            val params: MutableMap<String, String> = HashMap()
//            val size: Int = url.querySize
//            val filterKeys = secret?.filterSignatureKeys
//
//            var arguments: List<*>? = null
//            val retry = request.header(HANDLE_PARAMS_TAG) == "true"
//            if (retry) {
//                arguments = request.tag(Invocation::class.java)?.arguments()
//            }
//            for (i in 0 until size) {
//                val name: String = url.queryParameterName(i)
//                val value: String? =
//                    if (retry && arguments != null && i < arguments.size) arguments[i] as String? else url.queryParameterValue(
//                        i
//                    )
//                if (filterKeys != null && !filterKeys.contains(name)) {
//                    if (!mEncryptionParams.contains(name)) {
//                        urlBuilder.setQueryParameter(name, value)
//                    }
//                } else {
//                    params[name] = value ?: ""
//                }
//            }
//            val encryptedParams =
//                getEncryptedParams(request.method, path, params, secret, nonce, security)
//            for ((key, value) in encryptedParams) {
//                urlBuilder.setQueryParameter(key, value)
//            }
//            requestBuilder.url(urlBuilder.build())
//        } catch (e: Exception) {
//            throw IOException(e.message)
//        }
//    }
//
//    private fun handlePostParams(
//        requestBuilder: Request.Builder,
//        request: Request,
//        path: String,
//        secret: SecretData?,
//        nonce: String,
//        security: String
//    ) {
//        val requestBody = request.body
//        if (requestBody is FormBody) {
//            val bodyBuilder = FormBody.Builder()
//            val params: MutableMap<String, String> = HashMap()
//            val filterKeys = secret?.filterSignatureKeys
//            var arguments: List<*>? = null
//            val retry = request.header(HANDLE_PARAMS_TAG) == "true"
//            if (retry) {
//                arguments = request.tag(Invocation::class.java)?.arguments()
//            }
//            for (i in 0 until requestBody.size) {
//                val name = requestBody.name(i)
//                val value =
//                    if (retry && arguments != null && i < arguments.size) arguments[i] as String else requestBody.value(
//                        i
//                    )
//                if (filterKeys != null && !filterKeys.contains(name)) {
//                    if (!mEncryptionParams.contains(name)) {
//                        bodyBuilder.add(name, value)
//                    }
//                } else {
//                    params[requestBody.name(i)] = requestBody.value(i)
//                }
//            }
//            try {
//                val encryptedParams =
//                    getEncryptedParams(request.method, path, params, secret, nonce, security)
//                for ((key, value) in encryptedParams) {
//                    bodyBuilder.add(key, value)
//                }
//                requestBuilder.post(bodyBuilder.build())
//            } catch (e: java.lang.Exception) {
//                throw IOException(e.message)
//            }
//        }
//    }
//
//    private fun getEncryptedParams(
//        method: String,
//        path: String,
//        params: MutableMap<String, String>,
//        secret: SecretData?,
//        nonce: String,
//        security: String
//    ): Map<String, String> {
//        val tempPath = if (!path.startsWith("/")) "/$path" else path
//        return if (encryptResponse(secret)) {
//            CloudUtil.encryptParams(method, tempPath, params, nonce, security)
//        } else {
//            CloudUtil.encryptParams2(tempPath, params, nonce, security)
//        }
//    }
//
//    private fun decryptResponse(content: String, nonce: String, security: String): String {
//        return CloudUtil.decryptResponse(content, nonce, security)
//    }
//
//    private fun encryptResponse(secret: SecretData?): Boolean {
//        if (!mAccountManager.isLogin) {
//            return false
//        }
//        return secret?.encryptResponse ?: true
//    }
//
//}