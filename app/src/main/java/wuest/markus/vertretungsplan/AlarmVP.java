package wuest.markus.vertretungsplan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmVP extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        // Put here YOUR code.
        if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.UPDATE), true)) {
            Log.d("AlarmVP", "Setting AlarmVP");
            context.startService(new Intent(context, UpdateDataSet.class));
            if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.DEVELOPER_MODE), false)) {
                Toast.makeText(context, "AlarmVP !!!!!!!!!!", Toast.LENGTH_SHORT).show(); // For example
            }
        }
        wl.release();
    }

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmVP.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        int repeatTime;
        /*if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.DEVELOPER_MODE), false)) {
            repeatTime = 1;
        } else {*/
        try {
            repeatTime = Integer.parseInt(Preferences.readStringFromPreferences(context, context.getString(R.string.UPDATE_TIME), context.getString(R.string.UPDATE_TIME_STANDARD)));
            if (repeatTime < 1) {
                repeatTime = 15;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            repeatTime = 15;
        }
        //}
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * repeatTime, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmVP.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}