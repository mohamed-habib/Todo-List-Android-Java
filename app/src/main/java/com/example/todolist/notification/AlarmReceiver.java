package com.example.todolist.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.todolist.data.Task;
import com.example.todolist.data.TodoListRepository;

import static com.example.todolist.notification.AlarmScheduler.TASK_ID_KEY;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        ///show on activity.
        final long taskId = intent.getExtras().getLong(TASK_ID_KEY);

        TodoListRepository todoListRepository = new TodoListRepository(context);
        todoListRepository.getTaskById(taskId, new TodoListRepository.GetTaskByIdCallback() {
            @Override
            public void onTaskRetrieved(Task task) {

                //createNotification
                NotificationHelper.createNotification(context, task.getName(), task.getDescription(), taskId);


            }
        });


    }
}
