package com.zhuinden.navigator;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.statebundle.StateBundle;

import java.util.Collections;
import java.util.List;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class BackstackHost
        extends Fragment {
    public BackstackHost() {
        setRetainInstance(true);
    }

    BackstackManager backstackManager;

    StateChanger stateChanger = new StateChanger() {
        @Override
        public void handleStateChange(StateChange stateChange, StateChanger.Callback completionCallback) {
            if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
                completionCallback.stateChangeComplete();
                return;
            }
            StateKey previousKey = stateChange.topPreviousState();
            View previousView = container.getChildAt(0);
            backstackManager.persistViewToState(previousView);
            ViewController previousController;
            if(previousView != null && previousKey != null) {
                com.zhuinden.simplestack.SavedState savedState = backstackManager.getSavedState(previousKey);
                previousController = ViewController.get(previousView);
                if(previousController instanceof Bundleable) {
                    savedState.setBundle(((Bundleable) previousController).toBundle());
                }
                previousController.detach(previousView);
            }
            container.removeView(previousView);
            StateKey newKey = stateChange.topNewState();
            ViewController newController = newKey.createViewController();
            Context newContext = stateChange.createContext(getActivity(), newKey);
            View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), container, false);
            backstackManager.restoreViewFromState(newView);
            ViewController.bind(newController, newView);
            if(newController instanceof Bundleable) {
                ((Bundleable) newController).fromBundle(backstackManager.getSavedState(newKey).getBundle());
            }
            container.addView(newView);
            newController.attach(newView);
            completionCallback.stateChangeComplete();
        }
    };

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
}
