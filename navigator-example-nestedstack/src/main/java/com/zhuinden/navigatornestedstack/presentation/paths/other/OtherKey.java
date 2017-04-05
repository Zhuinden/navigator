package com.zhuinden.navigatornestedstack.presentation.paths.other;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.changehandlers.FadeViewChangeHandler;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;

/**
 * Created by Owner on 2017. 02. 27..
 */
@AutoValue
public abstract class OtherKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_other;
    }

    @Override
    public String stackIdentifier() {
        return "";
    }

    public static OtherKey create() {
        return new AutoValue_OtherKey();
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new FadeViewChangeHandler();
    }
}
