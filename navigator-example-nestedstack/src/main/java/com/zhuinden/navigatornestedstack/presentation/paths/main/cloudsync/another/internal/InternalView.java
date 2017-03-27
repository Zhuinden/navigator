package com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync.another.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.application.MainActivity;
import com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync.another.internal2.Internal2Key;
import com.zhuinden.navigatornestedstack.util.BackPressListener;
import com.zhuinden.navigatornestedstack.util.ServiceLocator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhuinden on 2017.02.25..
 */

public class InternalView
        extends RelativeLayout
        implements BackPressListener {
    public InternalView(Context context) {
        super(context);
        init(context);
    }

    public InternalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InternalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public InternalView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    InternalKey internalKey;

    private void init(Context context) {
        if(!isInEditMode()) {
            this.internalKey = Backstack.getKey(context);
        }
    }

    @BindView(R.id.another_text)
    TextView textView;

    @OnClick(R.id.another_back)
    public void backClicked() {
        if(!parentStack.getBackstack().goBack()) {
            MainActivity.get(getContext()).onBackPressed();
        }
    }

    @OnClick(R.id.another_forward)
    public void forwardClicked() {
        InternalKey internalKey = Backstack.getKey(getContext());
        parentStack.getBackstack().goTo(Internal2Key.create(internalKey.parent()));
    }

    BackstackManager parentStack;

    @OnClick(R.id.internal_button)
    public void click() {
        Toast.makeText(getContext(), "Hello!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        parentStack = ServiceLocator.getService(getContext(), Key.NESTED_STACK);
        Object key = Backstack.getKey(getContext());
        Log.i("Internal", "[" + key + "]");
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
