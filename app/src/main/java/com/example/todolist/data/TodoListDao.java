package com.example.todolist.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
//Data Access Object
interface TodoListDao {
    @Query("Select * from task")
    List<Task> getAllTasks();

    @Insert
    long addNewTask(Task task);

    @Query("Select * from task where id = :taskId")
    Task getTaskById(long taskId);
}
