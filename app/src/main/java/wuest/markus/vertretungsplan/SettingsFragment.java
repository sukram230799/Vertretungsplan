package wuest.markus.vertretungsplan;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String key = getString(R.string.UPDATE_TIME);
        if (!Preferences.readBooleanFromPreferences(getActivity(), getString(R.string.DEVELOPER_MODE), false)) {
            addPreferencesFromResource(R.xml.preferences);
        } else {
            addPreferencesFromResource(R.xml.preferences_dev);
        }
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
            if (!(etp.getText() == null) && !etp.getText().equals("") && !etp.getText().equals("null")) {
                pref.setSummary(getString(R.string.UPDATE_TIME_TEXT, etp.getText()));
            } else {
                pref.setSummary(getString(R.string.UPDATE_TIME_STANDARD_TEXT, getString(R.string.UPDATE_TIME_STANDARD)));
            }
        }
    }
}
