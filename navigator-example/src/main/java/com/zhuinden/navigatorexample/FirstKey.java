package com.zhuinden.navigatorexample;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewController;

/**
 * Created by Zhuinden on 2017.03.10..
 */
@AutoValue
public abstract class FirstKey
        implements StateKey, Parcelable {
    @Override
    public int layout() {
        return R.layout.fragment_first;
    }

    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public ViewController createViewController(Object... args) {
        return new FirstController(this);
    }
}
