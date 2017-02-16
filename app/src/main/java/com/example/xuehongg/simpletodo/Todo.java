package com.example.xuehongg.simpletodo;

import java.util.Date;

/**
 * Created by xuehongg on 2/14/2017.
 */

enum Status {
    Active,
    Completed
};

public class Todo {
    public int id;
    public String description;
    public Date due;
    public Status status;

    public Todo(int id, String description, Date due, Status status)
    {
        this.id = id;
        this.description = description;
        this.due = due;
        this.status = status;
    }
}
