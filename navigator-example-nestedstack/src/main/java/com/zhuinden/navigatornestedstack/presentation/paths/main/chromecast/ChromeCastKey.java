package com.zhuinden.navigatornestedstack.presentation.paths.main.chromecast;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.presentation.paths.main.MainView;

/**
 * Created by Owner on 2017. 01. 12..
 */

@AutoValue
public abstract class ChromeCastKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_chromecast;
    }

    public static ChromeCastKey create() {
        return new AutoValue_ChromeCastKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.CHROMECAST.name();
    }
}
