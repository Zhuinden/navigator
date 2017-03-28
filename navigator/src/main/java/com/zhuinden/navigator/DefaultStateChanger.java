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
public final class DefaultStateChanger
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

    /**
     * Used to configure the instance of the {@link DefaultStateChanger}.
     *
     * Allows setting an external state changer, which is executed before the view change.
     * Also allows setting a {@link ViewChangeCompletionListener} which is executed after the view change.
     */
    public static class Configurer {
        StateChanger externalStateChanger = null;
        ViewChangeCompletionListener viewChangeCompletionListener = null;

        private Configurer() {
        }

        /**
         * Sets the external state changer. It is executed before the view change.
         *
         * @param stateChanger the state changer
         * @return the configurer
         */
        public Configurer setExternalStateChanger(@NonNull StateChanger stateChanger) {
            if(stateChanger == null) {
                throw new NullPointerException("If set, external state changer cannot be null!");
            }
            this.externalStateChanger = stateChanger;
            return this;
        }

        /**
         * Sets the {@link ViewChangeCompletionListener}. It is executed after the view change.
         *
         * @param viewChangeCompletionListener the view change completion listener
         * @return the configurer
         */
        public Configurer setViewChangeCompletionListener(@NonNull ViewChangeCompletionListener viewChangeCompletionListener) {
            if(viewChangeCompletionListener == null) {
                throw new NullPointerException("If set, view change completion listener cannot be null!");
            }
            this.viewChangeCompletionListener = viewChangeCompletionListener;
            return this;
        }

        /**
         * Creates the {@link DefaultStateChanger} with the specified parameters.
         *
         * @param baseContext the base context used to inflate the views
         * @param container   the container into which views are added and removed from
         * @return the new {@link DefaultStateChanger}
         */
        public DefaultStateChanger create(Context baseContext, ViewGroup container) {
            return new DefaultStateChanger(baseContext, container, externalStateChanger, viewChangeCompletionListener);
        }
    }

    /**
     * Factory method to create a configured {@link DefaultStateChanger}.
     * You can set an external state changer which is executed before the view change, and a {@link ViewChangeCompletionListener} that is executed after view change.
     *
     * @return the {@link Configurer}
     */
    public static Configurer configure() {
        return new Configurer();
    }

    /**
     * Factory method to create the {@link DefaultStateChanger} with default configuration.
     *
     * To add additional configuration such as external state changer or {@link ViewChangeCompletionListener}, use the {@link DefaultStateChanger#configure()} method.
     *
     * @param baseContext the base context used to inflate views
     * @param container   the container into which views are added to or removed from
     * @return the state changer
     */
    public static DefaultStateChanger create(Context baseContext, ViewGroup container) {
        return new DefaultStateChanger(baseContext, container);
    }

    DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container) {
        this(baseContext, container, null, null);
    }

    DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container, @Nullable StateChanger externalStateChanger, @Nullable ViewChangeCompletionListener viewChangeCompletionListener) {
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
