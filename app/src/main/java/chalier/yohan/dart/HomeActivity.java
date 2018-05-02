package chalier.yohan.dart;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button button = findViewById(R.id.buttonStart);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GameActivity.class);

                EditText editPlayerCount = findViewById(R.id.editTextPlayerCount);
                EditText editStartScore = findViewById(R.id.editTextStartScore);

                String playerCount = editPlayerCount.getText().toString();
                String startScore = editStartScore.getText().toString();

                if (!playerCount.isEmpty()) {
                    intent.putExtra("playerCount", Integer.valueOf(playerCount));
                }

                if (!startScore.isEmpty()) {
                    intent.putExtra("startScore", Integer.valueOf(startScore));
                }

                startActivity(intent);
            }
        });


    }
}
