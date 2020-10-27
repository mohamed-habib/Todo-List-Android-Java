package com.example.todolist;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.data.Task;
import com.example.todolist.databinding.TaskItemBinding;

import java.util.ArrayList;
import java.util.List;

public class TodoListRecyclerViewAdapter extends RecyclerView.Adapter<BindingViewHolder<TaskItemBinding>> {
    ArrayList<Task> taskArrayList;

    public TodoListRecyclerViewAdapter(ArrayList<Task> taskArrayList) {
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public BindingViewHolder<TaskItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BindingViewHolder<>(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BindingViewHolder<TaskItemBinding> holder, int position) {
        holder.getBinding().setTaskItem(taskArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public void addData(List<Task> taskList) {
        taskArrayList.clear();
        taskArrayList.addAll(taskList);
        notifyDataSetChanged();
    }
}
