package com.zhuinden.navigatorexample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewController;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.03.10..
 */

public class SecondController
        extends ViewController
        implements Bundleable {
    Unbinder unbinder;

    public SecondController(StateKey stateKey) {
        super(stateKey);
    }

    @Override
    public void attach(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void detach(View view) {
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putString("HELLO", "WORLD");
        return stateBundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            Log.i("SECOND", bundle.getString("HELLO"));
        }
    }
}
