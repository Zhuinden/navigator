package com.zhuinden.navigatorexamplemvp.data.repository;

import com.zhuinden.navigatorexamplemvp.data.entity.DbTask;
import com.zhuinden.navigatorexamplemvp.data.entity.DbTaskFields;
import com.zhuinden.navigatorexamplemvp.presentation.mapper.TaskMapper;
import com.zhuinden.navigatorexamplemvp.presentation.objects.Task;
import com.zhuinden.navigatorexamplemvp.util.SchedulerHolder;
import com.zhuinden.navigatorexamplemvp.util.optional.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.subscriptions.Subscriptions;

/**
 * Created by Owner on 2017. 01. 26..
 */
@Singleton
public class TaskRepository {
    @Inject
    TaskMapper taskMapper;

    @Inject
    @Named("LOOPER_SCHEDULER")
    SchedulerHolder looperScheduler;

    @Inject
    @Named("WRITE_SCHEDULER")
    SchedulerHolder writeScheduler;

    @Inject
    public TaskRepository() {
    }

    public Observable<List<Task>> getTasks() {
        return Observable.create(new Observable.OnSubscribe<List<Task>>() {
            private List<Task> mapFrom(RealmResults<DbTask> dbTasks) {
                List<Task> tasks = new ArrayList<>(dbTasks.size());
                for(DbTask dbTask : dbTasks) {
                    tasks.add(taskMapper.fromRealm(dbTask));
                }
                return tasks;
            }

            @Override
            public void call(final Subscriber<? super List<Task>> subscriber) {
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<DbTask> dbTasks = realm.where(DbTask.class).findAllSorted(DbTaskFields.ID, Sort.ASCENDING);
                final RealmChangeListener<RealmResults<DbTask>> realmChangeListener = element -> {
                    if(!subscriber.isUnsubscribed()) {
                        List<Task> tasks = mapFrom(element);
                        if(!subscriber.isUnsubscribed()) {
                            subscriber.onNext(tasks);
                        }
                    }
                };
                subscriber.add(Subscriptions.create(() -> {
                    if(dbTasks.isValid()) {
                        dbTasks.removeChangeListener(realmChangeListener);
                    }
                    realm.close();
                }));
                dbTasks.addChangeListener(realmChangeListener);
                subscriber.onNext(mapFrom(dbTasks));
            }
        }).subscribeOn(looperScheduler.getScheduler()).unsubscribeOn(looperScheduler.getScheduler());
    }

    public Observable<List<Task>> getCompletedTasks() {
        return Observable.create(new Observable.OnSubscribe<List<Task>>() {
            private List<Task> mapFrom(RealmResults<DbTask> dbTasks) {
                List<Task> tasks = new ArrayList<>(dbTasks.size());
                for(DbTask dbTask : dbTasks) {
                    tasks.add(taskMapper.fromRealm(dbTask));
                }
                return tasks;
            }

            @Override
            public void call(final Subscriber<? super List<Task>> subscriber) {
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<DbTask> dbTasks = realm.where(DbTask.class)
                        .equalTo(DbTaskFields.COMPLETED, true)
                        .findAllSorted(DbTaskFields.ID, Sort.ASCENDING);
                final RealmChangeListener<RealmResults<DbTask>> realmChangeListener = element -> {
                    if(!subscriber.isUnsubscribed()) {
                        List<Task> tasks = mapFrom(element);
                        if(!subscriber.isUnsubscribed()) {
                            subscriber.onNext(tasks);
                        }
                    }
                };
                subscriber.add(Subscriptions.create(() -> {
                    if(dbTasks.isValid()) {
                        dbTasks.removeChangeListener(realmChangeListener);
                    }
                    realm.close();
                }));
                dbTasks.addChangeListener(realmChangeListener);
                subscriber.onNext(mapFrom(dbTasks));
            }
        }).subscribeOn(looperScheduler.getScheduler()).unsubscribeOn(looperScheduler.getScheduler());
    }

    public Observable<List<Task>> getActiveTasks() {
        return Observable.create(new Observable.OnSubscribe<List<Task>>() {
            private List<Task> mapFrom(RealmResults<DbTask> dbTasks) {
                List<Task> tasks = new ArrayList<>(dbTasks.size());
                for(DbTask dbTask : dbTasks) {
                    tasks.add(taskMapper.fromRealm(dbTask));
                }
                return tasks;
            }

            @Override
            public void call(final Subscriber<? super List<Task>> subscriber) {
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<DbTask> dbTasks = realm.where(DbTask.class)
                        .equalTo(DbTaskFields.COMPLETED, false)
                        .findAllSorted(DbTaskFields.ID, Sort.ASCENDING);
                final RealmChangeListener<RealmResults<DbTask>> realmChangeListener = element -> {
                    if(!subscriber.isUnsubscribed()) {
                        List<Task> tasks = mapFrom(element);
                        if(!subscriber.isUnsubscribed()) {
                            subscriber.onNext(tasks);
                        }
                    }
                };
                subscriber.add(Subscriptions.create(() -> {
                    if(dbTasks.isValid()) {
                        dbTasks.removeChangeListener(realmChangeListener);
                    }
                    realm.close();
                }));
                dbTasks.addChangeListener(realmChangeListener);
                subscriber.onNext(mapFrom(dbTasks));
            }
        }).subscribeOn(looperScheduler.getScheduler()).unsubscribeOn(looperScheduler.getScheduler());
    }

    @SuppressWarnings("NewApi")
    public void insertTask(Task task) {
        Single.create((Single.OnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(taskMapper.toRealm(task));
                });
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }

    @SuppressWarnings("NewApi")
    public void insertTasks(List<Task> tasks) {
        Single.create((Single.OnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                List<DbTask> dbTasks = new ArrayList<>(tasks.size());
                for(Task task : tasks) {
                    dbTasks.add(taskMapper.toRealm(task));
                }
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(dbTasks);
                });
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }

    @SuppressWarnings("NewApi")
    public void deleteCompletedTasks() {
        Single.create((Single.OnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> realm.where(DbTask.class)
                        .equalTo(DbTaskFields.COMPLETED, true)
                        .findAll()
                        .deleteAllFromRealm());
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }

    @SuppressWarnings("NewApi")
    public Single<Optional<Task>> findTask(String taskId) {
        return Single.fromCallable(new Callable<Optional<Task>>() {
            @Override
            public Optional<Task> call()
                    throws Exception {
                try(Realm realm = Realm.getDefaultInstance()) {
                    RealmResults<DbTask> tasks = realm.where(DbTask.class).equalTo(DbTaskFields.ID, taskId).findAll();
                    if(tasks.size() > 0) {
                        return Optional.of(taskMapper.fromRealm(tasks.get(0)));
                    } else {
                        return Optional.absent();
                    }
                }
            }
        }).subscribeOn(looperScheduler.getScheduler());
    }

    public void setTaskCompleted(Task task) {
        insertTask(task.toBuilder().setCompleted(true).build());
    }

    public void setTaskActive(Task task) {
        insertTask(task.toBuilder().setCompleted(false).build());
    }

    @SuppressWarnings("NewApi")
    public void deleteTask(Task task) {
        Single.create((Single.OnSubscribe<Void>) singleSubscriber -> {
            try(Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> realm.where(DbTask.class).equalTo(DbTaskFields.ID, task.id()).findAll().deleteAllFromRealm());
            }
        }).subscribeOn(writeScheduler.getScheduler()).subscribe();
    }
}
