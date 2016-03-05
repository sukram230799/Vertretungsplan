package wuest.markus.vertretungsplan;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class VPWidgetTextProcess {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static String[] processVPData(Context context, VPData vpData) {
        String Wochentag;
        Calendar c = new GregorianCalendar();
        c.setTime(vpData.getDate());

        Wochentag = TimeTableHelper.getDayName(c.get(Calendar.DAY_OF_WEEK), context);

        return new String[]{
                context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpData.getDate())),
                CombineData.hoursString(vpData.getHours(), false),
                vpData.getSubject(),
                vpData.getRoom(),
                vpData.getInfo1(),
                vpData.getInfo2()};


    }

    public static String[] processSPData(Context context, HWLesson hwLesson) {
        if (hwLesson.getSubject().equals("PAUSE")) {
            return new String[]{
                    CombineData.hoursString(hwLesson.getHours(), true),
                    "PAUSE"
            };
        }
        return new String[]{
                CombineData.hoursString(hwLesson.getHours(), true),
                hwLesson.getTeacher(),
                hwLesson.getSubject(),
                hwLesson.getRoom(),
                hwLesson.getRepeatType()
        };
    }

    public static String[] processPlan(Context context, HWPlan hwPlan) {
        if (hwPlan.getSpSubject().equals("PAUSE")) {
            return new String[]{
                    hwPlan.getHourString(),
                    "PAUSE"
            };
        } else if(hwPlan.getDate() == null) {
            return new String[]{
                    hwPlan.getHourString(),
                    hwPlan.getTeacher(),
                    hwPlan.getSpSubject(),
                    hwPlan.getSpRoom(),
                    hwPlan.getRepeatType(),
                    hwPlan.getVpSubject(),
                    hwPlan.getVpRoom(),
                    hwPlan.getInfo1(),
                    hwPlan.getInfo2(),
                    dateFormat.format(hwPlan.getDate())
            };
        } else {
            return new String[] {

            };
        }
    }

    public static String brief(String[] text) {
        return text[0] + " " + text[1] + " " + text[2] + " in " + text[3] + ": " + text[4] + " " + text[5];
    }
}
