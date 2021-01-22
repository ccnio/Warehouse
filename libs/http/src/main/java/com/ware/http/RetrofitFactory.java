package com.ware.http;

import com.ware.http.interceptor.CommonInterceptor;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jianfeng.li on 19-7-18.
 */
public class RetrofitFactory {

    private HashMap<String, Retrofit> mRetrofits = new HashMap<>();

    private HttpLoggingInterceptor mLoggingInterceptor;

    private static class SingleHolder {
        private final static RetrofitFactory INSTANCE = new RetrofitFactory();
    }

    public static RetrofitFactory instance() {
        return SingleHolder.INSTANCE;
    }

    private RetrofitFactory() {
        if (BuildConfig.DEBUG) {
            mLoggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        }
    }

    public <T> T getService(Class<T> clazz, String baseUrl) {
        Retrofit retrofit = getRetrofit(baseUrl);
        return retrofit.create(clazz);
    }

    public Retrofit getRetrofit(String baseUrl) {
        Retrofit retrofit = mRetrofits.get(baseUrl);
        if (retrofit != null) return retrofit;

        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(initClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        mRetrofits.put(baseUrl, retrofit);
        return retrofit;
    }

    private OkHttpClient initClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(mLoggingInterceptor);
        }
        builder.addInterceptor(new CommonInterceptor());
        return builder.build();
    }

}
