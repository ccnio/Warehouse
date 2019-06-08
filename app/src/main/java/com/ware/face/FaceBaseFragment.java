package com.ware.face;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceBaseFragment extends Fragment {
    //    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face_list, container, false);
        ButterKnife.bind(this, view);

        initView(view);
        return view;
    }

    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.mRecyclerView);
    }
}
