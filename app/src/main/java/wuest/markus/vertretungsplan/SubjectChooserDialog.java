package wuest.markus.vertretungsplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class SubjectChooserDialog extends DialogFragment {

    private String[] subjects;
    private ArrayList<String> selectedSubjects;

    private ArrayList<Integer> mSelectedItems;
    private OnReloadData onReloadData;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        try {
            DBHandler dbHandler = new DBHandler(getActivity(), null, null, 0);
            HWLesson[] lessons = dbHandler.getTimeTable(new HWGrade(Preferences.readStringFromPreferences(getActivity(), getActivity().getString(R.string.SELECTED_GRADE), "")));
            subjects = TimeTableHelper.findSubscribableSubjects(lessons);
            selectedSubjects = new ArrayList<>(Arrays.asList(dbHandler.getSubscribedSubjects()));
        } catch (DBError error) {
            error.printStackTrace();
            subjects = new String[0];
            selectedSubjects = new ArrayList<>();
        }
        boolean[] selectedItem = new boolean[subjects.length];
        mSelectedItems = new ArrayList();  // Where we track the selected items
        for (int i = 0; i < subjects.length; i++) {
            String subject = subjects[i];
            boolean found = false;
            for (String selectedSubject : selectedSubjects) {
                if (subject.equals(selectedSubject)) {
                    found = true;
                    mSelectedItems.add(i);
                    break;
                }
            }
            selectedItem[i] = found;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Wahlfächer wählen")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(subjects, selectedItem,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        DBHandler dbHandler = new DBHandler(getActivity(), null, null, 0);
                        selectedSubjects = new ArrayList<>();
                        for (int position : mSelectedItems) {
                            selectedSubjects.add(subjects[position]);
                        }
                        dbHandler.setSubscribedSubjects(selectedSubjects.toArray(new String[selectedSubjects.size()]));
                        if (onReloadData != null) {
                            onReloadData.reloadData();
                        }
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        onReloadData.reloadData();
                    }
                });

        return builder.create();
    }

    public void setOnReloadData(OnReloadData onReloadData) {
        this.onReloadData = onReloadData;
    }

    public interface OnReloadData {
        void reloadData();
    }
}
