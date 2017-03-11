package com.zhuinden.navigator;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class NoOpViewChangeHandler
        implements ViewChangeHandler {
    @Override
    public void performViewChange(@NonNull ViewGroup container, @NonNull View previousView, @NonNull View newView, int direction, @NonNull CompletionCallback completionCallback) {
        container.removeView(previousView);
        container.addView(newView);
        completionCallback.onCompleted();
    }
}
