package com.zhuinden.navigatorexamplemvp.presentation.paths.taskdetail;

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
public abstract class TaskDetailKey
        implements Key {
    abstract String taskId();

    public static TaskDetailKey create(String taskId) {
        return new AutoValue_TaskDetailKey(R.layout.path_taskdetail, taskId);
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }

    @Override
    public int menu() {
        return R.menu.taskdetail_fragment_menu;
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
            TaskDetailView coordinator = (TaskDetailView)view;
            coordinator.editTask();
        };
    }

    @Override
    public int fabDrawableIcon() {
        return R.drawable.ic_edit;
    }
}
