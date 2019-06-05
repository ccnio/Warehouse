package com.ware.third;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.ware.R;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class JsonActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RxJavaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        findViewById(R.id.common).setOnClickListener(this);
        findViewById(R.id.just).setOnClickListener(this);
        findViewById(R.id.fromIterable).setOnClickListener(this);
        findViewById(R.id.defer).setOnClickListener(this);
        findViewById(R.id.map).setOnClickListener(this);

    }

    private void map() {
        Observable.just("hello", "world!").map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) {
                return s.length();
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                Log.d(TAG, "map accept: " + integer);
            }
        });

        Observable.just("hello", "world").flatMap(new Function<String, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(String s) throws Exception {
                return Observable.just(s);
            }
        }).subscribe();
    }

    private void defer() {
        Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() {
                return Observable.just("hello", "world");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Log.d(TAG, "accept: " + s);
            }
        });
    }

    private void fromIterable() {
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        list.add("world");
        Observable.fromIterable(list).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Log.d(TAG, "accept: " + s);
            }
        });
    }

    private void just() {
        Observable.just("hello", "world").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Log.d(TAG, "accept: " + s);
            }
        });
    }

    private void common() {
        //创建被观察者
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) {
                Log.d(TAG, "emitter: ");
                e.onNext("hello");//发送事件
                e.onNext("world");
                e.onComplete();//结束事件
            }
        });
        //创建观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: ");
            }
        };
        //订阅操作
        observable.subscribe(observer);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.common:
                common();
                break;
            case R.id.just:
                just();
                break;
            case R.id.fromIterable:
                fromIterable();
                break;
            case R.id.defer:
                defer();
                break;
            case R.id.map:
                map();
                break;
        }
    }
}
