package wuest.markus.vertretungsplan;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link VPWidgetConfigureActivity VPWidgetConfigureActivity}
 *
 */
public class VPWidget extends AppWidgetProvider {

    private  static final String TAG = "VPWidget";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Log.d(TAG, "@updateAppWidget");

        CharSequence gradePref = String.valueOf(VPWidgetConfigureActivity.loadGradePref(context, appWidgetId));

        Intent serviceIntent = new Intent(context, VPWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
        Log.d(TAG, "vpWidget");
        RemoteViews vpWidget = new RemoteViews(context.getPackageName(), R.layout.vpwidget);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Log.d(TAG, "BV: " + Build.VERSION.SDK_INT);
            vpWidget.setRemoteAdapter(R.id.vpwidget, serviceIntent);
        } else  {
            Log.d(TAG, "BV: " + Build.VERSION.SDK_INT);
            vpWidget.setRemoteAdapter(appWidgetId, R.id.vpwidget, serviceIntent);
        }

        Intent onClickIntent = new Intent(context, MainActivity.class);
        PendingIntent onClickPendingIntent = PendingIntent.getActivity(context, 0, onClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.d(TAG, "onClickIntent");
        vpWidget.setPendingIntentTemplate(R.id.vpwidget, onClickPendingIntent);
        Log.d(TAG, "setPendingIntent");
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, vpWidget);
        Log.d(TAG, "-updateAppWidget");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "@onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d(TAG, "-onUpdate");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            VPWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

