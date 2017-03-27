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

    private static final NoOpViewChangeHandler NO_OP_VIEW_CHANGE_HANDLER = new NoOpViewChangeHandler();

    private Context baseContext;
    private ViewGroup container;
    private StateChanger externalStateChanger;

    public DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container) {
        this(baseContext, container, null);
    }

    public DefaultStateChanger(@NonNull Context baseContext, @NonNull ViewGroup container, @Nullable StateChanger externalStateChanger) {
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
    }

    private void finishStateChange(Callback completionCallback) {
        completionCallback.stateChangeComplete();
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
                    finishStateChange(completionCallback);
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
                                    finishStateChange(completionCallback);
                                }
                            });
                }
            }
        });
    }
}
