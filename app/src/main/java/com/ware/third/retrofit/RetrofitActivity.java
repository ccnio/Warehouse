package com.ware.third.retrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.ware.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RetrofitActivity";
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        findViewById(R.id.bt).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);
        mImageView = findViewById(R.id.mImageView);
        String pathname = "/storage/emulated/0/DCIM/Camera/IMG_20191206_213347.jpg";
        Picasso.get().load(new File(pathname)).fit().into(mImageView);
//
//        Picasso.Builder builder = new Picasso.Builder(WareApp.sContext);
//        builder.listener((picasso, uri, exception) -> Log.d("FeedbackP", "onBindViewHolder: ex = " + exception.getMessage())
//        );
//        builder.build().load(new File(pathname)).resize(100, 100).into(mImageView);

        LinkedHashMap<String, Info> map = new LinkedHashMap<>();
        Info value = new Info();
        value.name = "a";
        map.put("a", value);

        ArrayList<Info> strings = new ArrayList<>(map.values());

        map.get("a").name = "b";

        Log.d(TAG, "onCreate: " + strings.get(0).name);

    }

    class Info{
        String name;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt:
                syncRequest();
                break;
            case R.id.bt2:

                final Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://api.github.com/")
                        .addConverterFactory(GsonConverterFactory.create())//Gson解析结果
                        .build();
                final GitHubService service = retrofit.create(GitHubService.class);
                Call<String> repos = service.listRepos("edreamoon");
                // 异步调用
                repos.enqueue(new Callback<String>() {

                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "onResponse: ");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d(TAG, "onFailure: ");
                    }
                });

                break;
        }

    }

    private void syncRequest() {

        //同步请求
        //Response<List<Repo>> response = repos.execute();
    }
}
