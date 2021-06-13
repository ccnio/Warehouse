package com.ccnio.mdialog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jianfeng.li on 2021/6/13.
 */
public class SeriaBean implements Parcelable {
    private final String name;
     NotSeria notSeria;

    public SeriaBean(String name, NotSeria notSeria) {
        this.name = name;
        this.notSeria = notSeria;
    }

    protected SeriaBean(Parcel in) {
        name = in.readString();
    }

    public static final Creator<SeriaBean> CREATOR = new Creator<SeriaBean>() {
        @Override
        public SeriaBean createFromParcel(Parcel in) {
            return new SeriaBean(in);
        }

        @Override
        public SeriaBean[] newArray(int size) {
            return new SeriaBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

   public static class NotSeria {
        public final String desc;

        public NotSeria(String desc) {
            this.desc = desc;
        }
    }

}

