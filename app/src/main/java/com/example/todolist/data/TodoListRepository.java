package com.example.todolist.data;

import android.content.Context;

import java.net.URLDecoder;
import java.util.List;

public class TodoListRepository {
    TodoListDatabase todoListDatabase;
    TodoListDao todoListDao;

    public TodoListRepository(Context context) {
        todoListDatabase = TodoListDatabase.getInstance(context);

        todoListDao = todoListDatabase.todoListDao();
    }

    public void addNewTask(final Task task, final TaskCreatedCallback callback) {

        TodoListDatabase.dataBaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long taskId = todoListDao.addNewTask(task);
                //notify that task is created
                callback.onTaskCreated(taskId);
            }
        });

    }

    public List<Task> getAllTasks() {
        return todoListDao.getAllTasks();
    }


    public void getTaskById(final long taskId, final GetTaskByIdCallback callback) {
        TodoListDatabase.dataBaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Task task = todoListDao.getTaskById(taskId);
                callback.onTaskRetrieved(task);
            }
        });
    }

    public interface GetTaskByIdCallback {
        void onTaskRetrieved(Task task);
    }

    public interface TaskCreatedCallback {
        void onTaskCreated(long taskId);
    }

}
