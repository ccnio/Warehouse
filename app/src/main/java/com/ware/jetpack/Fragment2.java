package com.ware.jetpack;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ware.R;

public class Fragment2 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.item_capture, container, false);
    }

    private void demoTransformation() {
        LocationLiveData data = LocationLiveData.getsIns(getContext());
        LiveData<String> stringLatLon = Transformations.map(data,
                location -> location.getLatitude() + ", " + location.getLongitude());

        Transformations.map(data, new Function<Location, String>() {
            @Override
            public String apply(Location location) {
                return location.getLatitude() + ", " + location.getLongitude();
            }
        });


        stringLatLon.observe(this, latLon -> {
            ((TextView) (this.getView().findViewById(R.id.tv_name))).setText(latLon);
        });

        stringLatLon.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        Transformations.switchMap(data, location -> getCountryName(location))
                .observe(this, country -> ((TextView) (this.getView().findViewById(R.id.tv_name))).setText(country));


    }

    // 返回当前位置所在的国家名字
    private LiveData<String> getCountryName(Location loc) {
        // 只是为了演示，实际中需要把转换的逻辑封装到自定义的 LiveData 中，
        // 和 LocationLiveData 类似
        MutableLiveData<String> ld = new MutableLiveData<>();
        ld.postValue("中国");
        return ld;
    }

}
