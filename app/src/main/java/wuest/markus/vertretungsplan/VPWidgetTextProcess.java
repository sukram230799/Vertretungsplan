package wuest.markus.vertretungsplan;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class VPWidgetTextProcess {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static String[] processData(Context context, VPData vpData) {
        String Wochentag;
        Calendar c = new GregorianCalendar();
        c.setTime(vpData.getDate());
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                Wochentag = context.getString(R.string.dayMonday);
                break;
            case Calendar.TUESDAY:
                Wochentag = context.getString(R.string.dayTuesday);
                break;
            case Calendar.WEDNESDAY:
                Wochentag = context.getString(R.string.dayWednesday);
                break;
            case Calendar.THURSDAY:
                Wochentag = context.getString(R.string.dayThursday);
                break;
            case Calendar.FRIDAY:
                Wochentag = context.getString(R.string.dayFriday);
                break;
            case Calendar.SATURDAY:
                Wochentag = context.getString(R.string.daySaturday);
                break;
            case Calendar.SUNDAY:
                Wochentag = context.getString(R.string.daySunday);
                break;
            default:
                Wochentag = context.getString(R.string.dayInvalid);
                break;
        }

        return new String[] {
                context.getString(R.string.datebuilder, Wochentag, dateFormat.format(vpData.getDate())),
                CombineData.hoursString(vpData.getHours(), false),
                vpData.getSubject(),
                vpData.getRoom(),
                vpData.getInfo1(),
                vpData.getInfo2()};


    }

    public static String brief(String[] text){
        return text[0] + " " + text[1] + " " + text[2] + " in " + text[3] + ": " + text[4] + " " + text[5];
    }
}
