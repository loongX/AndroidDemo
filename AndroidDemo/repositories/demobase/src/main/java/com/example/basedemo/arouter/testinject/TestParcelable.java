package com.example.basedemo.arouter.testinject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/7/7.
 */

public class TestParcelable implements Parcelable {
    public String name;
    public int id;

    public TestParcelable() {
    }

    public TestParcelable(String name, int id) {
        this.name = name;
        this.id = id;
    }

    protected TestParcelable(Parcel in) {
        name = in.readString();
        id = in.readInt();
    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        @Override
        public TestParcelable createFromParcel(Parcel in) {
            return new TestParcelable(in);
        }

        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
    }
}
