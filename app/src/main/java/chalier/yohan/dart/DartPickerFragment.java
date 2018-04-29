package chalier.yohan.dart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

public class DartPickerFragment extends Fragment {

    private final static String[] VALUES = {"OUT", "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
            "19", "20", "25", "50"};

    private NumberPicker picker;
    private ToggleButton toggleDouble;
    private ToggleButton toggleTriple;

    public DartPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dart_picker,
                container, false);

        picker = rootView.findViewById(R.id.numberPicker1);
        picker.setDisplayedValues(VALUES);
        picker.setMinValue(0);
        picker.setMaxValue(VALUES.length-1);

        toggleDouble = rootView.findViewById(R.id.toggleButton1);
        toggleTriple = rootView.findViewById(R.id.toggleButton2);

        toggleDouble.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean b) {
                if (b) {
                    toggleTriple.setChecked(false);
                }
            }
        });

        toggleTriple.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton,
                                         boolean b) {
                if (b) {
                    toggleDouble.setChecked(false);
                }
            }
        });

        return rootView;
    }

    public int evaluate() {
        int value = 0;
        int index = picker.getValue();
        if (index > 0) {
            value = Integer.parseInt(VALUES[index]);
        }
        if (toggleDouble.isChecked()) {
            return 2 * value;
        } else if (toggleTriple.isChecked()) {
            return 3 * value;
        }
        return value;
    }

}
