package com.zhuinden.navigatorexample;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.view.AsyncLayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.navigator.LayoutInflationStrategy;

/**
 * Created by Owner on 2017. 03. 21..
 */

public class AsyncLayoutInflationStrategy
        implements LayoutInflationStrategy {
    @Override
    public void inflateView(@NonNull Context context, @LayoutRes int layout, @NonNull ViewGroup container, @NonNull InflationCallback inflationCallback) {
        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        asyncLayoutInflater.inflate(layout, container, new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(View view, int resid, ViewGroup parent) {
                inflationCallback.onViewInflated(view);
            }
        });
    }
}
