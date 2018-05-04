package chalier.yohan.dart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

public class GameActivity extends AppCompatActivity {

    private DartPickerFragment picker1;
    private DartPickerFragment picker2;
    private DartPickerFragment picker3;

    private int playerCount;
    private int playerUnfinished;
    private int turn;
    private int[] scores;
    private int[] results;
    private int currentPlayer;
    private String[] playerNames;

    private StackBuffer<PlayerTurnScore> history;

    private final static int HISTORY_SIZE = 5;

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

        history = new StackBuffer<>();
        history.setMaxSize(HISTORY_SIZE);

        picker1 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker1);
        picker2 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker2);
        picker3 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker3);

        final GameActivity self = this;

        findViewById(R.id.buttonNextPlayer).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.update();
            }
        });

        findViewById(R.id.buttonUndo).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!history.isEmpty()) {
                    undo(history.pop());
                }
            }
        });

        update();

    }

    private int evaluateCurrentPlayerScore() {
        return picker1.evaluate() + picker2.evaluate() + picker3.evaluate();
    }

    private PlayerTurnScore evaluateCurrentPlayerTurnScore() {
        int score = evaluateCurrentPlayerScore();
        boolean overScored = scores[currentPlayer] < score;
        boolean finished = scores[currentPlayer] - score == 0;
        return new PlayerTurnScore(score, overScored, finished);
    }

    private void updateCurrentPlayerScore() {
        // currentPlayer is -1 at start, so we need to skip this bit to init,
        // and we only compute for players that have no terminated yet.
        if (currentPlayer >= 0 && scores[currentPlayer] > 0) {
            PlayerTurnScore score = evaluateCurrentPlayerTurnScore();
            history.push(score);
            if (!score.overScored) {
                scores[currentPlayer] -= score.score;
            }
            if (score.finished) {
                playerUnfinished--;
                results[currentPlayer] = turn;
            }
        }
    }

    private void undo(PlayerTurnScore score) {
         if (!score.overScored) {
             scores[score.player] += score.score;
         }
         if (score.finished) {
             results[score.player] = -1;
             playerUnfinished++;
         }
         while (playerUnfinished > 0) {
             currentPlayer--;
             if (currentPlayer < 0) {
                 currentPlayer = playerCount - 1;
                 turn--;
             }
             if (scores[currentPlayer] > 0) {
                 break;
             }
         }
         updateView();
    }

    private void goToNextPlayer() {
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
    }

    private ArrayList<Integer> computeRanking() {
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
        return ranks;
    }

    private void updateView() {

        ArrayList<Integer> ranks = computeRanking();

        TextView playerLabel = findViewById(R.id.textViewPlayerName);
        TextView scoreLabel = findViewById(R.id.textViewPlayerScore);
        TextView rankLabel = findViewById(R.id.textViewPlayerRank);

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

    private void update() {

        updateCurrentPlayerScore();
        goToNextPlayer();

        updateView();
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

    public class StackBuffer<E> extends ArrayList<E> {

        private int maxSize;

        void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        void push(E element) {
            super.add(element);
            if (size() > maxSize) {
                remove(0);
            }
        }

        public E pop() {
            if (size() > 0) {
                return remove(size() - 1);
            }
            return null;
        }

        public boolean isEmpty() {
            return size() == 0;
        }

    }

    private class PlayerTurnScore {
        private int player;
        private int score;
        private boolean overScored;
        private boolean finished;

        PlayerTurnScore
                (int score, boolean overScored, boolean finished) {
            this.player = currentPlayer;
            this.score = score;
            this.overScored = overScored;
            this.finished = finished;
        }

    }
}

