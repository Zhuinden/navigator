package com.zhuinden.navigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public abstract class ViewAnimatorChangeHandler
        implements ViewChangeHandler {
    @Override
    public void performViewChange(@NonNull final ViewGroup container, @NonNull final View previousView, @NonNull final View newView, final int direction, @NonNull final CompletionCallback completionCallback) {
        container.addView(newView);
        ViewUtils.waitForMeasure(newView, new ViewUtils.OnMeasuredCallback() {
            @Override
            public void onMeasured(View view, int width, int height) {
                runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        container.removeView(previousView);
                        completionCallback.onCompleted();
                    }
                });
            }
        });
    }

    // animation
    private void runAnimation(final View previousView, final View newView, int direction, AnimatorListenerAdapter animatorListenerAdapter) {
        Animator animator = createAnimator(previousView, newView, direction);
        animator.addListener(animatorListenerAdapter);
        animator.start();
    }

    protected abstract Animator createAnimator(@NonNull View previousView, @NonNull View newView, int direction);
}
