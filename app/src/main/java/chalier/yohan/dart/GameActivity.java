package chalier.yohan.dart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

public class GameActivity extends AppCompatActivity {

    private DartPickerFragment picker1;
    private DartPickerFragment picker2;
    private DartPickerFragment picker3;

    private TextView playerLabel;
    private TextView scoreLabel;
    private TextView rankLabel;

    private int playerCount;
    private int playerUnfinished;
    private int turn;
    private int[] scores;
    private int[] results;
    private int currentPlayer;
    private String[] playerNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        playerCount = intent.getIntExtra(
                HomeActivity.EXTRA_PLAYER_COUNT, 2);
        int startScore = intent.getIntExtra(
                HomeActivity.EXTRA_START_SCORE, 301);
        playerUnfinished = playerCount;
        playerNames = intent.getStringArrayExtra(
                HomeActivity.EXTRA_PLAYER_NAMES);

        scores = new int[playerCount];
        results = new int[playerCount];
        for (int i = 0; i < scores.length; i++) {
            scores[i] = startScore;
            results[i] = -1;
        }

        turn = 0;
        currentPlayer = -1;

        picker1 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker1);
        picker2 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker2);
        picker3 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker3);

        playerLabel = findViewById(R.id.textViewPlayerName);
        scoreLabel = findViewById(R.id.textViewPlayerScore);
        rankLabel = findViewById(R.id.textViewPlayerRank);
        // scoreboardNames = findViewById(R.id.textViewScoreboardNames);
        // scoreboardScores = findViewById(R.id.textViewScoreboardScores);

        final GameActivity self = this;
        Button button = findViewById(R.id.buttonNextPlayer);
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

        // Computing score of current player
        if (currentPlayer >= 0 && scores[currentPlayer] > 0) {
            int score = evaluate();
            if (scores[currentPlayer] - score >= 0) {
                scores[currentPlayer] -= score;
            }
            if (scores[currentPlayer] == 0) {
                playerUnfinished--;
                results[currentPlayer] = turn;

                // Disable NEXT button when everything is over
                // It prevents the user from starting an infinite loop
                if (playerUnfinished == 0) {
                    findViewById(R.id.buttonNextPlayer).setEnabled(false);
                }
            }
        }

        // Find next player who has not finished yet
        // The condition is always true or never entered
        while (playerUnfinished > 0) {
            currentPlayer = (currentPlayer + 1) % playerCount;
            if (currentPlayer == 0) {
                turn++;
            }
            if (scores[currentPlayer] > 0) {
                break;
            }
        }

        picker1.reset();
        picker2.reset();
        picker3.reset();

        playerLabel.setText(String.format(
                getResources().getString(R.string.player_turn),
                playerNames[currentPlayer]));
        scoreLabel.setText(String.format(
                getResources().getString(R.string.score_label),
                scores[currentPlayer]));

        setTitle(String.format(
                getResources().getString(R.string.turn_label),
                turn));

        ArrayList<Integer> ranks = new ArrayList<>();
        for (int i = 0; i < playerCount; i++) {
            ranks.add(i);
        }

        ranks.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {

                if (results[integer] > -1 && results[t1] == -1) {
                    return -1;
                } else if (results[integer] == -1 && results[t1] > -1) {
                    return 1;
                } else if (results[integer] == -1 && results[t1] == -1) {
                    return Integer.compare(scores[integer], scores[t1]);
                } else {
                    return Integer.compare(results[integer], results[t1]);
                }

            }
        });

        LinearLayout scoreboardLayout = findViewById(R.id.layoutScoreboard);
        scoreboardLayout.removeAllViews();
        for (int index : ranks) {
            int playerRank = ranks.indexOf(index) + 1;

            @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(
                    R.layout.scoreboard_row,
                    null);
            TextView player = v.findViewById(R.id.textViewScoreboardPlayerName);
            TextView score = v.findViewById(R.id.textViewScoreboardPlayerScore);

            player.setText(String.format(
                    getResources().getString(R.string.scoreboard_header),
                    rankSuffixed(playerRank), playerNames[index]));
            if (results[index] == -1) {
                score.setText(String.format(
                        getResources().getString(R.string.score_label),
                        scores[index]));
            } else {
                score.setText(String.format(
                        getResources().getString(R.string.done_label),
                        results[index]));
            }

            scoreboardLayout.addView(v);
        }

        rankLabel.setText(String.format(
                getResources().getString(R.string.rank),
                rankSuffixed(ranks.indexOf(currentPlayer) + 1)));
    }

    private String rankSuffixed(int rank) {
        String strRank = Integer.toString(rank);
        char lastChar = strRank.charAt(strRank.length() - 1);
        if (lastChar == '1' && rank != 11) {
            return strRank + "st";
        } else if (lastChar == '2' && rank != 12) {
            return strRank + "nd";
        } else if (lastChar == '3' && rank != 13) {
            return strRank + "rd";
        } else {
            return strRank + "th";
        }
    }

}
