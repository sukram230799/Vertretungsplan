package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
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

        gradeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String grade = gradeInput.getText().toString().trim();
                if (grade.contains("/")) {
                    gradeInputLayout.setError("'/' durch '-' ersetzen");
                    gradeInputLayout.setErrorEnabled(true);
                } else if (grade.length() == 0) {
                    gradeInputLayout.setError(getString(R.string.fillwith_builder, ""));
                    gradeInputLayout.setErrorEnabled(true);
                } else {
                    gradeInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        groupInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (groupInput.getText().toString().trim().length() == 0) {
                    groupInputLayout.setError("'-', wenn keine Gruppe");
                    groupInputLayout.setErrorEnabled(true);
                } else {
                    groupInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Konfiguration");

        builder.setView(view);

        builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dialogInterface.onValueSaved(startHour.getValue(), endHour.getValue());
                if (groupInput.getText().toString().trim().length() > 0 &&
                        gradeInput.getText().toString().trim().length() > 0 &&
                        !groupInputLayout.isErrorEnabled() && !groupInputLayout.isErrorEnabled()) {
                    Preferences.saveStringToPreferences(getActivity(), getString(R.string.SELECTED_GROUP), groupInput.getText().toString().trim());
                    Preferences.saveStringToPreferences(getActivity(), getString(R.string.SELECTED_GRADE), gradeInput.getText().toString().trim());
                    DBHandler handler = new DBHandler(getActivity(), null, null, 0);
                    try {
                        HWGrade[] grades = handler.getGrades();
                        boolean equals = false;
                        for (HWGrade grade : grades) {
                            if (grade.getGradeName().equals(gradeInput.getText().toString().trim())) {
                                equals = true;
                                break;
                            }
                        }
                        if (!equals) {
                            handler.addGrade(new HWGrade(gradeInput.getText().toString().trim()));
                        }
                    } catch (DBError error) {
                        error.printStackTrace();
                        handler.addGrade(new HWGrade(gradeInput.getText().toString().trim()));
                    }
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
