package com.ware.jetpack;

import androidx.lifecycle.LiveData;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationLiveData extends LiveData<Location> {
    private static final String TAG = "LiveData";
    private LocationManager locationManager;
    private static LocationLiveData sIns;

    public LocationLiveData(Context applicationContext) {

    }

    public static LocationLiveData getsIns(Context context) {
        if (sIns == null) {
            sIns = new LocationLiveData(context.getApplicationContext());
        }
        return sIns;
    }

    //省略其他函数
}