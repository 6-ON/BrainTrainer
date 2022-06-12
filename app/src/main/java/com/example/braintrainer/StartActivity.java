package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {
    Button buttonStart;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkState();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Intent play = new Intent(this, PlayGroundActivity.class);
        buttonStart = findViewById(R.id.buttonGetStarted);


        buttonStart.setOnClickListener(view -> {
            sharedPreferences.edit().putInt("state", 1).apply();
            MoveToMenu();

        });
    }


    private void MoveToMenu() {
        Intent getStarted = new Intent(StartActivity.this, MenuActivity.class);
        getStarted.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(getStarted);
        finish();
    }

    private void checkState() {
        sharedPreferences = getPreferences(MODE_PRIVATE);

        int state = sharedPreferences.getInt("state", -1);

        if (state == 1) {

            MoveToMenu();
        }
    }

}
