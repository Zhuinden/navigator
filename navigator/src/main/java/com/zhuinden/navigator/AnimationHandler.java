package com.zhuinden.navigator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public interface AnimationHandler {
    interface CompletionListener {
        void onCompleted();
    }

    void runAnimation(@Nullable final View previousView, @NonNull final View newView, final int direction, @NonNull final CompletionListener completionListener);
}
