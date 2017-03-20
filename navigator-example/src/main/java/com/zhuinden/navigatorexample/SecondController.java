package com.zhuinden.navigatorexample;

import android.support.annotation.NonNull;
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
    public void onViewCreated(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    protected void onStateRestored(View view) {
        Log.i("SECOND", "Restored state of [" + view + "]");
    }

    @Override
    public void onViewDestroyed(View view) {
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void preSaveState(View view) {
        Log.i("SECOND", "Saving state of [" + view + "]");
    }

    @Override
    protected void onSaveControllerState(@NonNull StateBundle bundle) {
        bundle.putString("HELLO", "WORLD");
    }

    @Override
    protected void onRestoreControllerState(@NonNull StateBundle bundle) {
        Log.i("SECOND", bundle.getString("HELLO"));
    }
}
