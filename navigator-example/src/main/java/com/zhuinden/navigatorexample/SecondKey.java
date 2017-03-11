package com.zhuinden.navigatorexample;

import android.os.Parcel;
import android.os.Parcelable;

import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewController;

/**
 * Created by Zhuinden on 2017.03.10..
 */
public class SecondKey
        implements StateKey, Parcelable {
    public SecondKey() {
    }

    protected SecondKey(Parcel in) {
    }

    public static final Creator<SecondKey> CREATOR = new Creator<SecondKey>() {
        @Override
        public SecondKey createFromParcel(Parcel in) {
            return new SecondKey(in);
        }

        @Override
        public SecondKey[] newArray(int size) {
            return new SecondKey[size];
        }
    };

    @Override
    public int layout() {
        return R.layout.fragment_second;
    }

    @Override
    public ViewController createViewController(Object... args) {
        return new SecondController(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int hashCode() {
        return SecondKey.class.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof SecondKey;
    }
}
