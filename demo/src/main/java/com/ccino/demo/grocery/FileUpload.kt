/**
 * Created by jianfeng.li on 2023/11/28.
 * 文件上传主要逻辑
 */


/*
const val TAG = "FileUpload"

private const val UPLOAD_RETRY_COUNT = 1 // 上传重试次数
private const val URL_RETRY_COUNT = 1
private const val URL_RETRY_DELAY_MILLS = 1000L
private val DEFAULT_ERROR = Resource.Error(errorMsg = StringUtils.getString(R.string.psl_error_desc))

class FileUpload(
    private val urlSize: Int = 1, // size of preload url
    private val autoFetchUrl: Boolean = true, // auto load url
    private val fileSuffix: String = DEFAULT_FILE_SUFFIX
) : KoinComponent, NetworkUtils.OnNetworkStatusChangedListener, Utils.OnAppStatusChangedListener {
    private val fillMutex = Mutex()
    private val scope = MainScope()
    private val api: HttpApi by inject()
    private val urlQueue = ArrayBlockingQueue<FileUploadInfo>(urlSize) // cache url
    private val foregroundEmitter by lazy { MutableStateFlow(AppUtils.isAppForeground()) }
    private val netConnectEmitter by lazy { MutableStateFlow(NetworkUtils.isConnected()) }

    init {
        Logu.d(TAG, "init: urlSize=$urlSize, auto=$autoFetchUrl")
        if (autoFetchUrl) {
            scope.launch {
                foregroundEmitter.combine(netConnectEmitter) { foreground, connect ->
                    Logu.d(TAG, "autoEmitter: fore=$foreground, connect=$connect")
                    connect && foreground
                }.collect {
                    if (it) autoPrepareUrl()
                }
            }
            NetworkUtils.registerNetworkStatusChangedListener(this)
            AppUtils.registerAppStatusChangedListener(this)
        }
    }

    private fun autoPrepareUrl() {
        if (!autoFetchUrl) return
        Logu.d(TAG, "autoPrepareUrl: ")
        scope.launch { prepareUrl() }
    }

    suspend fun upload(file: File) = doUpload(file)
    suspend fun upload(path: String) = doUpload(path)
    suspend fun upload(bitmap: Bitmap, compressQuality: Int = BITMAP_QUALITY) = doUpload(bitmap, compressQuality)

    private suspend fun <T> doUpload(resource: T, qualityForBitmap: Int = BITMAP_QUALITY): Resource<FileUploadInfo> {
        if (!scope.isActive) {
            Logu.e(TAG, "file upload destroyed")
            return Resource.Error(errorMsg = "file upload  destroyed")
        }

        val connected = NetworkUtils.isConnected()
        Logu.d(TAG, "doUpload: connect=$connected")
        return if (!connected) Resource.Error(throwable = NetUnavailableException())
        else doUploadWithRetry(resource, qualityForBitmap).first()
    }

    private suspend fun <T> doUploadWithRetry(resource: T, qualityForBitmap: Int = BITMAP_QUALITY) =
        flow {
            val urlRet = getUploadUrl()
            Logu.d(TAG, "uploadWithRetry: urlRet=$urlRet")
            val ret = if (urlRet !is Resource.Success) urlRet
            else {
                val uploadRet = requestUpload(resource, requireNotNull(urlRet.data), qualityForBitmap)
                urlQueue.remove(urlRet.data)
                if (uploadRet.needUploadRetry()) throw UploadException(uploadRet) else uploadRet
            }
            emit(ret)
        }.retryWhen { cause, retry ->
            Logu.d(TAG, "uploadWithRetry: retry=$retry")
            retry < UPLOAD_RETRY_COUNT && cause is UploadException
        }.catch {
            Logu.w(TAG, "uploadWithRetry: catch")
            emit(if (it is UploadException) it.error else DEFAULT_ERROR)
        }.onCompletion {
            Logu.d(TAG, "uploadWithRetry: onCompletion")
            autoPrepareUrl()
        }

    private suspend fun <T> requestUpload(
        resource: T, info: FileUploadInfo, qualityForBitmap: Int
    ): Resource<FileUploadInfo> {
        val uploadUrl = requireNotNull(info.uploadUrl)
        val body = when (resource) {
            is Bitmap -> resource.toRequestBody(qualityForBitmap)
            is File -> resource.asRequestBody(getMimeType(resource).toMediaTypeOrNull())
            is String -> File(resource).run { asRequestBody(getMimeType(this).toMediaTypeOrNull()) }
            else -> throw RuntimeException("requestUpload unsupported type")
        }
        val result = runCatching {
            val response = api.uploadFile(uploadUrl, body)
            if (response.isSuccessful) Resource.Success(data = info)
            else Resource.Error(response.code(), response.errorBody()?.string())
        }
        Logu.d(TAG, "requestUpload: ret=$result")
        return result.getOrElse { Resource.Error(throwable = it) }
    }

    // first cache, then server
    private suspend fun getUploadUrl(): Resource<FileUploadInfo> {
        trimUrls()
        val peek = urlQueue.peek()
        if (peek.valid()) {
            Logu.d(TAG, "getUploadUrl: use cache")
            return Resource.Success(peek)
        }

        fillMutex.withLock { Logu.d(TAG, "getUploadUrl: fill mutex") }
        return if (!urlQueue.isEmpty()) Resource.Success(urlQueue.peek()) else requestUrl()
    }

    // request upload url
    private suspend fun requestUrl(): Resource<FileUploadInfo> {
        val urlResp = safeApiCall { api.generateFileUpLoadTargetUrl(fileSuffix).toResource() }
        Logu.d(TAG, "requestUrl: $urlResp")
        return if (urlResp is Resource.Success) {
            val data = urlResp.data.toFileUploadInfo()
            val offer = data?.let { urlQueue.offer(it) }
            if (offer == true) Resource.Success(data = data) else DEFAULT_ERROR
        } else urlResp as Resource.Error
    }

    // prepare upload url: full false 的话尝试请求一个 url, 否则尝试填满 urlQueue
    suspend fun prepareUrl(full: Boolean = true): List<Resource<FileUploadInfo>> {
        if (!scope.isActive) {
            Logu.e(TAG, "file upload destroyed")
            return emptyList()
        }
        fillMutex.withLock { return prepareUrlWithRetry(full) }
    }

    private suspend fun prepareUrlWithRetry(full: Boolean = true, retryIndex: Int = 0): List<Resource<FileUploadInfo>> {
        trimUrls()
        val lack = urlSize - urlQueue.size
        Logu.d(TAG, "prepareUrl: lack=$lack, full=$full")
        if (lack <= 0) return emptyList()
        val jobs = mutableListOf<Deferred<Resource<FileUploadInfo>>>()
        val list = coroutineScope {
            repeat(if (full) lack else 1) { jobs.add(async { requestUrl() }) }
            jobs.awaitAll()
        }
        Logu.d(TAG, "prepareUrl: retry=$retryIndex, ret=$list")
        if (retryIndex == URL_RETRY_COUNT) return list

        val needRetry = list.find { it is Resource.Error } != null
        return if (needRetry) {
            val succeedList = list.filterIsInstance<Resource.Success<FileUploadInfo>>()
            delay(URL_RETRY_DELAY_MILLS)
            succeedList + prepareUrlWithRetry(full, retryIndex + 1)
        } else list
    }

    // retain valid urls
    private fun trimUrls() {
        val retain = urlQueue.retainAll { it.valid() }
        Logu.d(TAG, "trimUrls: retain=$retain, size=${urlQueue.size}")
    }

    override fun onConnected(networkType: NetworkUtils.NetworkType?) = onNetChanged()
    override fun onDisconnected() = onNetChanged()

    private fun onNetChanged() {
        val connected = NetworkUtils.isConnected()
        scope.launch { netConnectEmitter.emit(connected) }
    }

    override fun onForeground(activity: Activity?) {
        foregroundEmitter.value = true
    }

    override fun onBackground(activity: Activity?) {
        foregroundEmitter.value = false
    }

    fun destroy() {
        Logu.d(TAG, "destroy: ")
        if (autoFetchUrl) {
            NetworkUtils.unregisterNetworkStatusChangedListener(this)
            AppUtils.unregisterAppStatusChangedListener(this)
        }
        scope.cancel()
    }
}
*/
