package wuest.markus.vertretungsplan;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The configuration screen for the {@link VPWidget VPWidget} AppWidget.
 */
public class VPWidgetConfigureActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "wuest.markus.vertretungsplan.VPWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    VPWidgetPreviewFragment vpWidgetPreviewFragment;
    FragmentManager fragmentManager;

    //EditText mAppWidgetText;
    Spinner mAppWidgetGradeSpinner;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = VPWidgetConfigureActivity.this;

            if (Preferences.readBooleanFromPreferences(context, getString(R.string.DEVELOPER_MODE), false)) {
                Toast.makeText(context, String.valueOf(mAppWidgetGradeSpinner.getSelectedItem().toString()), Toast.LENGTH_LONG).show();
            }
            saveTitlePref(context, mAppWidgetId, mAppWidgetGradeSpinner.getSelectedItemPosition());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            VPWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public VPWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, int type) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        //prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, type);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadGradePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        /*String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }*/
        try {
            return prefs.getInt(PREF_PREFIX_KEY + appWidgetId, -1);
        } catch (java.lang.ClassCastException e) {
            return -1;
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.vpwidget_configure);

        fragmentManager = getSupportFragmentManager();
        vpWidgetPreviewFragment = VPWidgetPreviewFragment.newInstance(0);
        fragmentManager.beginTransaction()
                .replace(R.id.container, vpWidgetPreviewFragment)
                .commit();

        //mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        mAppWidgetGradeSpinner = (Spinner) findViewById(R.id.grade_select_spinner);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
/*
        Adapter for GradeSelection
        DBHandler dbHandler = new DBHandler(this, null, null, 0);
        List<String> hwGrades = new ArrayList<>();
        try {
            for(HWGrade hwGrade: dbHandler.getGrades()){
                hwGrades.add(hwGrade.get_GradeName());
            }
        } catch (DBError error){
            error.printStackTrace();
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, hwGrades);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/

        List<String> availableWidgetLayouts = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.spinner)));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, availableWidgetLayouts);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mAppWidgetGradeSpinner.setAdapter(arrayAdapter);
        mAppWidgetGradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Update(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //mAppWidgetText.setText(loadGradePref(VPWidgetConfigureActivity.this, mAppWidgetId));
    }

    private void Update(int position) {
        vpWidgetPreviewFragment = VPWidgetPreviewFragment.newInstance(position);
        fragmentManager.beginTransaction()
                .replace(R.id.container, vpWidgetPreviewFragment)
                .commit();
    }
}

