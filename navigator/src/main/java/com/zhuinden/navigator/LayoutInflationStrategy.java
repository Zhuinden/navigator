package com.zhuinden.navigator;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Owner on 2017. 03. 21..
 */

public interface LayoutInflationStrategy {
    interface InflationCallback {
        void onViewInflated(@NonNull View view);
    }

    void inflateView(@NonNull Context context, @LayoutRes int layout, @NonNull ViewGroup container, @NonNull InflationCallback inflationCallback);
}
