package com.zhuinden.navigatornestedstack.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.View;

import com.zhuinden.navigator.changehandlers.AnimatorViewChangeHandler;

/**
 * Created by Zhuinden on 2017.03.27..
 */

public class FadeAnimationHandler
        extends AnimatorViewChangeHandler {
    @Override
    protected Animator createAnimator(@NonNull View previousView, @NonNull View newView, int direction) {
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(previousView, "alpha", 1, 0));
        set.play(ObjectAnimator.ofFloat(newView, "alpha", 0, 1));
        return set;
    }
}
