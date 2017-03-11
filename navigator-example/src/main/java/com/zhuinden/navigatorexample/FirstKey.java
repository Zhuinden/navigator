package com.zhuinden.navigatorexample;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewController;

/**
 * Created by Zhuinden on 2017.03.10..
 */

public class FirstKey
        implements StateKey, Parcelable {
    public FirstKey() {
    }

    protected FirstKey(Parcel in) {
    }

    public static final Creator<FirstKey> CREATOR = new Creator<FirstKey>() {
        @Override
        public FirstKey createFromParcel(Parcel in) {
            return new FirstKey(in);
        }

        @Override
        public FirstKey[] newArray(int size) {
            return new FirstKey[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int layout() {
        return R.layout.fragment_first;
    }

    @Override
    public ViewController createViewController(Object... args) {
        return new FirstController(this);
    }

    @Override
    public int hashCode() {
        return FirstKey.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof FirstKey;
    }
}
