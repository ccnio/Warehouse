package com.ware.http.interceptor;

import com.ware.http.base.NoNetException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by jianfeng.li on 19-12-26.
 */
public class CommonInterceptor implements Interceptor {
    private static final String TAG = "CommonInterceptor";

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        boolean netEnable = true;// NetUtil.isNetworkAvailable();
//        Log.d(TAG, "intercept: netEnable = " + netEnable);
        if (!netEnable) {
            throw new NoNetException("network is unavailable");
        }
        return chain.proceed(chain.request());
    }
}
