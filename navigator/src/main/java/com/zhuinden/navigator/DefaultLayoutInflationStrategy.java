package com.zhuinden.navigator;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by Owner on 2017. 03. 21..
 */

public class DefaultLayoutInflationStrategy
        implements LayoutInflationStrategy {
    @Override
    public void inflateView(@NonNull Context context, @LayoutRes int layout, @NonNull ViewGroup container, @NonNull InflationCallback inflationCallback) {
        inflationCallback.onViewInflated(LayoutInflater.from(context).inflate(layout, container, false));
    }
}