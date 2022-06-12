package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    Button buttonPlay,buttonQuit;
    TextView textViewHighestScore;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Log.i("User Info"," Creating To Menu");
        updateHighestScore();
        setupButtons();


    }

    private void updateHighestScore() {
        sharedPreferences = getSharedPreferences("game_info", Context.MODE_PRIVATE);
        textViewHighestScore = findViewById(R.id.textViewHighestScore);
        int score = sharedPreferences.getInt("highestScore",0);
        if (score !=0){
            Log.i("User Info","update highScore");
            textViewHighestScore.setText(String.valueOf(score));
        }
    }

    private void setupButtons() {
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonQuit =findViewById(R.id.buttonQuit);
        buttonPlay.setOnClickListener(view -> {
            Intent intentPlay = new Intent(MenuActivity.this,PlayGroundActivity.class);
            startActivity(intentPlay);
            finish();
        });
        buttonQuit.setOnClickListener(view -> finishAffinity());
    }
}