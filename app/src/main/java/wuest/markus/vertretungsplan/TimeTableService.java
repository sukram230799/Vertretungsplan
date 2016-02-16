package wuest.markus.vertretungsplan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimeTableService extends Service {

    private Context context = this;
    public static final String TAG = "TimeTableService";

    public TimeTableService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        DBHandler dbHandler = new DBHandler(context, null, null, 0);
        try {
            HWGrade grade = new HWGrade(Preferences.readStringFromPreferences(context, getString(R.string.SELECTED_GRADE), null));
            GregorianCalendar calendar = (GregorianCalendar) GregorianCalendar.getInstance();
            HWTime time = new HWTime(calendar);
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            int week = calendar.get(Calendar.WEEK_OF_YEAR);
            if (grade.getGradeName() != null && TimeTableHelper.lessonsLeft(dbHandler.getTimeTable(grade), time, week, day, context)) {
                int nextHour = TimeTableHelper.getNextHour(new HWTime(calendar));
                HWLesson[] lessonsBefore = null;
                String[] subscribedSubjects = dbHandler.getSubscribedSubjects();
                try {
                    lessonsBefore = TimeTableHelper.selectLessonsFromRepeatType(dbHandler.getTimeTableLesson(grade, day, nextHour - 1), week, subscribedSubjects, context);
                } catch (DBError error) {
                    error.printStackTrace();
                }
                HWLesson[] lessons = TimeTableHelper.selectLessonsFromRepeatType(dbHandler.getTimeTableLesson(grade, day, nextHour), week, subscribedSubjects, context);
                if (lessons.length > 0 && (lessonsBefore == null || lessonsBefore.length > 0 &&
                        (!lessonsBefore[0].getTeacher().equals(lessons[0].getTeacher()) ||
                                !lessonsBefore[0].getSubject().equals(lessons[0].getSubject()) ||
                                !lessonsBefore[0].getRoom().equals(lessons[0].getRoom())))) {
                    UpdateNotification(context, TimeTableHelper.getLessonMessage(lessons[0]));
                }
                stopSelf();
                return START_NOT_STICKY;
            }
            stopSelf();
            return START_NOT_STICKY;
        } catch (DBError error) {
            error.printStackTrace();
            stopSelf();
            return START_NOT_STICKY;
        }
    }

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

        android.app.Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_calendar)
                        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stat_calendar))
                .setAutoCancel(true)
                        //        .setContentIntent(resultPendingIntent)
                .setContentTitle("Nächste Stunde")
                .setContentText(message)
                .setLights(Color.BLUE, 500, 500)
                        //.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .build();

        //notification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
        //notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2, notification);
    }
}
