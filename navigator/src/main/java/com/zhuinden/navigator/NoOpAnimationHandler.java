package com.zhuinden.navigator;

import android.view.View;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class NoOpAnimationHandler
        implements AnimationHandler {
    @Override
    public void runAnimation(View previousView, View newView, int direction, CompletionListener completionListener) {
        completionListener.onCompleted();
    }
}
