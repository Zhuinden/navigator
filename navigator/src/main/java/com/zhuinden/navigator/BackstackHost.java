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

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.KeyParceler;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.statebundle.StateBundle;

import java.util.Collections;
import java.util.List;

/**
 * This is public because it has to be.
 *
 * Created by Zhuinden on 2017.03.11..
 */
public final class BackstackHost
        extends Fragment
        implements BaseContextProvider {

    public BackstackHost() {
        setRetainInstance(true);
    }

    StateChanger externalStateChanger;
    KeyParceler keyParceler;
    BackstackManager.StateClearStrategy stateClearStrategy;
    LayoutInflationStrategy layoutInflationStrategy;

    BackstackManager backstackManager;

    InternalStateChanger internalStateChanger;

    List<Object> initialKeys = Collections.emptyList(); // should not stay empty list
    ViewGroup container;

    Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    void initialize(boolean isInitializeDeferred) {
        if(backstackManager == null) {
            backstackManager = new BackstackManager();
            backstackManager.setKeyParceler(keyParceler);
            backstackManager.setStateClearStrategy(stateClearStrategy);
            backstackManager.setup(initialKeys);
            if(savedInstanceState != null) {
                backstackManager.fromBundle(savedInstanceState.<StateBundle>getParcelable("NAVIGATOR_STATE_BUNDLE"));
            }
        }
        if(!isInitializeDeferred) {
            internalStateChanger = new InternalStateChanger(this,
                    layoutInflationStrategy,
                    externalStateChanger,
                    backstackManager,
                    container);
            backstackManager.setStateChanger(internalStateChanger);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ViewController.persistState(backstackManager, container.getChildAt(0));
        outState.putParcelable("NAVIGATOR_STATE_BUNDLE", backstackManager.toBundle());
    }

    @Override
    public void onStart() {
        super.onStart();
        currentViewController().onActivityStarted();
    }

    @Override
    public void onResume() {
        super.onResume();
        backstackManager.reattachStateChanger();
        currentViewController().onActivityResumed();
    }

    @Override
    public void onPause() {
        currentViewController().onActivityPaused();
        backstackManager.detachStateChanger();
        super.onPause();
    }

    @Override
    public void onStop() {
        currentViewController().onActivityStopped();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        backstackManager.getBackstack().executePendingStateChange();
        ViewController.unbind(container.getChildAt(0));
        internalStateChanger = null;
        container = null;
        super.onDestroyView();
    }

    private ViewController currentViewController() {
        return ViewController.get(container.getChildAt(0));
    }

    public Backstack getBackstack() {
        return backstackManager.getBackstack();
    }

    @Override
    public Context getBaseContext() {
        return getActivity();
    }
}
