package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

public class HourPickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {

    private NumberPicker startHour;
    private NumberPicker endHour;
    private static final String startHourName = "startHour";
    private static final String endHourName = "endHour";
    private int startHourValue = 1;
    private int endHourValue = 1;

    static Context context;
    private NumberDialogInterface dialogInterface;

//    private View view;
    private View coordinatorView;

    public static HourPickerDialog newInstance(int startHour, int endHour) {

        Bundle args = new Bundle();
        args.putInt(startHourName, startHour);
        args.putInt(endHourName, endHour);
        HourPickerDialog fragment = new HourPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public HourPickerDialog() {
        Log.d("D", String.valueOf(getActivity()));
        this.context = getActivity();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.fragment_hour_picker_dialog, container);
        View view = inflater.inflate(R.layout.fragment_hour_picker_dialog, container);

        startHour = (NumberPicker) view.findViewById(R.id.numberPickerStart);
        endHour = (NumberPicker) view.findViewById(R.id.numberPickerEnd);

        startHour.setMinValue(1);
        startHour.setMaxValue(15);
        endHour.setMinValue(1);
        endHour.setMaxValue(15);

        startHour.setOnValueChangedListener(this);
        this.view = view;
        return view;
    }*/

    public void setNumberDialogInterface(HourPickerDialog.NumberDialogInterface dialogInterface){
        this.dialogInterface = dialogInterface;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if(savedInstanceState != null){
            startHourValue = savedInstanceState.getInt(startHourName);
            endHourValue = savedInstanceState.getInt(endHourName);
        } else if(arguments != null){
            startHourValue = arguments.getInt(startHourName);
            endHourValue = arguments.getInt(endHourName);
        }
        //View view = inflater.inflate(R.layout.fragment_hour_picker_dialog, container);
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_hour_picker_dialog, null);

        startHour = (NumberPicker) view.findViewById(R.id.numberPickerStart);
        endHour = (NumberPicker) view.findViewById(R.id.numberPickerEnd);

        startHour.setMinValue(1);
        startHour.setMaxValue(15);
        startHour.setValue(startHourValue);
        endHour.setMinValue(1);
        endHour.setMaxValue(15);
        endHour.setValue(endHourValue);

        startHour.setOnValueChangedListener(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Bitte Stund wählen");

        builder.setView(view);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogInterface.onValueSaved(startHour.getValue(), endHour.getValue());
                dismiss();
            }
        });
        //builder.setCustomTitle(view);
        //return super.onCreateDialog(savedInstanceState);
        return  builder.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        getDialog().setTitle("Test");
        endHour.setMinValue(newVal);
    }


    public interface NumberDialogInterface{
        void onValueSaved(int startHour, int endHour);
    }
}
