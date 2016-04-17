package wuest.markus.vertretungsplan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.Visibility;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 */
public class VPWidgetAdapter implements RemoteViewsFactory {
    private ArrayList<Object> arrayList = new ArrayList<>();
    private Context context = null;
    private final int appWidgetId;
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
        HWGrade grade = new HWGrade(Preferences.readStringFromPreferences(context, context.getString(R.string.SELECTED_GRADE), ""));
        try {
            int type = VPWidgetConfigureActivity.loadGradePref(context, appWidgetId);
            Integer[] lessonDays = dbHandler.getDaysWithLessons(grade);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -1);
            HWTime time = new HWTime(c);
            time = TimeTableHelper.getNextHWTime(time, lessonDays);
            c.setTime(time.toDate());
            int day = c.get(Calendar.DAY_OF_WEEK);
            int week = c.get(Calendar.WEEK_OF_YEAR);
            String[] subscribedSubjects = dbHandler.getSubscribedSubjects();
            if (type <= 1) {
                arrayList = new ArrayList<>();
                VPData[] data = dbHandler.getVP(grade);
                for (VPData vpData : data) {
                    boolean found = false;
                    for (Object usedDataArray : arrayList) {
                        for (VPData usedData : (VPData[]) usedDataArray) {
                            if (usedData == vpData) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        arrayList.add(CombineData.getSimilarVP(vpData, data));
                    }
                }
            } else if (type <= 2) {
                HWLesson[] lesson = TimeTableHelper.selectLessonsFromTime(dbHandler.getTimeTable(grade, day), time, subscribedSubjects, context);
                arrayList = new ArrayList<>(Arrays.asList((Object[]) lesson));
            } else {
                HWLesson[] hwLessons = dbHandler.getTimeTable(grade, day /*Calendar.getInstance().get(Calendar.DAY_OF_WEEK)*/);
                HWPlan[] plan;
                hwLessons = TimeTableHelper.selectLessonsFromTime(hwLessons, time, subscribedSubjects, context); //No CombineData, because of better layout;
                //hwLessons = TimeTableHelper.fillGabs(hwLessons, week, weekDay, getActivity());
                try {
                    VPData[] vpData = dbHandler.getVP(grade, new HWTime(GregorianCalendar.getInstance()));
                    //vpData = TimeTableHelper.selectVPDataFromWeekDay(vpData, day);
                    vpData = TimeTableHelper.selectVPDataFromSubscribedSubjects(vpData, subscribedSubjects);
                    plan = PlanHelper.combineVPSP(hwLessons, vpData);
                    plan = PlanHelper.fillPlanGabs(plan, day);
                } catch (DBError e) {
                    e.printStackTrace();
                    plan = PlanHelper.combineVPSP(hwLessons, new VPData[0]);
                }
                arrayList = new ArrayList<>(Arrays.asList((Object[]) plan));
            }
        } catch (DBError dbError) {
            dbError.printStackTrace();
        } finally {
            dbHandler.close();
        }

    }

    @Override
    public int getCount() {
        return arrayList.size();
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

        int type = VPWidgetConfigureActivity.loadGradePref(context, appWidgetId);

        final int redColor = Color.parseColor("#8aFF0000");
        final int textColor = Color.parseColor("#8a000000");

        String[] text;
        VPData vpData;
        HWLesson lesson;
        HWPlan plan;
        if (type <= 1) {
            vpData = ((VPData[]) arrayList.get(position))[0];
            Integer[] hours = new Integer[((VPData[]) arrayList.get(position)).length];
            for (int i = 0; i < hours.length; i++) {
                hours[i] = ((VPData[]) arrayList.get(position))[i].getHour();
            }
            text = VPWidgetTextProcess.processVPData(context, vpData, hours);
            if (text.length < 6) {
                text = new String[]{"Something", "went", "horribly", "wrong!", "We're", "sorry"};
            }
        } else if (type <= 2) {
            Log.d(TAG, String.valueOf(arrayList.size()));
            lesson = (HWLesson) arrayList.get(position);
            text = VPWidgetTextProcess.processSPData(context, lesson);
        } else {
            plan = (HWPlan) arrayList.get(position);
            text = VPWidgetTextProcess.processPlan(context, plan);
        }

        Log.d(TAG, "Type: " + VPWidgetConfigureActivity.loadGradePref(context, appWidgetId));
        switch (type) {
            case 0: //Full Design
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_full_row);
                remoteView.setTextViewText(R.id.vpTextDate, text[0]);
                remoteView.setTextViewText(R.id.vpTextHour, text[1]);
                remoteView.setTextViewText(R.id.vpTextSubject, text[2]);
                remoteView.setTextViewText(R.id.vpTextRoom, text[3]);
                remoteView.setTextViewText(R.id.vpTextInfo1, text[4]);
                remoteView.setTextViewText(R.id.vpTextInfo2, text[5]);
                break;
            case 1: //Minimal Design
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_minimal_row);

                        /*
                        context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpDataArrayList.get(position).getVpDate())) + " " +
                        CombineData.hoursString(vpDataArrayList.get(position).getHours()) + " " +
                        vpDataArrayList.get(position).getSubject() + " in: " +
                        vpDataArrayList.get(position).getRoom();*/
                remoteView.setTextViewText(R.id.textBrief, VPWidgetTextProcess.brief(text));
                break;
            case 2:
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_table_row);
                if (text.length <= 2) {
                    remoteView.setViewVisibility(R.id.spTextBreak, View.VISIBLE);

                    remoteView.setViewVisibility(R.id.spTextHour, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextTeacher, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextSubject, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextRoom, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextRepeatType, View.GONE);

                    remoteView.setTextViewText(R.id.spTextHour, text[0]);
                    remoteView.setTextViewText(R.id.spTextBreak, text[1]);
                } else {
                    remoteView.setViewVisibility(R.id.spTextBreak, View.GONE);

                    remoteView.setViewVisibility(R.id.spTextHour, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextTeacher, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextSubject, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextRoom, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextRepeatType, View.VISIBLE);

                    remoteView.setTextViewText(R.id.spTextHour, text[0]);
                    remoteView.setTextViewText(R.id.spTextTeacher, text[1]);
                    remoteView.setTextViewText(R.id.spTextSubject, text[2]);
                    remoteView.setTextViewText(R.id.spTextRoom, text[3]);
                    remoteView.setTextViewText(R.id.spTextRepeatType, text[4]);
                }
                break;
            case 3:
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_plan_row);
                if (text.length <= 2) {

                    remoteView.setViewVisibility(R.id.spTextBreak, View.VISIBLE);

                    remoteView.setViewVisibility(R.id.spTextHour, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextTeacher, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextSubject, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextRoom, View.GONE);
                    remoteView.setViewVisibility(R.id.spTextRepeatType, View.GONE);

                    remoteView.setTextViewText(R.id.spTextHour, text[0]);
                    remoteView.setTextViewText(R.id.spTextBreak, text[1]);
                } else if (text.length == 6) {
                    remoteView.setViewVisibility(R.id.spTextBreak, View.GONE);

                    remoteView.setViewVisibility(R.id.spTextHour, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextTeacher, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextSubject, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextRoom, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextRepeatType, View.VISIBLE);

                    remoteView.setViewVisibility(R.id.vpTextDate, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.vpTextSubject, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.vpTextRoom, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.vpTextInfo1, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.vpTextInfo2, View.VISIBLE);

                    remoteView.setTextColor(R.id.spTextHour, redColor);
                    remoteView.setTextColor(R.id.spTextTeacher, redColor);
                    remoteView.setTextColor(R.id.spTextSubject, redColor);
                    remoteView.setTextColor(R.id.spTextRoom, redColor);
                    remoteView.setTextColor(R.id.spTextRepeatType, redColor);

                    remoteView.setTextViewText(R.id.spTextHour, text[1]);
                    remoteView.setTextViewText(R.id.spTextTeacher, "VP!");
                    remoteView.setTextViewText(R.id.spTextSubject, text[2]);
                    remoteView.setTextViewText(R.id.spTextRoom, text[3]);
                    remoteView.setTextViewText(R.id.spTextRepeatType, "");

                    remoteView.setTextViewText(R.id.vpTextDate, text[0]);
                    remoteView.setTextViewText(R.id.vpTextHour, text[1]);
                    remoteView.setTextViewText(R.id.vpTextSubject, text[2]);
                    remoteView.setTextViewText(R.id.vpTextRoom, text[3]);
                    remoteView.setTextViewText(R.id.vpTextInfo1, text[4]);
                    remoteView.setTextViewText(R.id.vpTextInfo2, text[5]);
                } else if (text.length == 5) {
                    remoteView.setViewVisibility(R.id.spTextHour, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextTeacher, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextSubject, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextRoom, View.VISIBLE);
                    remoteView.setViewVisibility(R.id.spTextRepeatType, View.VISIBLE);

                    remoteView.setViewVisibility(R.id.spTextBreak, View.GONE);

                    remoteView.setViewVisibility(R.id.vpLayout, View.GONE);

                    remoteView.setTextViewText(R.id.spTextHour, text[0]);
                    remoteView.setTextViewText(R.id.spTextTeacher, text[1]);
                    remoteView.setTextViewText(R.id.spTextSubject, text[2]);
                    remoteView.setTextViewText(R.id.spTextRoom, text[3]);
                    remoteView.setTextViewText(R.id.spTextRepeatType, text[4]);
                }
                break;
            default:
                remoteView = new RemoteViews(
                        context.getPackageName(), R.layout.widget_minimal_row);
                remoteView.setTextViewText(R.id.textBrief, "Bitte Widget neu erstellen.");
                break;
        }
        //remoteView.setTextViewText(R.id.spTextHour, "");
/*
        String text = context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpDataArrayList.get(position).getVpDate())) + " " +
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
        this.populateListItem();
    }

    @Override
    public void onDestroy() {
    }

}

