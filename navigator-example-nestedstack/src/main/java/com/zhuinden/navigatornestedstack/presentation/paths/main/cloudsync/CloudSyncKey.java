package com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigatornestedstack.util.TransitionHandler;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.presentation.paths.main.MainView;
import com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync.another.AnotherKey;
import com.zhuinden.navigatornestedstack.util.Child;

import java.util.List;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class CloudSyncKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_cloudsync;
    }

    public static CloudSyncKey create() {
        return new AutoValue_CloudSyncKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CLOUDSYNC.name();
    }

    @Override
    protected List<?> initialKeys() {
        return HistoryBuilder.single(AnotherKey.create(this));
    }

    @Override
    public boolean hasNestedStack() {
        return true;
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new TransitionHandler();
    }
}
