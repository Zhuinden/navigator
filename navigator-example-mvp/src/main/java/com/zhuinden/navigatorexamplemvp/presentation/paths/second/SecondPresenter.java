package com.zhuinden.navigatorexamplemvp.presentation.paths.second;

import android.support.annotation.Nullable;

import com.jakewharton.rxrelay.BehaviorRelay;
import com.zhuinden.navigatorexamplemvp.presentation.paths.tasks.TasksKey;
import com.zhuinden.navigatorexamplemvp.util.BasePresenter;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.statebundle.StateBundle;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by Owner on 2017. 01. 27..
 */

public class SecondPresenter
        extends BasePresenter<SecondView, SecondPresenter>
        implements Bundleable {
    @Inject
    public SecondPresenter() {
    }

    @Inject
    Backstack backstack;

    BehaviorRelay<String> state = BehaviorRelay.create("");

    Subscription subscription;

    @Override
    protected void onAttach(SecondView view) {
        subscription = state.asObservable() //
                .doOnNext(_state -> view.setStateText(_state)) //
                .subscribe();
    }

    @Override
    protected void onDetach(SecondView view) {
        subscription.unsubscribe();
    }

    public void updateState(String state) {
        this.state.call(state);
    }

    public void goToTodos() {
        backstack.goTo(TasksKey.create());
    }

    public StateBundle toBundle() {
        StateBundle bundle = new StateBundle();
        bundle.putString("state", state.getValue());
        return bundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {

        }
    }
}
