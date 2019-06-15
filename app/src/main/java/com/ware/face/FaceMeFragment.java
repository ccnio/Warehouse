package com.ware.face;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ware.R;
import com.ware.systip.recyclerview.RecyclerDecor;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceMeFragment extends FaceBaseFragment {

    private FaceAdapter mAdapter;

    @Override
    protected void initView(View view) {
        super.initView(view);
        mAdapter = new FaceAdapter(getContext(), FaceAdapter.TYPE_ME);

        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        RecyclerDecor decor = new RecyclerDecor(DisplayUtil.dip2px(15), getResources().getDimensionPixelOffset(R.dimen.face_column_divider), false);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
