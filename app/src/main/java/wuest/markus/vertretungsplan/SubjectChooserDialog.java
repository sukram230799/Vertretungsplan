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

/**
 * Created by Markus on 16.02.2016.
 */
public class SubjectChooserDialog extends DialogFragment implements SubjectChooserAdapter.OnCheckedChangeListener {

    private ListView listView;
    private SubjectChooserAdapter adapter;

    private String[] subjects;

    private ArrayList<String> selectedSubjects;
    private OnReloadData onReloadData;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_subject_chooser_dialog, null);
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
        listView = (ListView) view.findViewById(R.id.list_view);
        //adapter = new SubjectChooserAdapter(subjects, getActivity());
        adapter = new SubjectChooserAdapter(getActivity(), R.id.list_view, Arrays.asList(subjects));
        adapter.setSelectedSubjects(selectedSubjects);
        adapter.setOnCheckedChangeListener(this);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Konfiguration");

        builder.setView(view);

        builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DBHandler dbHandler = new DBHandler(getActivity(), null, null, 0);
                dbHandler.setSubscribedSubjects(selectedSubjects.toArray(new String[selectedSubjects.size()]));
                if(onReloadData != null){
                    onReloadData.reloadData();
                }
            }
        });
        return builder.show();
    }

    @Override
    public void onCheckedChanged(int position, boolean isChecked) {
        String subject = subjects[position];
        if(isChecked){
            if(!selectedSubjects.contains(subject)){
                selectedSubjects.add(subject);
            }
        } else {
            selectedSubjects.remove(subject);
        }
    }

    public void setOnReloadData(OnReloadData onReloadData){
        this.onReloadData = onReloadData;
    }

    public interface OnReloadData{
        void reloadData();
    }
}
