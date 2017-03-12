package com.zhuinden.navigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigator.changehandlers.NoOpViewChangeHandler;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class InternalStateChanger
        implements StateChanger {
    BaseContextProvider baseContextProvider;
    BackstackManager backstackManager;
    ViewGroup container;

    public InternalStateChanger(BaseContextProvider baseContextProvider, BackstackManager backstackManager, ViewGroup container) {
        this.baseContextProvider = baseContextProvider;
        this.backstackManager = backstackManager;
        this.container = container;
    }

    @Override
    public void handleStateChange(final StateChange stateChange, final Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        StateKey previousKey = stateChange.topPreviousState();
        final View previousView = container.getChildAt(0);
        if(previousView != null && previousKey != null) {
            ViewController.persistState(backstackManager, previousView);
            ViewController.unbind(previousView);
        }

        StateKey newKey = stateChange.topNewState();
        ViewController newController = newKey.provideViewController();
        Context newContext = stateChange.createContext(baseContextProvider.getBaseContext(), newKey);
        final View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), container, false);
        ViewController.bind(newController, newView);
        ViewController.restoreState(backstackManager, newView);

        if(previousView == null) {
            container.addView(newView);
            finishStateChange(completionCallback);
        } else {
            final ViewChangeHandler viewChangeHandler;
            if(stateChange.getDirection() == StateChange.FORWARD) {
                viewChangeHandler = newKey.getAnimationHandler();
            } else if(previousKey != null && stateChange.getDirection() == StateChange.BACKWARD) {
                viewChangeHandler = previousKey.getAnimationHandler();
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

    private void finishStateChange(Callback completionCallback) {
        completionCallback.stateChangeComplete();
    }
}
