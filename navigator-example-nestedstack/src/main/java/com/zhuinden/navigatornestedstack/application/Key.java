package com.zhuinden.navigatornestedstack.application;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.zhuinden.navigator.StateKey;
import com.zhuinden.navigator.ViewChangeHandler;
import com.zhuinden.navigator.changehandlers.SegueViewChangeHandler;
import com.zhuinden.servicetree.ServiceTree;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.navigatornestedstack.util.ServiceLocator;

import java.util.Collections;
import java.util.List;

/**
 * Created by Owner on 2017. 01. 12..
 */

public abstract class Key
        implements Parcelable, StateKey {
    public static final String NESTED_STACK = "NESTED_STACK";

    public abstract int layout();

    public abstract String stackIdentifier();

    public void bindServices(ServiceTree.Node node) {
        if(hasNestedStack()) {
            BackstackManager backstackManager = new BackstackManager();
            backstackManager.setStateClearStrategy((keyStateMap, stateChange) -> keyStateMap.keySet().retainAll(node.getTree().getKeys()));
            backstackManager.setup(initialKeys());
            node.bindService(NESTED_STACK, backstackManager);
        }
    }

    protected List<?> initialKeys() {
        return Collections.emptyList();
    }

    public boolean hasNestedStack() {
        return false;
    }

    @NonNull
    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
