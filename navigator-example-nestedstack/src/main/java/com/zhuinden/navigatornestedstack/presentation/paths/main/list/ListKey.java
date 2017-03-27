package com.zhuinden.navigatornestedstack.presentation.paths.main.list;

import com.google.auto.value.AutoValue;
import com.zhuinden.navigatornestedstack.R;
import com.zhuinden.navigatornestedstack.application.Key;
import com.zhuinden.navigatornestedstack.presentation.paths.main.MainView;

/**
 * Created by Zhuinden on 2017.02.19..
 */

@AutoValue
public abstract class ListKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_list;
    }

    public static ListKey create() {
        return new AutoValue_ListKey();
    }

    @Override
    public String stackIdentifier() {
        return MainView.StackType.LIST.name();
    }
}
