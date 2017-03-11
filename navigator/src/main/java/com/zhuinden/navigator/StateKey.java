package com.zhuinden.navigator;

/**
 * Created by Zhuinden on 2017.03.11..
 */

public interface StateKey {
    int layout();

    ViewController createViewController(Object... args);
}
