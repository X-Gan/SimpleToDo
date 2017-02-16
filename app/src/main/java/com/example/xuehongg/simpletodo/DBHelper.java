package com.example.xuehongg.simpletodo;

/**
 * Created by xuehongg on 2/14/2017.
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.lang.*;
import  java.text.*;
import java.util.Locale;

import static android.R.attr.name;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper sInstance;
    public static final String DATABASE_NAME = "todolistdb.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TODOS_TABLE_NAME = "todos";
    public static final String TODOS_COLUMN_ID = "id";
    public static final String TODOS_COLUMN_DESCRIPTION = "description";
    public static final String TODOS_COLUMN_DUE = "due";
    public static final String TODOS_COLUMN_STATUS = "status";
    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table todos (id integer primary key AUTOINCREMENT, name text, description text, due text, status int)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS todos");
        onCreate(db);
    }


    public boolean insertTodo(Todo todo) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("description", todo.description);
            contentValues.put("due", getDateTime(todo.due));
            contentValues.put("status", todo.status.ordinal());
            db.insert("todos", null, contentValues);
        } catch (Exception e) {
            Log.d("Todolist", "Error while trying to add todo to database: " + e.getMessage());
            return false;
        }
        return true;
    }
    public Todo getTodo(int id) {
        Todo todo;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select * from todos where id = ?", new String[] {String.valueOf(id)});
            res.moveToFirst();
            String description = res.getString(res.getColumnIndex(TODOS_COLUMN_DESCRIPTION));
            String due = res.getString(res.getColumnIndex(TODOS_COLUMN_DUE));
            int status = res.getInt(res.getColumnIndex(TODOS_COLUMN_STATUS));
            if (status == 1)
                todo = new Todo(id, description, setDateTime(due), Status.Completed);
            else
                todo = new Todo(id, description, setDateTime(due), Status.Active);
        } catch (Exception e) {
            Log.d("Todolist", "Error while trying to add post to database: " + e.getMessage());
            return null;
        }
        return todo;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TODOS_TABLE_NAME);
        return numRows;
    }


    public boolean updateTodo(Todo todo) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("description", todo.description);
            contentValues.put("due", getDateTime(todo.due));
            contentValues.put("status", todo.status.ordinal());
            db.update("todos", contentValues, "id = ? ", new String[]{Integer.toString(todo.id)});
        }catch (Exception e) {
            Log.d("Todolist", "Error while trying to update todo to database: " + e.getMessage());
            return false;
        }

        return true;
    }

    public Integer deleteTodo(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("todos",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<Todo> getAllTodos() {
        ArrayList<Todo> list = new ArrayList<Todo>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from todos", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            Todo todo;
            int id = res.getInt(res.getColumnIndex(TODOS_COLUMN_ID));
            String description = res.getString(res.getColumnIndex(TODOS_COLUMN_DESCRIPTION));
            String due = res.getString(res.getColumnIndex(TODOS_COLUMN_DUE));
            int status = res.getInt(res.getColumnIndex(TODOS_COLUMN_STATUS));
            if (status == 1)
                todo = new Todo(id, description, setDateTime(due), Status.Completed);
            else
                todo = new Todo(id, description, setDateTime(due), Status.Active);
            list.add(todo);
            res.moveToNext();
        }
        return list;
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    private Date setDateTime(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return dateFormat.parse(date);
        }
        catch(ParseException pe) {
            Log.e("TodoList", "Failed to parse date string: " + pe.getMessage());
            return null;
        }
    }
}
