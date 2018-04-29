package chalier.yohan.dart;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {

    private int playerCount;
    private int startScore;

    private DartPickerFragment picker1;
    private DartPickerFragment picker2;
    private DartPickerFragment picker3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        this.playerCount = intent.getIntExtra("playerCount", 2);
        this.startScore = intent.getIntExtra("startScore", 301);

        picker1 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker1);
        picker2 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker2);
        picker3 = (DartPickerFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentDartPicker3);

        Button button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Game", "Turn score:" + Integer.toString(evaluate()));
            }
        });

    }

    private int evaluate() {
        return picker1.evaluate() + picker2.evaluate() + picker3.evaluate();
    }

}
