package com.ware.face;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ware.R;

import java.util.ArrayList;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceMoreAdapter extends RecyclerView.Adapter<FaceMoreAdapter.FaceMoreHolder> {
    private final LayoutInflater mInflater;
    private final int mType;
    public static final int TYPE_ME = 1;
    private ArrayList<String> mList = new ArrayList<>();

    public FaceMoreAdapter(Context context, int type) {
        mType = type;
        mInflater = LayoutInflater.from(context);
        for (int i = 0; i < 13; i++) {
            mList.add("pos " + i);
        }
    }

    @NonNull
    @Override
    public FaceMoreHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View view = mInflater.inflate(R.layout.layout_face_more, viewGroup, false);
        view.findViewById(R.id.stateView_).setVisibility(mType == TYPE_ME ? View.GONE : View.VISIBLE);
        return new FaceMoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaceMoreHolder holder, int pos) {
        holder.mNameView.setText(mList.get(pos));
        ImageView imageView = holder.mImageView;
        imageView.setImageResource(R.drawable.bit);
//        Picasso.with(mContext.getApplicationContext()).load(R.mipmap.ic_launcher)
//                .centerCrop().resize(imageView.getW(), imageView.getH()).memoryPolicy(MemoryPolicy.NO_CACHE)
//                .transform(new FaceRoundTrans(mRadius, t, l)).into(imageView);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class FaceMoreHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mNameView;

        public FaceMoreHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.mImageView);
            mNameView = itemView.findViewById(R.id.mNameView);
        }
    }
}
