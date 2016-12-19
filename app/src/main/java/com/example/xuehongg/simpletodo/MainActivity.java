package com.example.xuehongg.simpletodo;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.commons.io.FileUtils;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView listView;
    EditText editText;
    int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lvItems);
        editText = (EditText) findViewById(R.id.edtItem);

        items = new ArrayList<String>();
        readItems();
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listView.setAdapter(itemsAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);

                i.putExtra("text", items.get(position));
                i.putExtra("position", position);

                startActivityForResult(i, REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE)
        {
            String text = data.getStringExtra("text");
            int position = data.getIntExtra("position", -1);

            if (position != -1)
            {
                items.set(position, text);
                itemsAdapter.notifyDataSetChanged();
                writeItems();
            }
        }
    }

    public void onClickAdd(View view) {
        String newItem = editText.getText().toString().trim();
        if (newItem.length() > 0) {
            items.add(newItem);
            editText.setText("");
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    private void readItems()
    {
        File fileDir = getFilesDir();
        File file = new File(fileDir,"items.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(file));
        } catch (IOException e)
        {
        }
    }

    private void writeItems()
    {
        File fileDir = getFilesDir();
        File file = new File(fileDir,"items.txt");
        try {
            FileUtils.writeLines(file,items);
        } catch (IOException e)
        {
        }
    }
}
