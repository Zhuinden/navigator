package com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync.another.internal;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.presentation.paths.main.MainView;
import com.zhuinden.navigatornestedstack.util.Child;
import com.zhuinden.navigatornestedstack.util.TransitionHandler;

/**
 * Created by Zhuinden on 2017.02.25..
 */

@AutoValue
public abstract class InternalKey
        extends Key
        implements Child {
    @Override
    public int layout() {
        return R.layout.path_internal;
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }

    public static InternalKey create(Object parent) {
        return new AutoValue_InternalKey(parent);
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new TransitionHandler();
    }
}