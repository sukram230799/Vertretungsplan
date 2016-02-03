package wuest.markus.vertretungsplan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AutoStart extends BroadcastReceiver {
    AlarmVP alarmVP = new AlarmVP();
    AlarmSP alarmSP = new AlarmSP();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AutoStart.Intent", intent.getAction());
        Log.d("AutoStart", Intent.ACTION_BOOT_COMPLETED);
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.UPDATE), true)) {
                Log.d("AutoStart", "AutoStart");
                DBHandler handler = new DBHandler(context, null, null, 1);
                handler.removeVP(new HWGrade(Preferences.readStringFromPreferences(context, context.getString(R.string.PREF_FILE_NAME), "NULL")));
                if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.DEVELOPER_MODE), false)) {
                    Toast.makeText(context, "Autostart", Toast.LENGTH_SHORT).show();
                }
                alarmVP.SetAlarm(context);
                context.startService(new Intent(context, UpdateDataSet.class));
            }
            //alarmSP.SetAlarm(context);
        }
    }
}
