package com.zhuinden.navigatorexamplemvp.presentation.paths.addoredittask;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.changehandlers.SegueViewChangeHandler;
import com.zhuinden.navigatorexamplemvp.R;
import com.zhuinden.navigatorexamplemvp.application.Key;

/**
 * Created by Zhuinden on 2017.01.25..
 */

@AutoValue
public abstract class AddOrEditTaskKey
        implements Key {
    public abstract Key parent();

    public abstract String taskId();

    public static Parcelable create(Key parent) {
        return createWithTaskId(parent, "");
    }

    public static Parcelable createWithTaskId(Key parent, String taskId) {
        return new AutoValue_AddOrEditTaskKey(R.layout.path_addoredittask, parent, taskId);
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
        return true;
    }

    @Override
    public int navigationViewId() {
        return 0;
    }

    @Override
    public boolean shouldShowUp() {
        return true;
    }

    @Override
    public View.OnClickListener fabClickListener(View view) {
        return v -> {
            AddOrEditTaskView addOrEditTaskCoordinator = (AddOrEditTaskView)view;
            addOrEditTaskCoordinator.saveTask();
            addOrEditTaskCoordinator.navigateBack();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_done;
    }
}
