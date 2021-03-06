package com.zhuinden.navigatorexample;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;

/**
 * Created by Zhuinden on 2017.03.10..
 */
@AutoValue
public abstract class FirstKey
        extends StateTitleKey
        implements Parcelable {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public int layout() {
        return R.layout.path_first;
    }

    @Override
    @NonNull
    public ViewChangeHandler viewChangeHandler() {
        return new TransitionHandler();
    }

    @Override
    public String title() {
        return "First";
    }
}
