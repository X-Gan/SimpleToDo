package com.example.xuehongg.simpletodo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    EditText editText;
    Button saveButton;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_item);
        String text = getIntent().getStringExtra("text");
        position = getIntent().getIntExtra("position", -1);

        Button saveButton = (Button)findViewById(R.id.btnSave);
        editText = (EditText)findViewById(R.id.edtItemText);
        editText.setText(text);

    }

    public void onClickSave(View view) {
        Intent data = new Intent();

        data.putExtra("text", editText.getText().toString());
        data.putExtra("position", position);

        setResult(RESULT_OK,data);
        finish();
    }
}
