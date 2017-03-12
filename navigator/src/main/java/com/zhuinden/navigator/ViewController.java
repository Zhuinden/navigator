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

import android.view.View;

import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;

/**
 * You need to extend this class.
 *
 * In order to persist its state into a StateBundle, you need to implement Bundleable.
 *
 * In that case, it should also manage the View's state bundle, if the View is Bundleable.
 *
 * Created by Zhuinden on 2017.03.11..
 */
public abstract class ViewController {
    private StateKey stateKey;

    public ViewController(StateKey stateKey) {
        this.stateKey = stateKey;
    }

    public <T extends StateKey> T getKey() {
        // noinspection unchecked
        return (T) stateKey;
    }

    protected abstract void onViewCreated(View view);

    protected abstract void onViewRestored(View view);

    protected void preViewSaveState(View view) {
    }

    protected abstract void onViewDestroyed(View view);

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
        viewController.preViewSaveState(view);
        backstackManager.persistViewToState(view);
        if(viewController instanceof Bundleable) {
            backstackManager.getSavedState(viewController.getKey()).setBundle(((Bundleable) viewController).toBundle());
        }
    }

    static void restoreState(BackstackManager backstackManager, View view) {
        ViewController viewController = get(view);
        backstackManager.restoreViewFromState(view);
        if(viewController instanceof Bundleable) {
            ((Bundleable) viewController).fromBundle(backstackManager.getSavedState(viewController.getKey()).getBundle());
        }
        viewController.onViewRestored(view);
    }
}
