package com.edreamoon.warehouse.third.retrofit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.edreamoon.warehouse.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RetrofitActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        findViewById(R.id.bt).setOnClickListener(this);
        findViewById(R.id.bt2).setOnClickListener(this);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
//                .addConverterFactory(GsonConverterFactory.create())//Gson解析结果
                .addConverterFactory(ScalarsConverterFactory.create())
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

        //同步请求
        //Response<List<Repo>> response = repos.execute();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt:
                syncRequest();
                break;
            case R.id.bt2:
                break;
        }

    }

    private void syncRequest() {

    }
}
