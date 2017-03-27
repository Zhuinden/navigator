package com.zhuinden.navigatorexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.zhuinden.navigator.DefaultStateChanger;
import com.zhuinden.navigator.Navigator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String TAG = "MainActivity";

    @BindView(R.id.root)
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Backstack backstack = Navigator.configure() //
                .setStateChanger(new DefaultStateChanger(this, root, this)) //
                .setDeferredInitialization(true)
                .install(this, root, HistoryBuilder.single(FirstKey.create()));

        // if init is deferred, you can do whatever you want with the backstack
        // but then the initialization must be started explicitly

        Navigator.executeDeferredInitialization(this);
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            completionCallback.stateChangeComplete();
            return;
        }
        StateTitleKey stateTitleKey = stateChange.topNewState();
        setTitle(stateTitleKey.title());
        completionCallback.stateChangeComplete();
    }
}

