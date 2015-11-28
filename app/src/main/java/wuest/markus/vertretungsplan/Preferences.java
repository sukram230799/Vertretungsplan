package wuest.markus.vertretungsplan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class Preferences {

    public static void saveStringToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.commit(); //or apply
    }

    public static String readStringFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static void saveBooleanToPreferences(Context context, String preferenceName, boolean preferenceValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.commit();
    }

    public static boolean readBooleanFromPreferences(Context context, String preferenceName, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }

    public static void saveIntToPreferences(Context context, String preferenceName, int preferenceValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(preferenceName, preferenceValue);
        editor.commit();
    }

    public static int readIntFromPreferences(Context context, String preferenceName, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(preferenceName, defaultValue);
    }

    public static void saveLongToPreferences(Context context, String preferenceName, long preferenceValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(preferenceName, preferenceValue);
        editor.commit();
    }

    public static long readLongFromPreferences(Context context, String preferenceName, long defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(preferenceName, defaultValue);
    }

    public static void saveFloatToPreferences(Context context, String preferenceName, float preferenceValue){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(preferenceName, preferenceValue);
        editor.commit();
    }

    public static float readFloatFromPreferences(Context context, String preferenceName, float defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //sharedPreferences = context.getSharedPreferences(MainActivity.PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(preferenceName, defaultValue);
    }
}
