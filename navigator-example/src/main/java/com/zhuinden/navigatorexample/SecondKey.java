package com.zhuinden.navigatorexample;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.SegueViewChangeHandler;
import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.ViewController;

/**
 * Created by Zhuinden on 2017.03.10..
 */
@AutoValue
public abstract class SecondKey
        extends StateKey
        implements Parcelable {
    @Override
    public int layout() {
        return R.layout.fragment_second;
    }

    @Override
    public ViewController createViewController(Object... args) {
        return new SecondController(this);
    }

    public static SecondKey create() {
        return new AutoValue_SecondKey();
    }

    @Override
    public ViewChangeHandler getAnimationHandler() {
        return new SegueViewChangeHandler();
    }
}
