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
        return (T) stateKey;
    }

    public abstract void attach(View view);

    public abstract void detach(View view);

    public static <T extends ViewController> T get(View view) {
        // noinspection unchecked
        return (T) view.getTag(R.id.navigator_controller_id);
    }

    public static void bind(ViewController controller, View view) {
        view.setTag(R.id.navigator_controller_id, controller);
    }

    public static void unbind(View view) {
        view.setTag(R.id.navigator_controller_id, null);
    }
}
