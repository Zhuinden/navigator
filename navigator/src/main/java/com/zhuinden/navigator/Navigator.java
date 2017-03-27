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


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.DefaultKeyParceler;
import com.zhuinden.simplestack.DefaultStateClearStrategy;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.StateChanger;

import java.util.List;

/**
 * Convenience class to hide lifecycle integration using retained fragment.
 * Essentially, a replacement for BackstackDelegate.
 *
 * It can be either configured via {@link Navigator#configure()}, or installed with default settings using {@link Navigator#install(Activity, ViewGroup, List)}.
 */
public class Navigator {
    /**
     * A configurer for {@link Navigator}.
     */
    public static class Installer {
        StateChanger stateChanger;
        BackstackManager.StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();
        KeyParceler keyParceler = new DefaultKeyParceler();
        boolean isInitializeDeferred = false;

        /**
         * Sets the state changer used by the navigator's backstack.
         *
         * If not set, then {@link DefaultStateChanger} is used, which requires keys to be {@link StateKey}.
         *
         * @param stateChanger if set, cannot be null.
         * @return the installer
         */
        public Installer setStateChanger(@NonNull StateChanger stateChanger) {
            if(stateChanger == null) {
                throw new IllegalArgumentException("If set, StateChanger cannot be null!");
            }
            this.stateChanger = stateChanger;
            return this;
        }

        /**
         * Sets the key parceler for parcelling state keys.
         *
         * @param keyParceler cannot be null if set
         * @return the installer
         */
        public Installer setKeyParceler(@NonNull KeyParceler keyParceler) {
            if(keyParceler == null) {
                throw new IllegalArgumentException("If set, KeyParceler cannot be null!");
            }
            this.keyParceler = keyParceler;
            return this;
        }

        /**
         * Sets the state clear strategy used to clear the stored state in BackstackManager after there are no queued state changes left.
         *
         * @param stateClearStrategy if set, it cannot be null
         * @return the installer
         */
        public Installer setStateClearStrategy(@NonNull BackstackManager.StateClearStrategy stateClearStrategy) {
            if(stateClearStrategy == null) {
                throw new IllegalArgumentException("If set, StateClearStrategy cannot be null!");
            }
            this.stateClearStrategy = stateClearStrategy;
            return this;
        }

        /**
         * Sets if after initialization, the state changer should only be set when {@link Navigator#executeDeferredInitialization(Context)} is called.
         * Typically needed to setup the backstack for dependency injection module.
         *
         * @param isInitializeDeferred if call to executing deferred initialization is needed
         * @return the installer
         */
        public Installer setDeferredInitialization(boolean isInitializeDeferred) {
            this.isInitializeDeferred = isInitializeDeferred;
            return this;
        }

        /**
         * Installs the {@link BackstackHost}.
         *
         * @param activity    the activity
         * @param container   the container
         * @param initialKeys the initial keys.
         * @return
         */
        public Backstack install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
            if(stateChanger == null) {
                stateChanger = new DefaultStateChanger(activity, container);
            }
            return Navigator.install(this, activity, container, initialKeys);
        }
    }

    private Navigator() {
    }

    /**
     * Creates an {@link Installer} to configure the {@link Navigator}.
     *
     * @return the installer
     */
    public static Installer configure() {
        return new Installer();
    }

    /**
     * Installs the {@link Navigator} with default parameters.
     *
     * This means that {@link DefaultStateChanger} and DefaultStateClearStrategy are used.
     *
     * @param activity the activity which will host the backstack
     * @param container the container in which custom viewgroups are hosted (to save its child's state in onSaveInstanceState())
     * @param initialKeys the keys used to initialize the backstack
     */
    public static void install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
        install(configure(), activity, container, initialKeys);
    }

    private static Backstack install(Installer installer, @NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
        if(activity == null) {
            throw new IllegalArgumentException("Activity cannot be null!");
        }
        if(container == null) {
            throw new IllegalArgumentException("State changer cannot be null!");
        }
        if(initialKeys == null || initialKeys.isEmpty()) {
            throw new IllegalArgumentException("Initial keys cannot be null!");
        }
        BackstackHost backstackHost = findBackstackHost(activity);
        if(backstackHost == null) {
            backstackHost = new BackstackHost();
            activity.getFragmentManager().beginTransaction().add(backstackHost, "NAVIGATOR_BACKSTACK_HOST").commit();
            activity.getFragmentManager().executePendingTransactions();
        }
        backstackHost.stateChanger = installer.stateChanger;
        backstackHost.keyParceler = installer.keyParceler;
        backstackHost.stateClearStrategy = installer.stateClearStrategy;
        backstackHost.initialKeys = initialKeys;
        backstackHost.container = container;
        return backstackHost.initialize(installer.isInitializeDeferred);
    }

    /**
     * If {@link Installer#setDeferredInitialization(boolean)} was set to true, then this will initialize the backstack using the state changer.
     *
     * @param context the context to which an activity belongs that hosts the backstack
     */
    public static void executeDeferredInitialization(Context context) {
        Activity activity = findActivity(context);
        BackstackHost backstackHost = findBackstackHost(activity);
        backstackHost.initialize(false);
    }

    /**
     * Gets the backstack that belongs to the Activity which hosts the backstack.
     *
     * @param context the context
     * @return the backstack
     */
    public static Backstack getBackstack(Context context) {
        BackstackHost backstackHost = getBackstackHost(context);
        return backstackHost.getBackstack();
    }

    /**
     * Delegates back press call to the backstack of the navigator.
     *
     * @param context the Context that belongs to an Activity which hosts the backstack.
     * @return true if a state change was handled or is in progress, false otherwise
     */
    public static boolean onBackPressed(Context context) {
        return getBackstack(context).goBack();
    }

    /**
     * Persists the view hierarchy state and optional StateBundle.
     *
     * @param view the view (can be Bundleable)
     */
    public static void persistViewToState(@Nullable View view) {
        if(view != null) {
            Context context = view.getContext();
            BackstackHost backstackHost = getBackstackHost(context);
            backstackHost.backstackManager.persistViewToState(view);
        }
    }

    /**
     * Restores the view hierarchy state and optional StateBundle.
     *
     * @param view the view (can be Bundleable)
     */
    public static void restoreViewFromState(@NonNull View view) {
        if(view == null) {
            throw new NullPointerException("You cannot restore state into null view!");
        }
        Context context = view.getContext();
        BackstackHost backstackHost = getBackstackHost(context);
        backstackHost.backstackManager.restoreViewFromState(view);
    }

    /**
     * Get the saved state for a given key.
     *
     * @param context the context to which an Activity belongs that hosts a backstack
     * @param key     the key
     * @return the saved state
     */
    public static SavedState getSavedState(@NonNull Context context, @NonNull Object key) {
        if(context == null) {
            throw new NullPointerException("context cannot be null");
        }
        if(key == null) {
            throw new NullPointerException("key cannot be null");
        }
        BackstackHost backstackHost = getBackstackHost(context);
        return backstackHost.backstackManager.getSavedState(key);
    }

    private static BackstackHost findBackstackHost(Activity activity) {
        return (BackstackHost) activity.getFragmentManager().findFragmentByTag("NAVIGATOR_BACKSTACK_HOST");
    }

    private static Activity findActivity(Context context) {
        if(context instanceof Activity) {
            return (Activity) context;
        } else {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            Context baseContext = contextWrapper.getBaseContext();
            if(baseContext == null) {
                throw new IllegalStateException("Activity was not found as base context of view!");
            }
            return findActivity(baseContext);
        }
    }

    private static BackstackHost getBackstackHost(Context context) {
        Activity activity = findActivity(context);
        return findBackstackHost(activity);
    }
}
