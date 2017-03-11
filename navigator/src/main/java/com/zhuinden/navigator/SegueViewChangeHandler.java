package com.zhuinden.navigator;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class SegueViewChangeHandler
        extends ViewAnimatorChangeHandler {
    @Override
    protected Animator createAnimator(@NonNull View from, @NonNull View to, int direction) {
        int fromTranslation = (-1) * direction * from.getWidth();
        int toTranslation = direction * to.getWidth();

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(from, "translationX", fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, "translationX", toTranslation, 0));
        return set;
    }
}
