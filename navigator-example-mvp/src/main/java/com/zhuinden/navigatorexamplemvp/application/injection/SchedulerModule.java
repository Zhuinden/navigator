package com.zhuinden.navigatorexamplemvp.application.injection;

import com.zhuinden.navigatorexamplemvp.util.SchedulerHolder;

import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.schedulers.Schedulers;

/**
 * Created by Owner on 2017. 01. 26..
 */

@Module
public class SchedulerModule {
    @Provides
    @Named("LOOPER_SCHEDULER")
    @Singleton
    SchedulerHolder looperScheduler(SchedulerHolder schedulerHolder) {
        return schedulerHolder;
    }

    @Provides
    @Named("WRITE_SCHEDULER")
    @Singleton
    SchedulerHolder writeScheduler(SchedulerHolder schedulerHolder) {
        schedulerHolder.setScheduler(Schedulers.from(Executors.newSingleThreadExecutor()));
        return schedulerHolder;
    }
}
