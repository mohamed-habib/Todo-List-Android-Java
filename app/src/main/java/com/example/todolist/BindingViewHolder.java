package com.example.todolist;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

class BindingViewHolder<T> extends RecyclerView.ViewHolder {
    private final T binding;

    public BindingViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = (T) DataBindingUtil.bind(itemView);
    }

    public T getBinding() {
        return binding;
    }
}
