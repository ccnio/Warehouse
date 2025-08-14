package com.ccino.demo.media



val videoList = mutableListOf<VideoInfo>().apply {
    add(VideoInfo("https://v-cdn.zjol.com.cn/276982.mp4", "视频0", "https://picsum.photos/id/237/1200/800"))
    add(VideoInfo("https://v-cdn.zjol.com.cn/276972.mp4", "视频1", "https://picsum.photos/id/1025/1200/800"))
    add(VideoInfo("https://v-cdn.zjol.com.cn/276970.mp4", "视频2", "https://picsum.photos/id/1003/1200/800"))
    add(VideoInfo("https://v-cdn.zjol.com.cn/276670.mp4", "视频3", "https://picsum.photos/id/1015/1200/800"))
    add(VideoInfo("https://www.exit109.com/~dnn/clips/RW20seconds_2.mp4", "视频4", "https://picsum.photos/id/1018/1200/800"))
    add(VideoInfo("https://v-cdn.zjol.com.cn/276982.mp4", "视频5", "https://picsum.photos/id/1024/1200/800"))
    add(VideoInfo("https://v-cdn.zjol.com.cn/276972.mp4", "视频6", "https://picsum.photos/id/1035/1200/800"))
    add(VideoInfo("https://v-cdn.zjol.com.cn/276970.mp4", "视频7", "https://picsum.photos/id/1043/1200/800"))
}


data class VideoInfo(val url: String, val title: String, val cover: String)