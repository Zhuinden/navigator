package com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync.another;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zhuinden.navigator.DefaultStateChanger;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.util.BackPressListener;
import com.zhuinden.navigatornestedstack.util.NestSupportServiceManager;
import com.zhuinden.navigatornestedstack.util.ServiceLocator;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhuinden on 2017.02.19..
 */

public class AnotherView
        extends RelativeLayout
        implements StateChanger, BackPressListener {
    public AnotherView(Context context) {
        super(context);
        init(context);
    }

    public AnotherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public AnotherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            anotherKey = Backstack.getKey(context);
        }
    }

    AnotherKey anotherKey;

    @BindView(R.id.another_nested_container)
    FrameLayout nestedContainer;

    BackstackManager backstackManager;

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        backstackManager = ServiceLocator.getService(getContext(), Key.NESTED_STACK);
        backstackManager.setStateChanger(DefaultStateChanger.configure()
                .setExternalStateChanger(this)
                .create(getContext(), nestedContainer));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        backstackManager.reattachStateChanger();
    }

    @Override
    protected void onDetachedFromWindow() {
        backstackManager.detachStateChanger();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onBackPressed() {
        if(nestedContainer.getChildAt(0) != null && nestedContainer.getChildAt(0) instanceof BackPressListener) {
            boolean handled = ((BackPressListener) nestedContainer.getChildAt(0)).onBackPressed();
            if(handled) {
                return true;
            }
        }
        return backstackManager.getBackstack().goBack();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        NestSupportServiceManager.get(getContext()).setupServices(stateChange);
        completionCallback.stateChangeComplete();
    }
}
