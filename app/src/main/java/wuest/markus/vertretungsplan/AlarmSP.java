package wuest.markus.vertretungsplan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmSP extends BroadcastReceiver {

    private static final String TAG = "AlarmSP";

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        // Put here YOUR code.
        if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.UPDATE), true)) {
            Log.d(TAG, "Setting AlarmSP");
            SetAlarm(context);
            context.startService(new Intent(context, TimeTableService.class));
            if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.DEVELOPER_MODE), false)) {
                Toast.makeText(context, "AlarmSP !!!!!!!!!!", Toast.LENGTH_SHORT).show(); // For example
            }
        }
        wl.release();
    }

    public void SetAlarm(Context context) {
        Log.d(TAG, "SetAlarm");
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmSP.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        int repeatTime;

        HWGrade grade = new HWGrade(Preferences.readStringFromPreferences(context, context.getString(R.string.SELECTED_GRADE), null));
        GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();

        HWTime currentTime = new HWTime(calendar);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int nextHour;
        nextHour = TimeTableHelper.getNextHour(currentTime);
        Log.d(TAG, String.valueOf(System.currentTimeMillis()));

        DBHandler dbHandler = new DBHandler(context, null, null, 0);
        /*HWLesson[] lessons = new HWLesson[0];
        try {
            lessons = TimeTableHelper.selectLessonsFromRepeatType(dbHandler.getTimeTable(grade, day), week, context);
        } catch (DBError error) {
            error.printStackTrace();
        }*/

        if (grade.getGradeName() != null) {
            long time = TimeTableHelper.getHourTime(nextHour, currentTime)[0].getTimeInMillis() - (20 * 60 * 1000);
            Log.d(TAG, String.valueOf(time));
            Log.d(TAG, String.valueOf(System.currentTimeMillis() - time));
            Log.d(TAG, String.valueOf((System.currentTimeMillis() - time) / 1000));
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            Log.d(TAG, new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(c.getTime()));
            am.set(AlarmManager.RTC_WAKEUP, time, pi); //20 minutes before new Lesson
        }
    }

    public void CancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmSP.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
