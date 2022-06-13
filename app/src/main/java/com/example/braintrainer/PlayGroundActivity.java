package com.example.braintrainer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class PlayGroundActivity extends AppCompatActivity {
    int scoreValue;
    int attemptsValue;
    public static int PROBLEM_TIME_IN_MS = 15 * 1000;
    public static int START_PROBLEM_DELAY = 1000;
    public static int NUM_CARDS = 4;
    public static int MAX_RAND_NUM = 500;
    Object AnswerTag;
    Button buttonSubmitAnswer, buttonLeave;
    TextView textViewScore, textViewAttempts, textViewProblem;
    LinearProgressIndicator timeProgress;
    ArrayList<View> options;
    GridLayout gridLayoutOptions;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ground);
        scoreValue = 0;
        attemptsValue = 3;
        SetupViews();

        startProblem();

    }

    private void startProblem() {
        Log.i("User Info", "New Problem Started");
        timeProgress.setIndicatorColor(getColorInt(R.color.teal_200));

        textViewAttempts.setText(String.valueOf(attemptsValue));
        textViewScore.setText(String.valueOf(scoreValue));
        Map.Entry<String, Integer> problemPair = getRandomProblemWithAnswer();
        textViewProblem.setText(problemPair.getKey());
        // generate random false answers
        setupOptionCards(problemPair.getValue());
        int answerCardIndex = new Random().nextInt(NUM_CARDS);
        MaterialCardView cardViewAnswer = (MaterialCardView) options.get(answerCardIndex);
        AnswerTag = cardViewAnswer.getTag();
        MaterialTextView textViewAnswer = (MaterialTextView) cardViewAnswer.getChildAt(0);
        textViewAnswer.setText(String.valueOf(problemPair.getValue()));
        timeProgress.setProgress(100);
        CountDownTimer roundTimer = setupProblemCountDown();
        setupButtonSubmitAnswer(roundTimer);
        roundTimer.start();

    }

    private void setupOptionCards(int answerValue) {
        for (View cardAsView : options) {
            MaterialCardView optionCardView = (MaterialCardView) cardAsView;
            optionCardView.setCardBackgroundColor(getColorStateListAlt(R.color.option_card_view));
            if (optionCardView.isFocused()) {
                optionCardView.clearFocus();
            }
            MaterialTextView optionTextView = (MaterialTextView) optionCardView.getChildAt(0);
            int randomFalseAnswerValue = new Random().nextInt(MAX_RAND_NUM);
            // to prevent the same duplicating answer value
            while (answerValue == randomFalseAnswerValue) {
                answerValue = new Random().nextInt(MAX_RAND_NUM);
            }
            optionTextView.setText(String.valueOf(randomFalseAnswerValue));

        }
    }

    private Map.Entry<String, Integer> getRandomProblemWithAnswer() {
        final char[] ops = {'+', '-', 'x', '/'};
        Random random = new Random();
        int A = random.nextInt(MAX_RAND_NUM);
        int B = random.nextInt(MAX_RAND_NUM);
        char op = ops[random.nextInt(ops.length)];
        int answer = 0;
        switch (op) {
            case '+':
                answer = A + B;
                break;
            case '-':
                answer = A - B;
                break;
            case 'x':
                answer = A * B;
                break;
            case '/':
                while (A < B) {
                    A = random.nextInt(MAX_RAND_NUM);
                    B = random.nextInt(MAX_RAND_NUM);
                }
                answer = A / B;
                break;
            default:
                Log.e("getProblem", "failed to make problem's answer");

        }

        return new AbstractMap.SimpleEntry(String.format(Locale.ENGLISH,"%d %c %d = ?", A, op, B), answer);
    }


    private void SetupViews() {
        buttonSubmitAnswer = findViewById(R.id.buttonSubmitAnswer);
        buttonLeave = findViewById(R.id.buttonLeaveRound);
        textViewAttempts = findViewById(R.id.textViewAttempts);
        textViewScore = findViewById(R.id.textViewScore);
        textViewProblem = findViewById(R.id.textViewProblem);
        timeProgress = findViewById(R.id.timeProgressBar);
        gridLayoutOptions = findViewById(R.id.gridLayoutOptionsContainer);
        options = gridLayoutOptions.getTouchables();
        buttonLeave.setOnClickListener( view -> new MaterialAlertDialogBuilder(PlayGroundActivity.this,
                R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.round_exit_confirmation_msg)
                .setNeutralButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.cancel())
                .setPositiveButton("Yes", (dialogInterface, i) -> goBackToMenu())
                .show());
    }

    CountDownTimer setupProblemCountDown() {
        return new CountDownTimer(PROBLEM_TIME_IN_MS, 200) {
            boolean timerProgressAlreadyStyled = false;

            int getPercentage(long x) {
                return (int) (x * 100 / PROBLEM_TIME_IN_MS);
            }

            @Override
            public void onTick(long l) {

                timeProgress.setProgressCompat(getPercentage(l), true);

                if (l <= 3000 && !timerProgressAlreadyStyled) {
                    timeProgress.setIndicatorColor(getColorInt(R.color.red_a700));
                    timerProgressAlreadyStyled = true;
                }
            }

            @Override
            public void onFinish() {
                // i need to decrease attempts and check if the player lose
                if (!PlayGroundActivity.this.isFinishing()) {
                    Log.i("User Info", "Out of Time !!");
                    setCardsStyle(false);
                    decreaseAttemptsAndCheckLoosingProb();
                    delayedStartProblem();
                }


            }

        };
    }

    void setupButtonSubmitAnswer(CountDownTimer currentCountDownTimer) {

        buttonSubmitAnswer.setOnClickListener(view -> {
            boolean hasChosenAnswer = false, hasChosenRightAnswer = false;
            //check if the user has pick an answer and check if its True
            for (View cardAsView : options) {
                if (cardAsView.isFocused() && cardAsView.getTag().equals(AnswerTag)) {
                    Log.i("User Info", "User has Chosen On the Right Answer");
                    scoreValue += 10;
                    hasChosenRightAnswer = true;
                    hasChosenAnswer = true;
                    break;
                } else if (cardAsView.isFocused()) {
                    Log.i("User Info", "User has Chosen the Wrong Answer");
                    decreaseAttemptsAndCheckLoosingProb();
                    hasChosenAnswer = true;
                    break;
                }
            }


            if (!hasChosenAnswer) {
                Toast.makeText(PlayGroundActivity.this, R.string.toast_user_didnt_pick, Toast.LENGTH_SHORT).show();
                Log.i("User Info", "User hasn't Chosen Any Answer");
            } else if (!PlayGroundActivity.this.isFinishing()) {
                currentCountDownTimer.cancel();
                buttonSubmitAnswer.setOnClickListener(null);
                setCardsStyle(hasChosenRightAnswer);
                delayedStartProblem();
            }


        });
    }

    void decreaseAttemptsAndCheckLoosingProb() {

        attemptsValue -= 1;
        if (attemptsValue == 0) {
            sharedPreferences = getSharedPreferences("game_info", Context.MODE_PRIVATE);
            if (sharedPreferences.getInt("highestScore", 0) < scoreValue) {
                sharedPreferences.edit().putInt("highestScore", scoreValue).apply();
            }
            exitGameSession();
        }

    }

    private void exitGameSession() {
        new MaterialAlertDialogBuilder(PlayGroundActivity.this,R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(R.string.dialog_title_lose)
                .setMessage(R.string.dialog_lose_message)
                .setPositiveButton(R.string.play_again,(dialogInterface, i) -> {
                    Intent intentBackMenu = new Intent(PlayGroundActivity.this, PlayGroundActivity.class);
                    startActivity(intentBackMenu);
                    finish();
                })
                .setNeutralButton("Leave",(dialogInterface, i) ->{
                    goBackToMenu();
                })
                .setCancelable(false)
                .show();

    }

    private void goBackToMenu() {
        Intent intentBackMenu = new Intent(PlayGroundActivity.this, MenuActivity.class);
        startActivity(intentBackMenu);
        finish();
    }

    int getColorInt(int resId) {

        return getResources().getColor(resId);
    }

    ColorStateList getColorStateListAlt(int resId) {
        return AppCompatResources.getColorStateList(this, resId);
    }

    void setCardsStyle(boolean rightAnswer) {
        for (View cardAsView : options) {
            if (cardAsView.getTag().equals(AnswerTag)) {
                MaterialCardView card = (MaterialCardView) cardAsView;
                card.setCardBackgroundColor(getColorInt(R.color.green_200));
            } else if (!rightAnswer) {
                MaterialCardView card = (MaterialCardView) cardAsView;

                card.setCardBackgroundColor(getColorInt(R.color.red_a300));
            }
        }

    }

    void delayedStartProblem() {
        Handler handler = new Handler();
        handler.postDelayed(this::startProblem, START_PROBLEM_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("User Info", "Play Ground Is Destroying");
    }
}
