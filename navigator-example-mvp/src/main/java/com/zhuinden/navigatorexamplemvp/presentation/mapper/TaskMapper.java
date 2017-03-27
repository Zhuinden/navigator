package com.zhuinden.navigatorexamplemvp.presentation.mapper;

import com.zhuinden.navigatorexamplemvp.data.entity.DbTask;
import com.zhuinden.navigatorexamplemvp.presentation.objects.Task;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Owner on 2017. 01. 26..
 */

@Singleton
public class TaskMapper {
    @Inject
    public TaskMapper() {
    }

    public Task fromRealm(DbTask dbTask) {
        return Task.createCompletedTaskWithId(dbTask.getTitle(), dbTask.getDescription(), dbTask.getId(), dbTask.getCompleted());
    }

    public DbTask toRealm(Task task) {
        DbTask dbTask = new DbTask();
        dbTask.setId(task.id());
        dbTask.setCompleted(task.completed());
        dbTask.setDescription(task.description());
        dbTask.setTitle(task.title());
        return dbTask;
    }
}
