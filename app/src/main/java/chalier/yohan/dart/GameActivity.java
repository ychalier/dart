package chalier.yohan.dart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {

    private int playerCount;
    private int startScore;

    private DartPickerFragment picker1;
    private DartPickerFragment picker2;
    private DartPickerFragment picker3;

    private TextView playerLabel;
    private TextView scoreLabel;
    private TextView scoreboard;

    private int[] scores;
    private int currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        playerCount = intent.getIntExtra("playerCount", 2);
        startScore = intent.getIntExtra("startScore", 301);

        scores = new int[playerCount];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = startScore;
        }

        currentPlayer = -1;

        picker1 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker1);
        picker2 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker2);
        picker3 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker3);

        playerLabel = findViewById(R.id.textView2);
        scoreLabel = findViewById(R.id.textView3);
        scoreboard = findViewById(R.id.textView4);

        final GameActivity self = this;
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.update();
            }
        });

        update();

    }

    private int evaluate() {
        return picker1.evaluate() + picker2.evaluate() + picker3.evaluate();
    }

    private void update() {
        if (currentPlayer >= 0) {
            int score = evaluate();
            if (scores[currentPlayer] - score >= 0) {
                scores[currentPlayer] -= score;
            }
            if (scores[currentPlayer] == 0) {
                Intent intent = new Intent(this, ResultsActivity.class);
                intent.putExtra("scores", scores);
                startActivity(intent);
            }
        }
        currentPlayer = (currentPlayer + 1) % playerCount;
        playerLabel.setText("Player " + (currentPlayer + 1));
        scoreLabel.setText(Integer.toString(scores[currentPlayer]));

        picker1.reset();
        picker2.reset();
        picker3.reset();

        String scoreboardText = "";
        for (int i = 0; i < playerCount; i++) {
            scoreboardText += "Player " + (i + 1) + ": " + scores[i] + "\n";
        }
        scoreboard.setText(scoreboardText);
    }

}
