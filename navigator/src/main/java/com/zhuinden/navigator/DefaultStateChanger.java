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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigator.changehandlers.NoOpViewChangeHandler;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * A default state changer that handles view changes, and allows an optional external state changer (which is executed before the view change).
 *
 * To work, all keys must implement {@link StateKey}, which specifies a layout, and a {@link ViewChangeHandler}.
 */
public class DefaultStateChanger
        implements StateChanger {
    private static class NoOpStateChanger
            implements StateChanger {
        @Override
        public void handleStateChange(StateChange stateChange, Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    }

    private static class NoOpViewChangeCompletionListener implements ViewChangeCompletionListener {
        @Override
        public void handleViewChangeComplete(@NonNull StateChange stateChange, @NonNull ViewGroup container, @Nullable View previousView, @NonNull View newView, @NonNull Callback completionCallback) {
            completionCallback.viewChangeComplete();
        }
    }

    /**
     * Allows the possibility of listening to when the view change is completed.
     */
    public interface ViewChangeCompletionListener {
        /**
         * Notifies the {@link DefaultStateChanger} that the view change completion listener completed its callback.
         */
        public interface Callback {
            void viewChangeComplete();
        }

        /**
         * Called when a view change is completed.
         *
         * @param stateChange the state change
         * @param container the container
         * @param previousView the previous view
         * @param newView the new view
         * @param completionCallback the completion callback that must be called once the view change callback is completed by the listener
         */
        void handleViewChangeComplete(@NonNull StateChange stateChange, @NonNull ViewGroup container, @Nullable View previousView, @NonNull View newView, @NonNull Callback completionCallback);
    }

    private static final NoOpViewChangeHandler NO_OP_VIEW_CHANGE_HANDLER = new NoOpViewChangeHandler();

    private Context baseContext;
    private ViewGroup container;
    private StateChanger externalStateChanger;
    private ViewChangeCompletionListener viewChangeCompletionListener;

    public DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container) {
        this(baseContext, container, null, null);
    }

    public DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container, @Nullable StateChanger externalStateChanger) {
        this(baseContext, container, externalStateChanger, null);
    }

    public DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container, @Nullable StateChanger externalStateChanger, @Nullable ViewChangeCompletionListener viewChangeCompletionListener) {
        if(baseContext == null) {
            throw new NullPointerException("baseContext cannot be null");
        }
        if(container == null) {
            throw new NullPointerException("container cannot be null");
        }
        this.baseContext = baseContext;
        this.container = container;
        if(externalStateChanger == null) {
            externalStateChanger = new NoOpStateChanger();
        }
        this.externalStateChanger = externalStateChanger;
        if(viewChangeCompletionListener == null) {
            viewChangeCompletionListener = new NoOpViewChangeCompletionListener();
        }
        this.viewChangeCompletionListener = viewChangeCompletionListener;
    }

    private void finishStateChange(StateChange stateChange, ViewGroup container, View previousView, View newView, final Callback completionCallback) {
        viewChangeCompletionListener.handleViewChangeComplete(stateChange, container, previousView, newView, new ViewChangeCompletionListener.Callback() {
            @Override
            public void viewChangeComplete() {
                completionCallback.stateChangeComplete();
            }
        });
    }

    @Override
    public final void handleStateChange(final StateChange stateChange, final Callback completionCallback) {
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
                    Navigator.persistViewToState(previousView);
                }
                StateKey newKey = stateChange.topNewState();
                Context newContext = stateChange.createContext(baseContext, newKey);
                final View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), container, false);
                Navigator.restoreViewFromState(newView);

                if(previousView == null) {
                    container.addView(newView);
                    finishStateChange(stateChange, container, previousView, newView, completionCallback);
                } else {
                    final ViewChangeHandler viewChangeHandler;
                    if(stateChange.getDirection() == StateChange.FORWARD) {
                        viewChangeHandler = newKey.viewChangeHandler();
                    } else if(previousKey != null && stateChange.getDirection() == StateChange.BACKWARD) {
                        viewChangeHandler = previousKey.viewChangeHandler();
                    } else {
                        viewChangeHandler = NO_OP_VIEW_CHANGE_HANDLER;
                    }
                    viewChangeHandler.performViewChange(container,
                            previousView,
                            newView,
                            stateChange.getDirection(),
                            new ViewChangeHandler.CompletionCallback() {
                                @Override
                                public void onCompleted() {
                                    finishStateChange(stateChange, container, previousView, newView, completionCallback);
                                }
                            });
                }
            }
        });
    }
}
