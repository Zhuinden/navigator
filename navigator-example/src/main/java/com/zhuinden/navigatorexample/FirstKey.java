package com.zhuinden.navigatorexample;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.ViewController;

/**
 * Created by Zhuinden on 2017.03.10..
 */
@AutoValue
public abstract class FirstKey
        extends StateTitleKey
        implements Parcelable {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public int layout() {
        return R.layout.fragment_first;
    }

    @Override
    public ViewController provideViewController(Object... args) {
        return new FirstController(this);
    }

    @Override
    public ViewChangeHandler getAnimationHandler() {
        return new TransitionHandler();
    }

    @Override
    public String getTitle() {
        return "First";
    }
}
