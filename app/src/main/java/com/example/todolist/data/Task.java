package com.example.todolist.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity//table
public class Task {

    @PrimaryKey(autoGenerate = true)
    int id;
    String name;
    String description;
    String date;
    String time;
    String userLocation;

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Ignore
    public Task(String name, String description, String date, String time, String userLocation) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.userLocation = userLocation;
    }

    public Task(int id, String name, String description, String date, String time, String userLocation) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.userLocation = userLocation;
    }
}
