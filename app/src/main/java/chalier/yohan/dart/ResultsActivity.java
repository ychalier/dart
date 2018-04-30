package chalier.yohan.dart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent intent = getIntent();
        int[] scores = intent.getIntArrayExtra("scores");

        int[] players = new int[scores.length];
        for (int i = 0; i < scores.length; i++) {
            players[i] = i + 1;
        }
        for (int i = 1; i < scores.length; i++) {
            int j = i;
            while (j > 0 && scores[players[j-1] - 1] > scores[players[j] - 1]) {
                j--;
                int aux = players[j-1];
                players[j-1] = players[j];
                players[j] = aux;
            }
        }

        TextView textView = findViewById(R.id.textView);

        String text = "Player " + players[0] + " wins!\n\n";

        for (int i = 1; i < scores.length; i++) {
            text += (i + 1) + ". Player " + players[i] + " (" + scores[i] + ")\n";
        }

        textView.setText(text);

    }
}
