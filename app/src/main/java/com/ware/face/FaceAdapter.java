package com.ware.face;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ware.R;

import java.util.ArrayList;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceAdapter extends RecyclerView.Adapter<FaceHolder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private final int mType;
    public static final int TYPE_ME = 1;
    public static final int TYPE_MORE = 2;
    private ArrayList<String> mList = new ArrayList<>();
    private static final int VER_MARGIN = DisplayUtil.dip2px(4f);
    private static final int HOR_MARGIN = DisplayUtil.dip2px(4f);
    private static final int mRadius = DisplayUtil.dip2px(16.7f);

    public FaceAdapter(Context context, int type) {
        mContext = context;
        mType = type;
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < 13; i++) {
            mList.add("pos " + i);
        }
    }

    @NonNull
    @Override
    public FaceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View view = mInflater.inflate(R.layout.layout_face, viewGroup, false);
        view.findViewById(R.id.mStateView).setVisibility(mType == TYPE_ME ? View.GONE : View.VISIBLE);
        return new FaceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaceHolder holder, int pos) {
        holder.mNameView.setText(mList.get(pos));
//        "http://img02.tooopen.com/images/20160509/tooopen_sy_161967094653.jpg"
        int l = 0, t = 0;
        FaceImageView imageView = holder.mImageView;
        if (pos == 0) { // if current use
            l = HOR_MARGIN;
            t = VER_MARGIN;
            imageView.showBorder(true);
        } else {
            imageView.showBorder(true);
        }
//        imageView.setImageResource(R.drawable.green_girl);
//        Picasso.with(mContext.getApplicationContext()).load(R.mipmap.ic_launcher)
//                .centerCrop().resize(imageView.getW(), imageView.getH()).memoryPolicy(MemoryPolicy.NO_CACHE)
//                .transform(new FaceRoundTrans(mRadius, t, l)).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
