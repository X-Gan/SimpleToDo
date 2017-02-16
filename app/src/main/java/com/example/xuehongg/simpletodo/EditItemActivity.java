package com.example.xuehongg.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Calendar;
import java.util.Date;

public class EditItemActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private RadioButton activeButton;
    private RadioButton completedButton;
    private int id;
    private Status status =Status.Active;
    private DBHelper dbhelper;
    EditText todoEdit;
    DatePicker  dPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_item);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.save_toolbar);
        setSupportActionBar(myToolbar);

        id = getIntent().getIntExtra("id", -1);

        dbhelper = DBHelper.getInstance(this);
        todoEdit = (EditText)findViewById(R.id.todoEdit);
        dPicker = (DatePicker)findViewById(R.id.datePicker3);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        activeButton = (RadioButton) findViewById(R.id.radioButton1);
        completedButton = (RadioButton) findViewById(R.id.radioButton2);
        activeButton.setChecked(true);  // default it is an active todo
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButton = (RadioButton) findViewById(checkedId);
                if (radioButton.getText().equals("Completed"))
                    status = Status.Completed;
                else
                    status = Status.Active;
            }
        });

        // check whether this is to edit an existing todo
        if (id!=-1) {
            dbhelper = DBHelper.getInstance(this);
            Todo todo = dbhelper.getTodo(id);
            todoEdit.setText(todo.description);
            int year = toCalendar(todo.due).get(Calendar.YEAR);
            int month = toCalendar(todo.due).get(Calendar.MONTH);
            int day = toCalendar(todo.due).get(Calendar.DAY_OF_MONTH);
            dPicker.init(year, month, day, null);
            if (todo.status == Status.Completed)
                completedButton.setChecked(true);
            else if (todo.status == Status.Active)
                activeButton.setChecked(true);
        }
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                String desciption = todoEdit.getText().toString();
                if( desciption.trim().equals("")) {
                    todoEdit.setError("Todo is required!");
                    todoEdit.setHint("please enter your todo");
                    return true;
                }

                Date date = getDateFromDatePicker(dPicker);
                Todo todo = new Todo(id, desciption, date, status);
                if (id==-1) {
                    dbhelper.insertTodo(todo);
                } else {
                    dbhelper.updateTodo(todo);
                }
                Intent data = new Intent();
                setResult(RESULT_OK,data);
                finish();
                return true;

            case R.id.action_sort:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
