package com.zhuinden.navigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.SavedState;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public class NavigatorStateChanger
        implements StateChanger {
    BaseContextProvider baseContextProvider;
    BackstackManager backstackManager;
    ViewGroup container;

    public NavigatorStateChanger(BaseContextProvider baseContextProvider, BackstackManager backstackManager, ViewGroup container) {
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
        backstackManager.persistViewToState(previousView);
        ViewController previousController;
        if(previousView != null && previousKey != null) {
            SavedState savedState = backstackManager.getSavedState(previousKey);
            previousController = ViewController.get(previousView);
            if(previousController instanceof Bundleable) {
                savedState.setBundle(((Bundleable) previousController).toBundle());
            }
            previousController.detach(previousView);
        }

        StateKey newKey = stateChange.topNewState();
        ViewController newController = newKey.createViewController();
        Context newContext = stateChange.createContext(baseContextProvider.getBaseContext(), newKey);
        final View newView = LayoutInflater.from(newContext).inflate(newKey.layout(), container, false);
        backstackManager.restoreViewFromState(newView);
        ViewController.bind(newController, newView);
        if(newController instanceof Bundleable) {
            ((Bundleable) newController).fromBundle(backstackManager.getSavedState(newKey).getBundle());
        }
        container.addView(newView);
        newController.attach(newView);

        if(previousView == null) {
            finishStateChange(completionCallback);
            return;
        } else {
            final AnimationHandler animationHandler;
            if(stateChange.getDirection() == StateChange.FORWARD) {
                animationHandler = newKey.getAnimationHandler();
            } else if(previousKey != null && stateChange.getDirection() == StateChange.BACKWARD) {
                animationHandler = previousKey.getAnimationHandler();
            } else {
                animationHandler = new NoOpAnimationHandler();
            }
            ViewUtils.waitForMeasure(newView, new ViewUtils.OnMeasuredCallback() {
                @Override
                public void onMeasured(View view, int width, int height) {
                    animationHandler.runAnimation(previousView,
                            newView,
                            stateChange.getDirection(),
                            new AnimationHandler.CompletionListener() {
                                @Override
                                public void onCompleted() {
                                    container.removeView(previousView);
                                    finishStateChange(completionCallback);
                                }
                            });
                }
            });
        }
    }

    private void finishStateChange(Callback completionCallback) {
        completionCallback.stateChangeComplete();
    }
}
