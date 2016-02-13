package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class ConfigureDialog extends DialogFragment {

    private Context context;

    private TextInputLayout gradeInputLayout;
    private TextInputLayout groupInputLayout;

    private AutoCompleteTextView gradeInput;
    private AutoCompleteTextView groupInput;

    private SaveDataInterface saveDataInterface;

    public ConfigureDialog() {
        Log.d("D", String.valueOf(getActivity()));
        this.context = getActivity();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_configure_dialog, null);

        gradeInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_grade);
        groupInputLayout = (TextInputLayout) view.findViewById(R.id.input_layout_group);
        gradeInput = (AutoCompleteTextView) view.findViewById(R.id.input_grade);
        groupInput = (AutoCompleteTextView) view.findViewById(R.id.input_group);

        gradeInput.setText(Preferences.readStringFromPreferences(getActivity(), getString(R.string.SELECTED_GRADE), ""));
        groupInput.setText(Preferences.readStringFromPreferences(getActivity(), getString(R.string.SELECTED_GROUP), ""));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Konfiguration");

        builder.setView(view);

        builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialogInterface.onValueSaved(startHour.getValue(), endHour.getValue());
                if (groupInput.getText().toString().trim().length() > 0 &&
                        gradeInput.getText().toString().trim().length() > 0) {
                    Preferences.saveStringToPreferences(getActivity(), getString(R.string.SELECTED_GROUP), groupInput.getText().toString().trim());
                    Preferences.saveStringToPreferences(getActivity(), getString(R.string.SELECTED_GRADE), gradeInput.getText().toString().trim());
                    dismiss();
                    if (saveDataInterface != null) {
                        saveDataInterface.dataSaved();
                    }
                }
            }
        });
        //builder.setCustomTitle(view);
        //return super.onCreateDialog(savedInstanceState);
        return builder.show();
    }

    public void setSaveDataInterface(SaveDataInterface saveDataInterface) {
        this.saveDataInterface = saveDataInterface;
    }

    public interface SaveDataInterface {
        void dataSaved();
    }
}
