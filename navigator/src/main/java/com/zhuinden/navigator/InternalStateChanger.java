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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigator.changehandlers.NoOpViewChangeHandler;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Zhuinden on 2017.03.11..
 */

class InternalStateChanger
        implements StateChanger {
    static class NoOpStateChanger
            implements StateChanger {
        @Override
        public void handleStateChange(StateChange stateChange, Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    }

    private Context baseContext;
    private StateChanger externalStateChanger;
    private ViewGroup container;

    InternalStateChanger(Context baseContext, StateChanger externalStateChanger, ViewGroup container) {
        this.baseContext = baseContext;
        this.externalStateChanger = externalStateChanger;
        this.container = container;
    }

    @Override
    public void handleStateChange(final StateChange stateChange, final Callback completionCallback) {
        externalStateChanger.handleStateChange(stateChange, new Callback() {
            @Override
            public void stateChangeComplete() {
                if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
                    completionCallback.stateChangeComplete();
                    return;
                }
                StateKey previousKey = stateChange.topPreviousState();
                final View previousView = container.getChildAt(0);
                if(previousView != null && previousKey != null) {
                    Navigator.persistState(previousView);
                }
                StateKey newKey = stateChange.topNewState();
                Context newContext = stateChange.createContext(baseContext, newKey);
                final View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), container, false);
                Navigator.restoreState(newView);

                if(previousView == null) {
                    container.addView(newView);
                    finishStateChange(completionCallback);
                } else {
                    final ViewChangeHandler viewChangeHandler;
                    if(stateChange.getDirection() == StateChange.FORWARD) {
                        viewChangeHandler = newKey.getViewChangeHandler();
                    } else if(previousKey != null && stateChange.getDirection() == StateChange.BACKWARD) {
                        viewChangeHandler = previousKey.getViewChangeHandler();
                    } else {
                        viewChangeHandler = new NoOpViewChangeHandler();
                    }
                    viewChangeHandler.performViewChange(container,
                            previousView,
                            newView,
                            stateChange.getDirection(),
                            new ViewChangeHandler.CompletionCallback() {
                                @Override
                                public void onCompleted() {
                                    finishStateChange(completionCallback);
                                }
                            });
                }
            }
        });
    }

    private void finishStateChange(Callback completionCallback) {
        completionCallback.stateChangeComplete();
    }
}
