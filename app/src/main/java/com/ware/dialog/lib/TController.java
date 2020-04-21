package com.ware.dialog.lib;

import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

import javax.annotation.Nullable;

public class TController implements Parcelable, Serializable { //<A extends TBaseAdapter>
    private int layoutRes;
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int width = ViewGroup.LayoutParams.WRAP_CONTENT;
    private float dimAmount;
    private int gravity;
    public int dx;
    public int dy;
    private String tag;
    private int[] ids;
    private boolean isCancelableOutside;
    private OnViewClickListener onViewClickListener;
    private OnBindViewListener onBindViewListener;
    //    private A adapter;
    //    private TBaseAdapter.OnAdapterItemClickListener adapterItemClickListener;
    private int orientation;
    private int dialogAnimationRes;
    private View dialogView;
    private DialogInterface.OnDismissListener onDismissListener;
    private DialogInterface.OnKeyListener onKeyListener;
    boolean cancelable;


    //////////////////////////////////////////Parcelable持久化//////////////////////////////////////////////////////
    public TController() {
    }

    protected TController(Parcel in) {
        layoutRes = in.readInt();
        height = in.readInt();
        width = in.readInt();
        dimAmount = in.readFloat();
        gravity = in.readInt();
        tag = in.readString();
        ids = in.createIntArray();
        isCancelableOutside = in.readByte() != 0;
        orientation = in.readInt();
        cancelable = in.readByte() != 0;
    }

    public static final Creator<TController> CREATOR = new Creator<TController>() {
        @Override
        public TController createFromParcel(Parcel in) {
            return new TController(in);
        }

        @Override
        public TController[] newArray(int size) {
            return new TController[size];
        }
    };

    //内容描述接口,不用管
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(layoutRes);
        dest.writeInt(height);
        dest.writeInt(width);
        dest.writeFloat(dimAmount);
        dest.writeInt(gravity);
        dest.writeString(tag);
        dest.writeIntArray(ids);
        dest.writeByte((byte) (isCancelableOutside ? 1 : 0));
        dest.writeInt(orientation);
        dest.writeByte((byte) (isCancelableOutside ? 1 : 0));
    }

    public int getLayoutRes() {
        return layoutRes;
    }

    public void setLayoutRes(int layoutRes) {
        this.layoutRes = layoutRes;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int mWidth) {
        this.width = mWidth;
    }

    public float getDimAmount() {
        return dimAmount;
    }

    public int getGravity() {
        return gravity;
    }

    public String getTag() {
        return tag;
    }

    public int[] getIds() {
        return ids;
    }

    public boolean isCancelableOutside() {
        return isCancelableOutside;
    }

    public OnViewClickListener getOnViewClickListener() {
        return onViewClickListener;
    }

    public int getOrientation() {
        return orientation;
    }

    public OnBindViewListener getOnBindViewListener() {
        return onBindViewListener;
    }

    public DialogInterface.OnDismissListener getOnDismissListener() {
        return onDismissListener;
    }

    public DialogInterface.OnKeyListener getOnKeyListener() {
        return onKeyListener;
    }

    public @Nullable
    View getDialogView() {
        return dialogView;
    }

    //列表
//    public A getAdapter() {
//        return adapter;
//    }

//    public void setAdapter(A adapter) {
//        this.adapter = adapter;
//    }

//    public TBaseAdapter.OnAdapterItemClickListener getAdapterItemClickListener() {
//        return adapterItemClickListener;
//    }
//
//    public void setAdapterItemClickListener(TBaseAdapter.OnAdapterItemClickListener adapterItemClickListener) {
//        this.adapterItemClickListener = adapterItemClickListener;
//    }

    public int getDialogAnimationRes() {
        return dialogAnimationRes;
    }

    /**************************************************************************
     */
    public static class TParams { //<A extends TBaseAdapter>
        public int mLayoutRes;
        public int mWidth;
        public int mHeight;
        public float mDimAmount = 0.2f;
        public int mGravity = Gravity.CENTER;
        public int dx;
        public int dy;
        public String mTag = "TDialog";
        public int[] ids;
        public boolean mIsCancelableOutside = true;
        public boolean cancelable = true;
        public OnViewClickListener mOnViewClickListener;
        public OnBindViewListener bindViewListener;
        public int mDialogAnimationRes = 0;//弹窗动画
        //        列表
        //        public A adapter;
        //        public TBaseAdapter.OnAdapterItemClickListener adapterItemClickListener;
        public int listLayoutRes;
        //        public int orientation = LinearLayoutManager.VERTICAL;//默认RecyclerView的列表方向为垂直方向
        public View mDialogView;//直接使用传入进来的View,而不需要通过解析Xml
        public DialogInterface.OnDismissListener mOnDismissListener;
        public DialogInterface.OnKeyListener mKeyListener;

        public void apply(TController tController) {
            if (mLayoutRes > 0) {
                tController.layoutRes = mLayoutRes;
            }
            if (mDialogView != null) {
                tController.dialogView = mDialogView;
            }
            if (mWidth > 0) {
                tController.width = mWidth;
            }
            if (mHeight > 0) {
                tController.height = mHeight;
            }
            tController.dimAmount = mDimAmount;
            tController.gravity = mGravity;
            tController.dx = dx;
            tController.dy = dy;
            tController.tag = mTag;
            if (ids != null) {
                tController.ids = ids;
            }
            tController.isCancelableOutside = mIsCancelableOutside;
            tController.cancelable = cancelable;
            tController.onViewClickListener = mOnViewClickListener;
            tController.onBindViewListener = bindViewListener;
            tController.onDismissListener = mOnDismissListener;
            tController.dialogAnimationRes = mDialogAnimationRes;
            tController.onKeyListener = mKeyListener;

//            if (adapter != null) {
//                tController.setAdapter(adapter);
//                if (listLayoutRes <= 0) {//使用默认的布局
//                    tController.setLayoutRes(R.layout.dialog_recycler);
//                } else {
//                    tController.setLayoutRes(listLayoutRes);
//                }
//                tController.orientation = orientation;
//            } else {
            if (tController.getLayoutRes() <= 0 && tController.getDialogView() == null) {
                throw new IllegalArgumentException("请先调用setLayoutRes()方法设置弹窗所需的xml布局!");
            }
//            }
//            if (adapterItemClickListener != null) {
//                tController.setAdapterItemClickListener(adapterItemClickListener);
//            }
        }
    }
}
