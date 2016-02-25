package wuest.markus.vertretungsplan;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class VPWidgetService extends RemoteViewsService {

    private static final String TAG = "VPWidgetService";

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(TAG, "@onGetViewFactory");
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        Log.d(TAG, "-onGetViewFactory");
        return (new VPWidgetAdapter(this.getApplicationContext(), intent));
    }


}
