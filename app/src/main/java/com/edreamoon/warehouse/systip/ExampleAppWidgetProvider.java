package com.edreamoon.warehouse.systip;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.RemoteViews;

import com.edreamoon.warehouse.R;

import java.io.File;
import java.io.IOException;


public class ExampleAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "ExampleApp";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
//        Intent intent = new Intent(CLICK_ACTION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, R.id.doge_imageView, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setOnClickPendingIntent(R.id.doge_imageView, pendingIntent);
        Uri bmpUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/remoteviews.jpg"));
//        remoteViews.setImageViewUri(R.id.iv_show, bmpUri);


//        try {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), bmpUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Utils.saveBitmap(bitmap, Environment.getExternalStorageDirectory().getAbsolutePath() + "/remoteviews.jpg");
////        } catch (Exception e) {
////            handle exception
////        }
        remoteViews.setImageViewBitmap(R.id.iv_show, bitmap);
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }
}