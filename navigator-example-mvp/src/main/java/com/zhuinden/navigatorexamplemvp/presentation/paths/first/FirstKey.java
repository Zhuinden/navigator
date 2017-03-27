package com.zhuinden.navigatorexamplemvp.presentation.paths.first;

import android.support.annotation.NonNull;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.changehandlers.SegueViewChangeHandler;
import com.zhuinden.navigatorexamplemvp.R;
import com.zhuinden.navigatorexamplemvp.application.Key;

/**
 * Created by Owner on 2017. 01. 12..
 */
@AutoValue
public abstract class FirstKey implements Key {
    public static FirstKey create() {
        return new AutoValue_FirstKey(R.layout.path_first);
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }

    @Override
    public int menu() {
        return R.menu.empty_menu;
    }

    @Override
    public boolean isFabVisible() {
        return false;
    }

    @Override
    public int navigationViewId() {
        return 0;
    }

    @Override
    public boolean shouldShowUp() {
        return false;
    }

    @Override
    public View.OnClickListener fabClickListener(View view) {
        return v -> {
        };
    }

    @Override
    public int fabDrawableIcon() {
        return 0;
    }
}
