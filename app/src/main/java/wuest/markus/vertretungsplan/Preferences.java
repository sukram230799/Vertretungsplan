package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class Preferences {

    SharedPreferences sharedPreferences;

    public Preferences(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveStringToPreferences(String preferenceName, String preferenceValue) {
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.commit(); //or apply
    }

    public String readStringFromPreferences(String preferenceName, String defaultValue) {
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public void saveBooleanToPreferences(String preferenceName, boolean preferenceValue){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.commit();
    }

    public boolean readBooleanFromPreferences(String preferenceName, boolean defaultValue) {
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }

    public void saveIntToPreferences(String preferenceName, int preferenceValue){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceName, preferenceValue);
        editor.commit();
    }

    public int readIntFromPreferences(String preferenceName, int defaultValue) {
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(preferenceName, defaultValue);
    }

    public void saveLongToPreferences(String preferenceName, long preferenceValue){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(preferenceName, preferenceValue);
        editor.commit();
    }

    public long readLongFromPreferences(String preferenceName, long defaultValue) {
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(preferenceName, defaultValue);
    }

    public void saveFloatToPreferences(String preferenceName, float preferenceValue){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(preferenceName, preferenceValue);
        editor.commit();
    }

    public float readFloatFromPreferences(String preferenceName, float defaultValue) {
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(preferenceName, defaultValue);
    }
}
