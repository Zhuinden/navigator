package com.zhuinden.navigatorexample;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.changehandlers.SegueViewChangeHandler;

/**
 * Created by Zhuinden on 2017.03.10..
 */
@AutoValue
public abstract class SecondKey
        extends StateTitleKey
        implements Parcelable {
    public static SecondKey create() {
        return new AutoValue_SecondKey();
    }

    @Override
    public int layout() {
        return R.layout.path_second;
    }

    @Override
    @NonNull
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }

    @Override
    public String title() {
        return "Second";
    }
}
