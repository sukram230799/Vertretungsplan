package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class GradeListPreference extends ListPreference {
    public static final String TAG = "GradeListPreference";

    public GradeListPreference(Context context) {
        super(context);
    }

    public GradeListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateDialogView() {
        Log.v(TAG, "onCreateDialogView");
        ListView view = new ListView(getContext());
        view.setAdapter(adapter());
        HWGrade[] hwGrades = new HWGrade[0];
        try {
            hwGrades = new DBHandler(getContext(), null, null, 1).getGrades();
        } catch (DBError dbError) {
            dbError.printStackTrace();
        }
        ArrayList<String> grades = new ArrayList<>();
        for(HWGrade hwGrade : hwGrades){
            grades.add(hwGrade.getGradeName());
        }
        String[] entries = grades.toArray(new String[grades.size()]);
        setEntries(entries);
        setEntryValues(entries);
        onSetInitialValue(true, entries[3]);
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        Log.v(TAG, positiveResult + "");
        SharedPreferences preferences = getSharedPreferences();
        String pref = preferences.getString(getKey(), "NULL");
        Log.v(TAG, pref);
    }

    private ListAdapter adapter() {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice);
        adapter.setNotifyOnChange(true);
        return adapter;
        //return new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice);
    }

/*
    private CharSequence[] entries() {
        //action to provide entry data in char sequence array for list
    }

    private CharSequence[] entryValues() {
        //action to provide value data for list
    }*/
}
