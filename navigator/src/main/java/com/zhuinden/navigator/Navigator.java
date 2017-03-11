/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.navigator;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.zhuinden.simplestack.Backstack;

import java.util.List;

public class Navigator {
    private Navigator() {
    }

    public static void install(@NonNull Activity activity, @NonNull ViewGroup container, @NonNull List<Object> initialKeys) {
        if(activity == null) {
            throw new IllegalArgumentException("Activity cannot be null!");
        }
        if(container == null) {
            throw new IllegalArgumentException("State changer cannot be null!");
        }
        if(initialKeys == null || initialKeys.isEmpty()) {
            throw new IllegalArgumentException("Initial keys cannot be null!");
        }
        BackstackHost backstackHost = (BackstackHost) activity.getFragmentManager().findFragmentByTag("NAVIGATOR_BACKSTACK_HOST");
        if(backstackHost == null) {
            backstackHost = new BackstackHost();
            activity.getFragmentManager().beginTransaction().add(backstackHost, "NAVIGATOR_BACKSTACK_HOST").commit();
            activity.getFragmentManager().executePendingTransactions();
        }
        backstackHost.initialKeys = initialKeys;
        backstackHost.container = container;
        backstackHost.initialize();
    }

    private static Activity findActivity(Context context) {
        if(context instanceof Activity) {
            return (Activity) context;
        } else {
            ContextWrapper contextWrapper = (ContextWrapper) context;
            Context baseContext = contextWrapper.getBaseContext();
            if(baseContext == null) {
                throw new IllegalStateException("Activity was not found as base context of view!");
            }
            return findActivity(baseContext);
        }
    }

    public static Backstack getBackstack(Context context) {
        Activity activity = findActivity(context);
        BackstackHost backstackHost = (BackstackHost) activity.getFragmentManager().findFragmentByTag("NAVIGATOR_BACKSTACK_HOST");
        return backstackHost.getBackstack();
    }

    public static boolean onBackPressed(Activity activity) {
        return getBackstack(activity).goBack();
    }
}
