package wuest.markus.vertretungsplan;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;

public class CallHome implements Runnable {

    private static final String TAG = "CallHome";
    private Context context;
    private Handler handler;

    CallHome(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    @Override
    public void run() {
        boolean update = false;
        try {
            Message msg = new Message();
            msg.obj = context;
            int id = Preferences.readIntFromPreferences(context, context.getString(R.string.ID), -1);
            if (id == -1) {
                Random random = new Random();
                id = random.nextInt(Integer.MAX_VALUE - 1);
                Preferences.saveIntToPreferences(context, context.getString(R.string.ID), id);
            }
            try {
                String version;
                try {
                    version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    version = "1.0.4";
                }

                byte dev = 0;
                if (Preferences.readBooleanFromPreferences(context, context.getString(R.string.DEVELOPER_MODE), false)) {
                    dev = 1;
                }
                Log.d(TAG, String.valueOf(dev));
                Document doc = Jsoup.connect("http://vertretungsplan.sukram230799.bplaced.net/")
                        .data("id", String.valueOf(id))
                                //.data("comp", String.valueOf(/*Math.round(Math.sqrt(id))*/3))
                        .data("dev", String.valueOf(dev))
                        .data("version", version)
                        .post();
                Log.d(TAG, doc.html());
                if (!doc.body().html().equals(version)) {
                    Log.d(TAG, doc.body().html());
                    String[] localversion = version.split("\\.");
                    String[] remoteversion = doc.body().html().split("\\.");
                    try {
                        for (int i = 0; i < 3; i++) {
                            Log.d(TAG, String.valueOf(i));
                            Log.d(TAG, localversion[i]);
                            int lv = Integer.parseInt(localversion[i]);
                            Log.d(TAG, String.valueOf(lv));
                            Log.d(TAG, remoteversion[i]);
                            int rv = Integer.parseInt(remoteversion[i]);
                            Log.d(TAG, String.valueOf(rv));
                            if (lv < rv) {
                                update = true;
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(context.getString(R.string.update_available), update);
                                msg.setData(bundle);
                                break;
                            }
                        }
                    } catch (java.lang.NumberFormatException e) {

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (handler != null) {
                handler.sendMessage(msg);
            } else {
                if (update) {
                    String message = "Neue Version verfügbar!";
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
                            .setContentIntent(resultPendingIntent)
                            .setContentTitle("Update")
                            .setContentText(message)
                            .setLights(Color.BLUE, 500, 500)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .build();

                    notification.defaults |= android.app.Notification.DEFAULT_VIBRATE;
                    notification.defaults |= android.app.Notification.DEFAULT_SOUND;
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(2, notification);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
