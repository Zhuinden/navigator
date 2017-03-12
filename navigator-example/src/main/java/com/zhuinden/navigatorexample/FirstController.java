package com.zhuinden.navigatorexample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.zhuinden.navigator.Navigator;
import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewController;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Zhuinden on 2017.03.10..
 */

public class FirstController
        extends ViewController
        implements Bundleable {
    public FirstController(StateKey stateKey) {
        super(stateKey);
    }

    @OnClick(R.id.first_button)
    public void firstButtonClick(View view) {
        Navigator.getBackstack(view.getContext()).goTo(SecondKey.create());
    }

    Unbinder unbinder;

    @Override
    public void onViewCreated(View view) {
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    protected void onViewRestored(View view) {
        Log.i("FIRST", "Restored state of [" + view + "]");
    }

    @Override
    public void onViewDestroyed(View view) {
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void preViewSaveState(View view) {
        Log.i("FIRST", "Saving state of [" + view + "]");
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
            Log.i("FIRST", bundle.getString("HELLO"));
        }
    }
}
