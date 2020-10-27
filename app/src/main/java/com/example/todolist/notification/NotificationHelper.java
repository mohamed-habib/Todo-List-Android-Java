package com.example.todolist.notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todolist.R;
import com.example.todolist.data.TodoListRepository;

public class NotificationHelper {

    public static void createNotification(Context context, String taskName, String taskDescription, long taskId) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "Task Noticiation Channel")
                .setContentTitle(taskName)
                .setContentText(taskDescription)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManagerCompat.from(context).notify((int) taskId, notificationBuilder.build());
        //notify
    }
}
