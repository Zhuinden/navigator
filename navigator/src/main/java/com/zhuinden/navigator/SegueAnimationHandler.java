package com.zhuinden.navigator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class SegueAnimationHandler
        implements AnimationHandler {
    @Override
    public void runAnimation(@NonNull View previousView, @NonNull View newView, int direction, @NonNull final CompletionListener completionListener) {
        runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                completionListener.onCompleted();
            }
        });
    }

    // animation
    private void runAnimation(final View previousView, final View newView, int direction, AnimatorListenerAdapter animatorListenerAdapter) {
        Animator animator = createSegue(previousView, newView, direction);
        animator.addListener(animatorListenerAdapter);
        animator.start();
    }

    private Animator createSegue(View from, View to, int direction) {
        int fromTranslation = (-1) * direction * from.getWidth();
        int toTranslation = direction * to.getWidth();

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(from, "translationX", fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, "translationX", toTranslation, 0));
        return set;
    }
}
