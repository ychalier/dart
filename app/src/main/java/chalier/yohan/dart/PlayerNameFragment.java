package chalier.yohan.dart;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class PlayerNameFragment extends Fragment {

    private String hint;
    private EditText editText;
    private FragmentDeleteListener listener;

    public PlayerNameFragment() {
        // Required empty public constructor
    }

    public static PlayerNameFragment newInstance(
            String hint, FragmentDeleteListener listener) {
        PlayerNameFragment f = new PlayerNameFragment();
        f.setHint(hint);
        f.setListener(listener);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_player_name,
                container, false);

        editText = rootView.findViewById(R.id.editTextPlayerName);
        if (hint != null) {
            editText.setHint(hint);
        }

        final PlayerNameFragment self = this;

        rootView.findViewById(R.id.buttonClearPlayer).setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.deleteFragment(self);
                }
            }
        });

        return rootView;
    }

    public void setListener(FragmentDeleteListener listener) {
        this.listener = listener;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getName() {
        if (editText.getText().toString().isEmpty()) {
            return hint;
        }
        return editText.getText().toString();
    }

    public interface FragmentDeleteListener {
        void deleteFragment(PlayerNameFragment fragment);
    }

}
