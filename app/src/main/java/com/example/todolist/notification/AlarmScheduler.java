package com.example.todolist.notification;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class AlarmScheduler {


    public static final String TASK_ID_KEY = "task_id_key";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void createAlarmForTask(Context context, long taskId, int year, int month, int day, int hour, int minute) {

        //Create alarm manager.

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar alarmDateTimeCalendar = Calendar.getInstance();
        alarmDateTimeCalendar.setTimeInMillis(System.currentTimeMillis());
        alarmDateTimeCalendar.set(Calendar.YEAR, year);
        alarmDateTimeCalendar.set(Calendar.MONTH, month);
        alarmDateTimeCalendar.set(Calendar.DAY_OF_MONTH, day);
        alarmDateTimeCalendar.set(Calendar.HOUR, hour);
        alarmDateTimeCalendar.set(Calendar.MINUTE, minute);
        alarmDateTimeCalendar.set(Calendar.SECOND, 0);
        alarmDateTimeCalendar.set(Calendar.MILLISECOND, 0);
        long dateTimeMilli = alarmDateTimeCalendar.getTimeInMillis();


        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra(TASK_ID_KEY, taskId);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dateTimeMilli, alarmIntent);


    }

}
