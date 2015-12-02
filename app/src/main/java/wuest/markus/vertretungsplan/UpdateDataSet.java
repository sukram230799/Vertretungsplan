package wuest.markus.vertretungsplan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class UpdateDataSet extends Service {
    VPData[] oldData;
    Alarm alarm = new Alarm();

    public void onCreate() {
        super.onCreate();
    }

    Handler vpHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("vpHandler", "RUN");
            try {
                String message = CombineData.getChanges(CombineData.findChanges(oldData, dbHandler.getVP(grade)));
                if (message != null) {
                    UpdateNotification(context, message);
                }
            } catch (DBError dbError) {
                dbError.printStackTrace();
            }
            stopSelf();
        }
    };

    Handler gradesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("gradesHandler", "RUN");
            if (Preferences.readBooleanFromPreferences(context, getString(R.string.DEVELOPER_MODE), false)) {
                Toast.makeText(context, "UpdateDataSet", Toast.LENGTH_SHORT).show();
            }
            grade = new HWGrade(Preferences.readStringFromPreferences(context, getString(R.string.SELECTED_GRADE), "NULL"));
            if (!grade.get_GradeName().equals("NULL")) {
                dbHandler = new DBHandler(context, null, null, 1);
                try {
                    oldData = dbHandler.getVP(grade);
                } catch (DBError dbError) {
                    oldData = null;
                }
                Thread thread = new Thread(new GetVP(context, grade, vpHandler));
                thread.start();
                //alarm.SetAlarm(this);
            } else {
                stopSelf();
            }
        }
    };

    Context context = this;
    DBHandler dbHandler;
    HWGrade grade;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!MainActivity.foreground || MainActivity.manualupdate) {
            MainActivity.manualupdate = false;
            Log.d("UpdateDataSet", "In there");
            if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.UPDATE), true)) {
                new Thread(new GetGradesFromVPGrades(this, gradesHandler)).start();
            } else {
                stopSelf();
                return START_NOT_STICKY;
            }
        } else {
            Log.d("UpdateDataSet", "MainActivity Active");
            stopSelf();
            return START_NOT_STICKY;
        }
        return START_STICKY;
    }
    /*
    @Override
    public void onStart(Intent intent, int startId)
    {
        alarm.SetAlarm(this);
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void UpdateNotification(Context context, String message) {
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );
/*
        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.custom_notification);
        expandedView.setTextViewText(R.id.notificationOldText, "Old\nText");
        expandedView.setTextViewText(R.id.notificationNewText, "New\nText");
*/
        android.app.Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_calendar)
                        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stat_calendar))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("VP-Änderung")
                .setContentText(message)
                .setLights(Color.BLUE, 500, 500)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        notification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
    }
}
