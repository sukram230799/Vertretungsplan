package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = getString(R.string.UPDATE_TIME);
        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        EditTextPreference editTextPref = (EditTextPreference) findPreference(key);
        editTextPref
                .setSummary(sp.getString(key, "Standard: 15min"));
        onSharedPreferenceChanged(sp, key);
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference) {
            EditTextPreference etp = (EditTextPreference) pref;
            if (!etp.getText().equals("") && !etp.getText().equals(null) && !etp.getText().equals("null")) {
                pref.setSummary(getString(R.string.UPDATE_TIME_TEXT, etp.getText()));
            } else {
                pref.setSummary(getString(R.string.UPDATE_TIME_STANDARD_TEXT, getString(R.string.UPDATE_TIME_STANDARD)));
            }
        }
    }
}
