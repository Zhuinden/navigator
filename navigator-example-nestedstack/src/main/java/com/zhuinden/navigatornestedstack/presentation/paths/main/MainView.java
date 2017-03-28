package com.zhuinden.navigatornestedstack.presentation.paths.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.presentation.paths.main.chromecast.ChromeCastKey;
import com.zhuinden.navigatornestedstack.presentation.paths.main.cloudsync.CloudSyncKey;
import com.zhuinden.navigatornestedstack.presentation.paths.main.list.ListKey;
import com.zhuinden.navigatornestedstack.presentation.paths.main.mail.MailKey;
import com.zhuinden.navigatornestedstack.util.ServiceLocator;
import com.zhuinden.navigatornestedstack.util.ViewUtils;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.BackstackManager;
import com.zhuinden.simplestack.HistoryBuilder;
import com.zhuinden.simplestack.KeyContextWrapper;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

/**
 * Created by Zhuinden on 2017.02.26..
 */

public class MainView
        extends CoordinatorLayout
        implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    @BindView(R.id.bottom_navigation)
    BottomNavigation bottomNavigation;

    public enum StackType {
        CLOUDSYNC {
            @Override
            public Key getKey() {
                return CloudSyncKey.create();
            }
        },
        CHROMECAST {
            @Override
            public Key getKey() {
                return ChromeCastKey.create();
            }
        },
        MAIL {
            @Override
            public Key getKey() {
                return MailKey.create();
            }
        },
        LIST {
            @Override
            public Key getKey() {
                return ListKey.create();
            }
        };

        public abstract Key getKey();
    }

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    BackstackManager backstackManager;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);

        bottomNavigation.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(@IdRes int menuItemId, int itemIndex, boolean b) {
                Key previousKey = Backstack.getKey(root.getChildAt(0).getContext());
                StackType newStack = StackType.values()[itemIndex];
                Key newKey = newStack.getKey();
                int direction = StateChange.REPLACE;
                StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
                direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
                backstackManager.getBackstack().setHistory(HistoryBuilder.from(backstackManager).removeLast().add(newKey).build(), direction);
            }

            @Override
            public void onMenuItemReselect(@IdRes int menuItemId, int itemIndex, boolean b) {

            }
        });
        backstackManager = ServiceLocator.getService(getContext(), Key.NESTED_STACK);
        backstackManager.setStateChanger(this);
    }

    private void exchangeViewForKey(Key newKey, int direction) {
        backstackManager.persistViewToState(root.getChildAt(0));
        View previousView = root.getChildAt(0);
        View newView = LayoutInflater.from(new KeyContextWrapper(getContext(), newKey)).inflate(newKey.layout(), this, false);
        backstackManager.restoreViewFromState(newView);
        root.addView(newView);

        if(direction == StateChange.REPLACE) {
            finishStateChange(previousView);
        } else {
            ViewUtils.waitForMeasure(newView, (view, width, height) -> {
                runAnimation(previousView, newView, direction, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finishStateChange(previousView);
                    }
                });
            });
        }
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        // no need to setup services here, because it is handled by Composite
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        int direction = StateChange.REPLACE;
        if(root.getChildAt(0) != null) {
            Key previousKey = stateChange.topPreviousState();
            Key newKey = stateChange.topNewState();
            StackType newStack = StackType.valueOf(newKey.stackIdentifier());
            if(previousKey != null) {
                StackType previousStack = StackType.valueOf(previousKey.stackIdentifier());
                direction = previousStack.ordinal() < newStack.ordinal() ? StateChange.FORWARD : previousStack.ordinal() > newStack.ordinal() ? StateChange.BACKWARD : StateChange.REPLACE;
            }
        }
        exchangeViewForKey(stateChange.topNewState(), direction);
        completionCallback.stateChangeComplete();
    }

    private void finishStateChange(View previousView) {
        root.removeView(previousView);
    }

    // animation
    private void runAnimation(final View previousView, final View newView, int direction, AnimatorListenerAdapter animatorListenerAdapter) {
        Animator animator = createSegue(previousView, newView, direction);
        animator.addListener(animatorListenerAdapter);
        animator.start();
    }

    private Animator createSegue(View from, View to, int direction) {
        boolean backward = direction == StateChange.BACKWARD;
        int fromTranslation = backward ? from.getWidth() : -from.getWidth();
        int toTranslation = backward ? -to.getWidth() : to.getWidth();

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
        set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));
        return set;
    }
}
