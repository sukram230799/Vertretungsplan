package wuest.markus.vertretungsplan;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class VPWidgetTextProcess {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static String[] processVPData(Context context, VPData vpData, Integer[] hours) {
        String Wochentag;
        Calendar c = new GregorianCalendar();
        c.setTime(vpData.getDate());

        Wochentag = TimeTableHelper.getDayName(c.get(Calendar.DAY_OF_WEEK), context);

        return new String[]{
                context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpData.getDate())),
                CombineData.hoursString(hours, false),
                vpData.getSubject(),
                vpData.getRoom(),
                vpData.getInfo1(),
                vpData.getInfo2()};


    }

    public static String[] processSPData(Context context, HWLesson hwLesson) {
        if (hwLesson.getSubject().equals("PAUSE")) {
            return new String[]{
                    String.valueOf(hwLesson.getHour()),
                    "PAUSE"
            };
        }
        return new String[]{
                String.valueOf(hwLesson.getHour()),
                hwLesson.getTeacher(),
                hwLesson.getSubject(),
                hwLesson.getRoom(),
                hwLesson.getRepeatType()
        };
    }

    public static String[] processPlan(Context context, HWPlan hwPlan) {
        boolean onlyVP = (hwPlan.getSpRoom() == null);
        boolean onlySP = (hwPlan.getVpRoom() == null);

        String Wochentag = "";
        if(!onlySP){
            Calendar c = new GregorianCalendar();
            c.setTime(hwPlan.getVpDate());
            Wochentag = TimeTableHelper.getDayName(c.get(Calendar.DAY_OF_WEEK), context);
        }

        if (!onlyVP) {
            if (hwPlan.getSpSubject().equals("PAUSE")) {
                return new String[]{
                        hwPlan.getHourString(),
                        "PAUSE"
                };
            }
            ArrayList<String> textToReturn = new ArrayList<>();
            textToReturn.add(hwPlan.getHourString());
            textToReturn.add(hwPlan.getSpTeacher());
            textToReturn.add(hwPlan.getSpSubject());
            textToReturn.add(hwPlan.getSpRoom());
            textToReturn.add(hwPlan.getSpRepeatType());
            if (onlySP) {
                return textToReturn.toArray(new String[textToReturn.size()]);
            }
            textToReturn.add(hwPlan.getVpSubject());
            textToReturn.add(hwPlan.getVpSubject());
            textToReturn.add(hwPlan.getVpRoom());
            textToReturn.add(hwPlan.getVpInfo1());
            textToReturn.add(hwPlan.getVpInfo2());
            textToReturn.add(dateFormat.format(hwPlan.getVpDate()));
            return textToReturn.toArray(new String[textToReturn.size()]);
        }
        if (!onlySP) {
            return new String[]{
                    context.getString(R.string.datebuilder, Wochentag, dateFormat.format(hwPlan.getVpDate())),
                    hwPlan.getHourString(),
                    hwPlan.getVpSubject(),
                    hwPlan.getVpRoom(),
                    hwPlan.getVpInfo1(),
                    hwPlan.getVpInfo2()
            };
        }

        if (hwPlan.getSpSubject().equals("PAUSE")) {
            return new String[]{
                    hwPlan.getHourString(),
                    "PAUSE"
            };
        } else if (hwPlan.getVpDate() == null) {
            return new String[]{
                    hwPlan.getHourString(),
                    hwPlan.getSpTeacher(),
                    hwPlan.getSpSubject(),
                    hwPlan.getSpRoom(),
                    hwPlan.getSpRepeatType(),
            };
        } else if (hwPlan.getSpSubject() == null) {
            return new String[]{
                    context.getString(R.string.datebuilder, Wochentag, dateFormat.format(hwPlan.getVpDate())),
                    hwPlan.getHourString(),
                    hwPlan.getVpSubject(),
                    hwPlan.getVpRoom(),
                    hwPlan.getVpInfo1(),
                    hwPlan.getVpInfo2()
            };
        } else {
            return new String[]{
                    hwPlan.getHourString(),
                    hwPlan.getSpTeacher(),
                    hwPlan.getSpSubject(),
                    hwPlan.getSpRoom(),
                    hwPlan.getSpRepeatType(),
                    hwPlan.getVpSubject(),
                    hwPlan.getVpRoom(),
                    hwPlan.getVpInfo1(),
                    hwPlan.getVpInfo2(),
                    dateFormat.format(hwPlan.getVpDate())
            };
        }
    }

    public static String brief(String[] text) {
        return text[0] + " " + text[1] + " " + text[2] + " in " + text[3] + ": " + text[4] + " " + text[5];
    }
}
