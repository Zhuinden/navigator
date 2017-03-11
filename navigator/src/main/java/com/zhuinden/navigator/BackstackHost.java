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
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import java.util.Collections;
import java.util.List;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class BackstackHost
        extends Fragment
        implements BaseContextProvider {
    public BackstackHost() {
        setRetainInstance(true);
    }

    BackstackManager backstackManager;

    NavigatorStateChanger stateChanger;

    List<Object> initialKeys = Collections.emptyList(); // should not stay empty list
    ViewGroup container;

    Bundle savedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    public void initialize() {
        if(backstackManager == null) {
            backstackManager = new BackstackManager();
            backstackManager.setup(initialKeys);
            if(savedInstanceState != null) {
                backstackManager.fromBundle(savedInstanceState.<StateBundle>getParcelable("NAVIGATOR_STATE_BUNDLE"));
            }
            stateChanger = new NavigatorStateChanger(this, backstackManager, container);
        }
        backstackManager.setStateChanger(stateChanger);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackManager.persistViewToState(container.getChildAt(0));
        ViewController viewController = (ViewController) container.getChildAt(0).getTag(R.id.navigator_controller_id);
        if(viewController instanceof Bundleable) {
            backstackManager.getSavedState(viewController.getKey()).setBundle(((Bundleable) viewController).toBundle());
        }
        outState.putParcelable("NAVIGATOR_STATE_BUNDLE", backstackManager.toBundle());
    }

    @Override
    public void onResume() {
        super.onResume();
        backstackManager.reattachStateChanger();
    }

    @Override
    public void onPause() {
        backstackManager.detachStateChanger();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ViewController viewController = (ViewController) container.getChildAt(0).getTag(R.id.navigator_controller_id);
        viewController.detach(container.getChildAt(0));
        backstackManager.getBackstack().executePendingStateChange();
        container = null;
        super.onDestroyView();
    }

    public Backstack getBackstack() {
        return backstackManager.getBackstack();
    }

    @Override
    public Context getBaseContext() {
        return getActivity();
    }
}
