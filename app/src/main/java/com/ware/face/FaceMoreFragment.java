package com.ware.face;

import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.ware.R;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceMoreFragment extends FaceBaseFragment {
    private FaceMoreAdapter mAdapter;

    @Override
    protected void initView(View view) {
        super.initView(view);
        mAdapter = new FaceMoreAdapter(getContext(), FaceAdapter.TYPE_MORE);

        GridLayoutManager manager = new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false);
        FaceDecor decor = new FaceDecor(DisplayUtil.dip2px(15), getResources().getDimensionPixelOffset(R.dimen.face_column_divider), true);
        mRecyclerView.addItemDecoration(decor);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);
    }
}
