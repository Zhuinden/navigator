/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.navigator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

/**
 * You need to extend this class.
 *
 * In order to persist its state into a StateBundle, you need to implement Bundleable.
 *
 * In that case, it should also manage the View's state bundle, if the View is Bundleable.
 *
 * Created by Zhuinden on 2017.03.11..
 */
public abstract class ViewController
        implements Bundleable {
    private StateKey stateKey;

    public ViewController(StateKey stateKey) {
        this.stateKey = stateKey;
    }

    public <T extends StateKey> T getKey() {
        // noinspection unchecked
        return (T) stateKey;
    }

    protected abstract void onViewCreated(View view);

    protected abstract void onStateRestored(View view);

    protected void preSaveState(View view) {
    }

    protected abstract void onViewDestroyed(View view);

    protected void onActivityStarted() {
    }

    protected void onActivityResumed() {
    }

    protected void onActivityPaused() {
    }

    protected void onActivityStopped() {
    }

    public static <T extends ViewController> T get(View view) {
        // noinspection unchecked
        return (T) view.getTag(R.id.navigator_controller_id);
    }

    public static void bind(ViewController controller, View view) {
        view.setTag(R.id.navigator_controller_id, controller);
        controller.onViewCreated(view);
    }

    public static void unbind(View view) {
        ViewController controller = get(view);
        controller.onViewDestroyed(view);
        view.setTag(R.id.navigator_controller_id, null);
    }

    static void persistState(BackstackManager backstackManager, View view) {
        ViewController viewController = get(view);
        viewController.preSaveState(view);
        backstackManager.persistViewToState(view);
        backstackManager.getSavedState(viewController.getKey()).setBundle((viewController).toBundle());
    }

    static void restoreState(BackstackManager backstackManager, View view) {
        ViewController viewController = get(view);
        backstackManager.restoreViewFromState(view);
        viewController.fromBundle(backstackManager.getSavedState(viewController.getKey()).getBundle());
        viewController.onStateRestored(view);
    }

    protected void onSaveControllerState(@NonNull StateBundle bundle) {

    }

    protected void onRestoreControllerState(@NonNull StateBundle bundle) {
    }

    @NonNull
    @Override
    public final StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        onSaveControllerState(stateBundle);
        return stateBundle;
    }

    @Override
    public final void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            onRestoreControllerState(bundle);
        }
    }
}
