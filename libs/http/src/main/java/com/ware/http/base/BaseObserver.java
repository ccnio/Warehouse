package com.ware.http.base;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ware.http.HttpHelper;

import io.reactivex.observers.ResourceObserver;
import retrofit2.HttpException;


/**
 * Created by jianfeng.li on 19-7-18.
 */
public abstract class BaseObserver<T extends BaseResp> extends ResourceObserver<T> {

    public abstract void onSuccess(@NonNull T t);


    public abstract void onFail(int code, String msg);

    @Override
    public void onError(Throwable e) {
        onFail(e instanceof NoNetException ? HttpHelper.HTTP_NO_NET_CODE : e instanceof HttpException ? ((HttpException) e).code() : HttpHelper.HTTP_ERROR_CODE, e.getMessage());
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onNext(T t) {
        if (t != null && t.oK()) {
            onSuccess(t);
        } else {
            if (t == null) {
                Log.d("BaseObserver", "onNext: fail entity is null");
                onFail(HttpHelper.HTTP_UNDEFINED_CODE, "entity is null");
            } else {
                Log.d("BaseObserver", "onNext: fail " + t.getCode() + "  " + (TextUtils.isEmpty(t.getMsg()) ? "msg is empty" : t.getMsg()));
                onFail(t.getCode(), t.getMsg());
            }
        }

    }
}
