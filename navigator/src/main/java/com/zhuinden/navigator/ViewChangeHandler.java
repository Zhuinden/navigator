package com.zhuinden.navigator;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public interface ViewChangeHandler {
    interface CompletionCallback {
        void onCompleted();
    }

    void performViewChange(@NonNull final ViewGroup container, @NonNull final View previousView, @NonNull final View newView, final int direction, @NonNull final CompletionCallback completionCallback);
}
