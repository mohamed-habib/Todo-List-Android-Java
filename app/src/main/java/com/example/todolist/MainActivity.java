package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.todolist.data.Task;
import com.example.todolist.data.TodoListDatabase;
import com.example.todolist.data.TodoListRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TodoListRepository todoListRepository;
    TodoListRecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new TodoListRecyclerViewAdapter(new ArrayList<Task>());

        recyclerView.setAdapter(recyclerViewAdapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddNewTask.class));
            }
        });

        todoListRepository = new TodoListRepository(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TodoListDatabase.dataBaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<Task> allTasks = todoListRepository.getAllTasks();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAdapter.addData(allTasks);
                    }
                });
            }
        });//kotlin coroutines.

    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}