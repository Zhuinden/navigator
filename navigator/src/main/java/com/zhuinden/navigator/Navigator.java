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
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.DefaultKeyParceler;
import com.zhuinden.simplestack.DefaultStateClearStrategy;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.StateChanger;

import java.util.List;

public class Navigator {
    public static class Installer {
        StateChanger stateChanger = new InternalStateChanger.NoOpStateChanger();
        BackstackManager.StateClearStrategy stateClearStrategy = new DefaultStateClearStrategy();
        KeyParceler keyParceler = new DefaultKeyParceler();
        boolean isInitializeDeferred = false;

        public Installer setStateChanger(@NonNull StateChanger stateChanger) {
            if(stateChanger == null) {
                throw new IllegalArgumentException("If set, StateChanger cannot be null!");
            }
            this.stateChanger = stateChanger;
            return this;
        }

        public Installer setKeyParceler(@NonNull KeyParceler keyParceler) {
            if(keyParceler == null) {
                throw new IllegalArgumentException("If set, KeyParceler cannot be null!");
            }
            this.keyParceler = keyParceler;
            return this;
        }

        public Installer setStateClearStrategy(@NonNull BackstackManager.StateClearStrategy stateClearStrategy) {
            if(stateClearStrategy == null) {
                throw new IllegalArgumentException("If set, StateClearStrategy cannot be null!");
            }
            this.stateClearStrategy = stateClearStrategy;
            return this;
        }

        public Installer setDeferredInitialization(boolean isInitializeDeferred) {
            this.isInitializeDeferred = isInitializeDeferred;
            return this;
        }

        public void install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
            Navigator.install(this, activity, container, initialKeys);
        }
    }

    private Navigator() {
    }

    public static Installer configure() {
        return new Installer();
    }

    public static void install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
        install(configure(), activity, container, initialKeys);
    }

    private static void install(Installer installer, @NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
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
        backstackHost.externalStateChanger = installer.stateChanger;
        backstackHost.keyParceler = installer.keyParceler;
        backstackHost.stateClearStrategy = installer.stateClearStrategy;
        backstackHost.initialKeys = initialKeys;
        backstackHost.container = container;
        backstackHost.initialize(installer.isInitializeDeferred);
    }

    public static void executeDeferredInitialization(Activity activity) {
        BackstackHost backstackHost = findBackstackHost(activity);
        backstackHost.initialize(false);
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

    public static Backstack getBackstack(Context context) {
        Activity activity = findActivity(context);
        BackstackHost backstackHost = findBackstackHost(activity);
        return backstackHost.getBackstack();
    }

    public static boolean onBackPressed(Activity activity) {
        return getBackstack(activity).goBack();
    }
}
