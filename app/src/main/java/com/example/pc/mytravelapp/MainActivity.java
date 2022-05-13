package com.example.pc.mytravelapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText edtTravelName;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtTravelName = findViewById(R.id.edtTravelNameId);
        btnStart = findViewById(R.id.btnStartId);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edtTravelName.getText().toString().trim().equals("")) {
                    Intent intent = new Intent(getApplicationContext(), MyCurrentTravelActivity.class);
                    intent.putExtra("name",edtTravelName.getText().toString().trim());
                    startActivity(intent);
                }

            }
        });
    }
}