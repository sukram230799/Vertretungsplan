package wuest.markus.vertretungsplan;

import java.util.ArrayList;
import java.util.Arrays;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 */
public class VPWidgetAdapter implements RemoteViewsFactory {
    private ArrayList<VPData> vpDataArrayList = new ArrayList<>();
    private Context context = null;
    private int appWidgetId;
    public static final String TAG = "VPWidgetAdapter";

    public VPWidgetAdapter(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        populateListItem();
    }

    private void populateListItem() {
        /*for (int i = 0; i < 10; i++) {
            ListItem listItem = new ListItem();
            listItem.heading = "Heading" + i;
            listItem.content = i
                    + " This is the content of the app widget listview.Nice content though";
            vpDataArrayList.add(listItem);
        }*/
        DBHandler dbHandler = new DBHandler(context, null, null, 0);
        try {
            vpDataArrayList = new ArrayList<>(Arrays.asList(CombineData.combineVP(dbHandler.getVP(new HWGrade("TG11-2")))));
        } catch (DBError dbError) {
            dbError.printStackTrace();
        } finally {
            dbHandler.close();
        }

    }

    @Override
    public int getCount() {
        return vpDataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     *Similar to getView of Adapter where instead of View
     *we return RemoteViews 
     * 
     */
    @Override
    public RemoteViews getViewAt(int position) {
        Log.d(TAG, "@getViewAt");
        final RemoteViews remoteView;
        VPData data = vpDataArrayList.get(position);
        String text[] = VPWidgetTextProcess.processData(context, data);
        if (text.length < 6) {
            text = new String[] {"Something", "went", "horribly", "wrong!", "We're", "sorry"};
        }
        Log.d(TAG, "Type: " + VPWidgetConfigureActivity.loadGradePref(context, appWidgetId));
        switch (VPWidgetConfigureActivity.loadGradePref(context, appWidgetId)) {
            case 0: //Full Design
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_full_row);
                remoteView.setTextViewText(R.id.textDate, text[0]);
                remoteView.setTextViewText(R.id.textHour, text[1]);
                remoteView.setTextViewText(R.id.textSubject, text[2]);
                remoteView.setTextViewText(R.id.textRoom, text[3]);
                remoteView.setTextViewText(R.id.textInfo1, text[4]);
                remoteView.setTextViewText(R.id.textInfo2, text[5]);
                break;
            case 1: //Minimal Design
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_minimal_row);

                        /*
                        context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpDataArrayList.get(position).getDate())) + " " +
                        CombineData.hoursString(vpDataArrayList.get(position).getHours()) + " " +
                        vpDataArrayList.get(position).getSubject() + " in: " +
                        vpDataArrayList.get(position).getRoom();*/
                remoteView.setTextViewText(R.id.textBrief, VPWidgetTextProcess.brief(text));
                break;
            default:
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_minimal_row);
                remoteView.setTextViewText(R.id.textBrief, "Bitte Widget neu erstellen.");
                break;
        }
        //remoteView.setTextViewText(R.id.textHour, "");
/*
        String text = context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpDataArrayList.get(position).getDate())) + " " +
                CombineData.hoursString(vpDataArrayList.get(position).getHours()) + " " +
                vpDataArrayList.get(position).getSubject() + " in: " +
                vpDataArrayList.get(position).getRoom();
        Log.d(TAG, text);
        */
        Log.d(TAG, "-getViewAt");
        return remoteView;
    }


    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

}

