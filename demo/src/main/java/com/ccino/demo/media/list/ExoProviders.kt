package com.ccino.demo.media.list

import com.ccino.demo.app
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache

object ExoProviders {
	val simpleCache by lazy {
		SimpleCache(app.cacheDir, LeastRecentlyUsedCacheEvictor(1024 * 1024 * 100), StandaloneDatabaseProvider(app))
	}

	val cacheDataSourceFactory: CacheDataSource.Factory by lazy {
		CacheDataSource.Factory()
			.setCache(simpleCache)
			.setCacheKeyFactory(CustomCacheKeyFactory())
			.setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
			.setCacheReadDataSourceFactory(FileDataSource.Factory())
			.setCacheWriteDataSinkFactory(CacheDataSink.Factory().setCache(simpleCache).setFragmentSize(Long.MAX_VALUE))
			.setFlags(CacheDataSource.FLAG_BLOCK_ON_CACHE)
	}

	val progressiveMediaSourceFactory: ProgressiveMediaSource.Factory by lazy {
		ProgressiveMediaSource.Factory(cacheDataSourceFactory)
	}
} 