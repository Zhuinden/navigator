package com.zhuinden.navigator;

import android.view.View;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public abstract class ViewController {
    StateKey stateKey;

    public ViewController(StateKey stateKey) {
        this.stateKey = stateKey;
    }

    public <T extends StateKey> T getKey() {
        // noinspection unchecked
        return (T)stateKey;
    }

    public abstract void attach(View view);
    public abstract void detach(View view);

    public static <T extends ViewController> T get(View view) {
        // noinspection unchecked
        return (T)view.getTag(R.id.navigator_controller_id);
    }

    public static void bind(ViewController controller, View view) {
        view.setTag(R.id.navigator_controller_id, controller);
    }

    public static void unbind(View view) {
        view.setTag(R.id.navigator_controller_id, null);
    }
}
