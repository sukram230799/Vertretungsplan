package wuest.markus.vertretungsplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AutoStart extends BroadcastReceiver {
    Alarm alarm = new Alarm();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            DBHandler handler = new DBHandler(context, null, null, 1);
            Preferences preferences = new Preferences(context);
            handler.removeVP(new HWGrade(preferences.readStringFromPreferences(context.getString(R.string.PREF_FILE_NAME), "NULL")));
            if (preferences.readBooleanFromPreferences(context.getString(R.string.TOAST_ENABLED), false)) {
                Toast.makeText(context, "Autostart", Toast.LENGTH_SHORT).show();
            }
            alarm.SetAlarm(context);
            //context.startService(new Intent(context, UpdateDataSet.class));
        }
    }
}
