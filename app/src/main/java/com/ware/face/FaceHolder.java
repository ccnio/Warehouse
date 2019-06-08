package com.ware.face;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ware.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.mImageView)
    FaceImageView mImageView;
    @BindView(R.id.mNameView)
    TextView mNameView;
    @BindView(R.id.mStateView)
    TextView mStatView;
    public FaceHolder(@NonNull View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.mImageView);
        mNameView = itemView.findViewById(R.id.mNameView);
        mStatView = itemView.findViewById(R.id.mStateView);
        ButterKnife.bind(this, itemView);
//        new Circle
    }
}
