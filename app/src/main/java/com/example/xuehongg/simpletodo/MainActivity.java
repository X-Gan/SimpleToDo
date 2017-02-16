package com.example.xuehongg.simpletodo;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.io.FileUtils;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbhelper;
    ArrayList<Todo> items;
    ArrayAdapter<String> itemsAdapter;
    ListView listView;
    EditText editText;
    boolean reverse = false;
    int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);


        listView = (ListView) findViewById(R.id.lvItems);
        dbhelper = DBHelper.getInstance(this);
        items = dbhelper.getAllTodos();
        sort(reverse);

        itemsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, items) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                    TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                    if (items.get(position).status == Status.Completed) {
                        text1.setTextColor(Color.GRAY);
                        text2.setTextColor(Color.GRAY);
                    } else {
                        text1.setTextColor(Color.BLACK);
                        text2.setTextColor(Color.BLACK);
                    }
                    text1.setText(items.get(position).description);
                    text2.setText("due: "+ getDateTime(items.get(position).due));
                    return view;
                }
            };
        ;
        listView.setAdapter(itemsAdapter);

        // delete a todo
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dbhelper.deleteTodo(items.get(position).id);
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                return true;
            }
        });

        // edit a todo
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("id", items.get(position).id);
                startActivityForResult(i, REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE)
        {
            // arraryadapter got a point to my items so we need to modify that items.
            ArrayList<Todo> currentitems = dbhelper.getAllTodos();
            items.clear();
            for (Todo currenttodo: currentitems)
            {
                    items.add(currenttodo);
            }

            sort(reverse);
            itemsAdapter.notifyDataSetChanged();
        }
    }

    private void sort(boolean reverse)
    {
        if (reverse) {
            Collections.sort(items, new Comparator<Todo>() {
                @Override
                public int compare(Todo todo1, Todo todo2) {

                    return todo2.due.compareTo(todo1.due);
                }
            });
        } else
        {
            Collections.sort(items, new Comparator<Todo>() {
                @Override
                public int compare(Todo todo1, Todo todo2) {

                    return todo1.due.compareTo(todo2.due);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // add a todo
            case R.id.action_add:
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("id", -1);
                startActivityForResult(i, REQUEST_CODE);
                return true;

            case R.id.action_sort:
                reverse = !reverse;
                sort(reverse);
                itemsAdapter.notifyDataSetChanged();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private String getDateTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }
}
