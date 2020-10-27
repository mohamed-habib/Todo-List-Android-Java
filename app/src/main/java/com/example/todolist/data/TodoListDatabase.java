package com.example.todolist.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//Singleton design pattern
//annotation
@Database(entities = {Task.class}, exportSchema = false, version = 2)
public abstract class TodoListDatabase extends RoomDatabase {

    private static TodoListDatabase instance;

    public static synchronized TodoListDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), TodoListDatabase.class, "todo_list_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }


   public static final ExecutorService dataBaseExecutor = Executors.newFixedThreadPool(4);


    public abstract TodoListDao todoListDao();

}
