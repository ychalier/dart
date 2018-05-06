package chalier.yohan.dart;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class HomeActivity
        extends AppCompatActivity
        implements PlayerNameFragment.FragmentDeleteListener {

    private final static String[] DEFAULT_NAMES = {"Jack", "John", "Jason",
            "James", "Jake", "Jamie", "Joshua", "Jackson", "Joe", "Jacob"};

    public final static String EXTRA_PLAYER_COUNT = "playerCount";
    public final static String EXTRA_START_SCORE = "startScore";
    public final static String EXTRA_PLAYER_NAMES = "playerNames";

    private ArrayList<PlayerNameFragment> playerNames;
    private LinearLayout playerNamesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        playerNames = new ArrayList<>();
        playerNamesLayout = findViewById(R.id.layoutPlayerNames);

        final HomeActivity self = this;

        findViewById(R.id.buttonAddPlayer).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                self.appendFragment();
            }
        });

        findViewById(R.id.buttonResetPlayers).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                while (playerNames.size() > 0) {
                    self.deleteFragment(playerNames.get(0));
                }
                self.appendFragment();
            }
        });

        self.appendFragment();

        /*
        findViewById(R.id.buttonStart).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.start, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_start:
                start();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public void appendFragment() {
        PlayerNameFragment fragment =
                PlayerNameFragment.newInstance(
                        DEFAULT_NAMES[playerNames.size()%DEFAULT_NAMES.length],
                        this);
        playerNames.add(fragment);

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(playerNamesLayout.getId(),
                fragment,"fragment");
        fragmentTransaction.commit();
    }

    @Override
    public void deleteFragment(PlayerNameFragment fragment) {
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        playerNames.remove(fragment);
        fragmentTransaction.commit();
    }

    public void start() {
        EditText editStartScore = findViewById(R.id.editTextStartScore);
        int startScore = 501;
        if (!editStartScore.getText().toString().isEmpty()) {
            startScore = Integer.parseInt(
                    editStartScore.getText().toString());
        }
        int playerCount = playerNames.size();
        String[] names = new String[playerCount];
        for (int i = 0; i < playerCount; i++) {
            names[i] = playerNames.get(i).getName();
        }

        Log.d("Home", "Player count: " + playerCount);
        Log.d("Home", "Start score: " + startScore);

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_PLAYER_COUNT, playerCount);
        intent.putExtra(EXTRA_PLAYER_NAMES, names);
        intent.putExtra(EXTRA_START_SCORE, startScore);
        startActivity(intent);
    }
}
