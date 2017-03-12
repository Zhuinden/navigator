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
package com.zhuinden.navigator.changehandlers;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigator.ViewChangeHandler;

/**
 * Created by Zhuinden on 2017.03.11..
 */
public final class NoOpViewChangeHandler
        implements ViewChangeHandler {
    @Override
    public void performViewChange(@NonNull ViewGroup container, @NonNull View previousView, @NonNull View newView, int direction, @NonNull CompletionCallback completionCallback) {
        container.removeView(previousView);
        container.addView(newView);
        completionCallback.onCompleted();
    }
}
