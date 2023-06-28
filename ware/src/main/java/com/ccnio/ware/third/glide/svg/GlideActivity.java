package com.ccnio.ware.third.glide.svg;

import android.app.Activity;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ccnio.ware.R;

/**
 * Displays an SVG image loaded from an android raw resource.
 */
public class GlideActivity extends Activity {
    private static final String TAG = "SVGActivity";

    //  private ImageView imageViewRes;
    private ImageView imageViewNet;
    private RequestBuilder<PictureDrawable> requestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glide_svg);


        ImageView userAvatarView = findViewById(R.id.imageview);
        String userAvatarUrl = "https://book-iot-cdn.turingos.cn/202207081229/241294e09a7f22d9f3729fc71c5f0d0b/svg/svgs-color-change/36530.svg";
        Utils.fetchSvg(this, userAvatarUrl, userAvatarView);

    }

    @Override
    protected void onStart() {
        super.onStart();
        reload();
    }

    public void cycleScaleType(View v) {
        ImageView.ScaleType curr = imageViewNet.getScaleType();
//    Log.w(TAG, "cycle: current=" + curr);
        ImageView.ScaleType[] all = ImageView.ScaleType.values();
        int nextOrdinal = (curr.ordinal() + 1) % all.length;
        ImageView.ScaleType next = all[nextOrdinal];
//    Log.w(TAG, "cycle: next=" + next);
//    imageViewRes.setScaleType(next);
        imageViewNet.setScaleType(next);
        reload();
    }

    private void reload() {
        Log.w(TAG, "reloading");
//    ((TextView) findViewById(R.id.button))
//            .setText(getString(R.string.scaleType, imageViewRes.getScaleType()));
//    loadRes();
        loadNet();
    }

    private void loadRes() {
//    Uri uri =
//            Uri.parse(
//                    ContentResolver.SCHEME_ANDROID_RESOURCE
//                            + "://"
//                            + getPackageName()
//                            + "/"
//                            + R.raw.android_toy_h);
//    requestBuilder.load(uri).into(imageViewRes);
    }

    private void loadNet() {
        Uri uri = Uri.parse("http://book-iot-cdn.turingos.cn/202207071919/d1551062a491b8c47c09849e4280a0fb/svg/svgs-color-change/30740.svg");
        requestBuilder.load(uri).into(imageViewNet);

        imageViewNet.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + imageViewNet.getDrawable());
            }
        }, 2000);
    }
}
