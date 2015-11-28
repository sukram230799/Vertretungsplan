package wuest.markus.vertretungsplan;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        /*final ListPreference listPreference = (ListPreference) findPreference("selected_grade");
        listPreference.setEntries(new String[]{"Eins", "Zwei", "Drei"});
        listPreference.setEntryValues(new String[]{"Eins", "Zwei", "Drei"});
        listPreference.setDefaultValue("Eins");
        setListPreferenceData(listPreference);*/
    }

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

}
