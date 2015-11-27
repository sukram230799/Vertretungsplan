/*
package wuest.markus.vertretungsplan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

public class UpdateNotification {
    public void Notification(Context context, String message) {
        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(this.getClass());

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
        );
/*
        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.custom_notification);
        expandedView.setTextViewText(R.id.notificationOldText, "Old\nText");
        expandedView.setTextViewText(R.id.notificationNewText, "New\nText");
*//*
        android.app.Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_calendar)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_stat_calendar))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("VP-Änderung")
                .setContentText(message)
                .setLights(Color.BLUE, 500, 500)
                .build();

        notification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);
    }
}
*/