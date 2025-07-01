package com.ccino.demo.media.list

import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheKeyFactory

class CustomCacheKeyFactory : CacheKeyFactory {
    override fun buildCacheKey(dataSpec: DataSpec): String {
        // 获取原始 URI
        val uri = dataSpec.uri

        // 从 URI 或其他地方提取所需信息
        // 例如：去除 URL 参数、替换域名、提取视频ID等

        // 示例：提取视频ID (假设格式为 https://example.com/videos/123?token=abc)
        val path = uri.path ?: ""
        val videoId = path.substringAfterLast("/")

        // 创建自定义缓存键（可以加入其他标识符）
        return "video_$videoId"

        // 或者简单去除查询参数
        // return uri.buildUpon().clearQuery().build().toString()
    }
}