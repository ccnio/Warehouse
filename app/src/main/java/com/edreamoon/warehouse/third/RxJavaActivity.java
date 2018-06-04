package com.edreamoon.warehouse.third;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.edreamoon.warehouse.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxJavaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RxJavaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_ac);
        findViewById(R.id.common).setOnClickListener(this);
        findViewById(R.id.just).setOnClickListener(this);
        findViewById(R.id.fromIterable).setOnClickListener(this);
        findViewById(R.id.defer).setOnClickListener(this);
        findViewById(R.id.map).setOnClickListener(this);
        findViewById(R.id.voidMsg).setOnClickListener(this);


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
            case R.id.voidMsg:
                voidMsg();
                break;
        }
    }

    private void voidMsg() {
        Observable.just("hello", "world").subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                Log.d(TAG, "accept: " + s);
            }
        });
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


/**
 * 上面的Map操作符是把每一个元素转换成一个新的元素，但是flatMap操作符是把每一个元素转换成新的被观察者，每个被观察者发射的元素将会合并成新的被观察者，这些元素顺序输出
 *
 * 对Observable发射的数据都应用(apply)一个函数，这个函数返回一个Observable，然后合并这些Observables，并且发送（emit）合并的结果。
 * flatMap和map操作符很相像，flatMap发送的是合并后的Observables，map操作符发送的是应用函数后返回的结果集
 */
Observable.just(7, 8).flatMap(new Function<Integer, ObservableSource<String>>() {
    @Override
    public ObservableSource<String> apply(@NonNull Integer integer) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add("I am value " + integer);
        }
        return Observable.fromIterable(list);
    }
}).subscribe(new Consumer<String>() {
    @Override
    public void accept(@NonNull String s) {
        Log.e(TAG, "flatMap : accept : " + s + "\n");
    }
});

/**
 05-18 19:33:24.943 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 7
 05-18 19:33:24.943 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 7
 05-18 19:33:24.943 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 7
 05-18 19:33:24.943 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 7
 05-18 19:33:24.944 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 8
 05-18 19:33:24.944 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 8
 05-18 19:33:24.944 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 8
 05-18 19:33:24.944 5284-5284/com.edreamoon.warehouse E/RxJavaActivity: flatMap : accept : I am value 8
 */


        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            list.add(i + "");
        }
        Observable.just(list).flatMap(new Function<List<String>, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(List<String> strings) throws Exception {
                return Observable.fromIterable(strings);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String o) throws Exception {
                Log.i("test2" , o);
            }
        });
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
                Log.d(TAG, "fromIterable accept: " + s);
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

}
