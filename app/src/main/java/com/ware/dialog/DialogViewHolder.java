package com.ware.dialog;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.IdRes;

import com.ware.R;

public class DialogViewHolder implements View.OnClickListener {

    public View view;
    private SparseArray<View> mViews;
    private BaseDialog mDialog;

    public DialogViewHolder(View view, BaseDialog dialog) {
        this.view = view;
        this.mDialog = dialog;
        mViews = new SparseArray<>();
    }

    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = this.view.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.mCloseView) {
            mDialog.dismiss();
        }

        if (mDialog.getOnClickListener() != null) {
            mDialog.getOnClickListener().onClick(this, view, mDialog);
        }
    }

    public DialogViewHolder addOnClickListener(@IdRes final int viewId) {
        final View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(this);
        }
        return this;
    }
}
