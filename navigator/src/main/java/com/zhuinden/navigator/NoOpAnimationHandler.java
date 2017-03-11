package com.zhuinden.navigator;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class NoOpAnimationHandler
        implements AnimationHandler {
    @Override
    public void runAnimation(@NonNull View previousView, @NonNull View newView, int direction, @NonNull CompletionListener completionListener) {
        completionListener.onCompleted();
    }
}
