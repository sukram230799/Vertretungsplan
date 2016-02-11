package wuest.markus.vertretungsplan;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

public class TimeTableHelper {

    public static final String TAG = "TimeTableHelper";


    public static final String format = "dd.MM.yyyy HH:mm,z";
    public static final SimpleDateFormat sdf = new SimpleDateFormat(format);

    public static HWLesson[] selectLessonsFromRepeatType(HWLesson[] hwLessons, int week, Context context) {
        ArrayList<HWLesson> hwLessonArrayList = new ArrayList<>(hwLessons.length);
        for (HWLesson lesson : hwLessons) {
            if (isRepeatType(lesson.getRepeatType(), week, context)) {
                hwLessonArrayList.add(lesson);
            }
        }
        return hwLessonArrayList.toArray(new HWLesson[hwLessonArrayList.size()]);
    }


    public static boolean isRepeatType(String repeatType, int week, Context context) {
        //Log.d(TAG, sdf.format(calendar.getTime()) + "isRepeatType");
        //int week = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
        //int year = calendar.get(GregorianCalendar.YEAR);
        String[] repeatTypes = repeatType.split("/");
        if (repeatTypes.length > 0) {
            repeatType = repeatTypes[0];
        } else if (repeatTypes.length > 1) {
            String group = repeatTypes[repeatTypes.length - 1];
            String selectedGroup = Preferences.readStringFromPreferences(context, context.getString(R.string.SELECTED_GROUP), null);
            if (selectedGroup != null && !group.equals(selectedGroup)) {
                return false;
            }
        }

        int[] quarters = getQuarters(context);

        int q1s = quarters[0];
        int q1e = quarters[1];
        int q2s = quarters[2];
        int q2e = quarters[3];
        int q3s = quarters[4];
        int q3e = quarters[5];
        int q4s = quarters[6];
        int q4e = quarters[7];

        boolean evenWeek = ((week % 2) == 0);
        Log.d(TAG, "EvenWeek " + evenWeek);

        repeatType = repeatType.toUpperCase();

        if (repeatType.equals("W")) {
            return true;
        } else if (repeatType.startsWith("G") && evenWeek) {
            if (repeatType.equals("G")) {
                return true;
            } else if (repeatType.equals("G1") && !secondHalf(week)) {
                return true;
            } else if (repeatType.equals("G151") && !secondHalf(week) && week < q1e) {
                return true;
            } else if (repeatType.equals("G152") && !secondHalf(week) && week > q2s) {
                return true;
            } else if (repeatType.equals("G2") && secondHalf(week)) {
                return true;
            } else if (repeatType.equals("G251") && secondHalf(week) && week < q3e) {
                return true;
            } else if (repeatType.equals("G252") && secondHalf(week) && week > q4s) {
                return true;
            }
        } else if (repeatType.startsWith("U") && !evenWeek) {
            if (repeatType.equals("U")) {
                return true;
            } else if (repeatType.equals("U1") && !secondHalf(week)) {
                return true;
            } else if (repeatType.equals("U2") && secondHalf(week)) {
                return true;
            }
        } else if (repeatType.startsWith("T")) {
            if (checkT(week, Integer.parseInt(repeatType.charAt(1) + ""))) {
                if (repeatType.length() > 2) {
                    if (repeatType.charAt(2) == '1' && !secondHalf(week)) {
                        return true;
                    } else if (repeatType.charAt(2) == '2' && secondHalf(week)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public static int[] getQuarters(Context context) {
        int q1s = Preferences.readIntFromPreferences(context, context.getString(R.string.Q1_S), -1);
        int q1e = Preferences.readIntFromPreferences(context, context.getString(R.string.Q1_E), -1);
        int q2s = Preferences.readIntFromPreferences(context, context.getString(R.string.Q2_S), -1);
        int q2e = Preferences.readIntFromPreferences(context, context.getString(R.string.Q2_E), -1);
        int q3s = Preferences.readIntFromPreferences(context, context.getString(R.string.Q3_S), -1);
        int q3e = Preferences.readIntFromPreferences(context, context.getString(R.string.Q3_E), -1);
        int q4s = Preferences.readIntFromPreferences(context, context.getString(R.string.Q4_S), -1);
        int q4e = Preferences.readIntFromPreferences(context, context.getString(R.string.Q4_E), -1);

        return new int[]{q1s, q1e, q2s, q2e, q3s, q3e, q4s, q4e};
    }

    public static boolean updateQuarters(Context context, HWTime time) {
        int month = time.getMonth();
        int yearCurrent = time.getYear();
        int yearS;
        int yearE;
        if (month < GregorianCalendar.JULY) {
            yearE = yearCurrent;
            yearS = yearCurrent - 1;
        } else {
            yearE = yearCurrent + 1;
            yearS = yearCurrent;
        }
        String lastUpdate = Preferences.readStringFromPreferences(context, context.getString(R.string.QUARTER_LAST_UPDATE), null);
        return (lastUpdate == null || !lastUpdate.equals(yearS + "/" + yearE));
    }

    public static boolean secondHalf(int week) {
        return week <= 6;
    }

    public static boolean oddQuarter(int week) {
        return week > 37 && week < 47 || week < 17;
    }

    public static boolean checkT(int week, int code) {
        return (week + code) % 4 == 0;
    }

    public static HWTime[] getHourTime(int hour, HWTime time) {
        switch (hour) {
            case 1:
                return new HWTime[]{setHour(8, 0, time), setHour(8, 45, time)};
            case 2:
                return new HWTime[]{setHour(8, 45, time), setHour(9, 30, time)};
            case 3:
                return new HWTime[]{setHour(9, 50, time), setHour(10, 35, time)};
            case 4:
                return new HWTime[]{setHour(10, 35, time), setHour(11, 20, time)};
            case 5:
                return new HWTime[]{setHour(11, 30, time), setHour(12, 15, time)};
            case 6:
                return new HWTime[]{setHour(12, 15, time), setHour(13, 0, time)};
            case 7:
                return new HWTime[]{setHour(13, 0, time), setHour(13, 45, time)};
            case 8:
                return new HWTime[]{setHour(13, 45, time), setHour(14, 30, time)};
            case 9:
                return new HWTime[]{setHour(14, 30, time), setHour(15, 15, time)};
            case 10:
                return new HWTime[]{setHour(15, 30, time), setHour(16, 15, time)};
            case 11:
                return new HWTime[]{setHour(16, 15, time), setHour(17, 0, time)};
            case 12:
                return new HWTime[]{setHour(17, 0, time), setHour(17, 45, time)};
            default:
                return new HWTime[]{setHour(22, 0, time), setHour(23, 0, time)};
        }
    }

    public static int getCurrentHour(HWTime currentTime) {
        for (int i = 1; i < 12; i++) {
            Log.d(TAG, "getCurrentHour");
            HWTime[] time = getHourTime(i, currentTime);
            if (currentTime.toDate().after(time[0].toDate()) && currentTime.toDate().before(time[1].toDate())) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isBreak(HWTime time) {
        for (int i = 1; i < 11; i++) {
            Log.d(TAG, "isBreak");
            HWTime before = getHourTime(i, time)[1];
            HWTime after = getHourTime(i + 1, time)[0];
            if (time.toDate().after(before.toDate()) && time.toDate().before(after.toDate())) {
                return true;
            }
        }
        return false;
    }

    /*public static int getCurrentBreak() {
        GregorianCalendar currentTime;
        currentTime = GregorianCalendar.getInstance();
        currentTime.set(GregorianCalendar.HOUR_OF_DAY, 8);
        currentTime.set(GregorianCalendar.MINUTE, 50);
        switch (getHourBeforeBreak(currentTime)) {
            case 2:
                return 1;
            case 4:
                return 2;
            case 9:
                return 3;
            default:
                return -1;
        }
    }*/

    public static int getHourBeforeBreak(HWTime currentTime) {
        for (int i = 1; i < 11; i++) {
            Log.d(TAG, "getHourBeforeBreak");
            HWTime before = getHourTime(i, currentTime)[1];
            HWTime after = getHourTime(i + 1, currentTime)[0];
            if (currentTime.toDate().after(before.toDate()) && currentTime.toDate().before(after.toDate())) {
                return i;
            }
        }
        return -1;
    }

    public static int getHour(HWTime time) {
        for (int i = 1; i < 12; i++) {
            Log.d(TAG, "getHour");
            HWTime[] times = getHourTime(i, time);
            if (time.toDate().after(times[0].toDate()) && time.toDate().before(times[1].toDate())) {
                return i;
            }
        }
        return -1;
    }

    public static int getNextHour(HWTime time) {
        for (int i = 1; i < 12; i++) {
            Log.d(TAG, "getNextHour");
            HWTime[] hour = getHourTime(i, time);
            Log.d(TAG, "Time: " + time.getHour() + ":" + time.getMinute());
            Log.d(TAG, "Time: " + hour[0].getHour() + ":" + hour[0].getMinute());
            try {
                Date timeDate = new SimpleDateFormat("HH:mm").parse(time.getHour() + ":" + time.getMinute());
                Date hourDate = new SimpleDateFormat("HH:mm").parse(hour[0].getHour() + ":" + hour[0].getMinute());
                Log.d(TAG, new SimpleDateFormat("HH:mm").format(timeDate));
                Log.d(TAG, new SimpleDateFormat("HH:mm").format(hourDate));
                Log.d(TAG, String.valueOf(timeDate.before(hourDate)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            boolean before = time.toDate().before(hour[0].toDate());
            Log.d(TAG, String.valueOf(before));
            if (before) {
                Log.d(TAG, String.valueOf(i));
                return i;
            }
        }
        return -1;
    }

    public static HWTime setHour(int hour, int minute, HWTime time) {
        HWTime newTime = new HWTime(time);
        newTime.setHour(hour);
        newTime.setMinute(minute);
        return newTime;
    }

    public static int[] lengthOfDay(HWLesson[] lessons, int week, int day, Context context) {
        lessons = selectLessonsFromRepeatType(lessons, week, context);
        int lowestHour = 12;
        int highestHour = 0;
        for (HWLesson lesson : lessons) {
            if (lesson.getDay() == day) {
                for (int hour : lesson.getHours()) {
                    if (lowestHour > hour) {
                        lowestHour = hour;
                    }
                    if (highestHour < hour) {
                        highestHour = hour;
                    }
                }
            }
        }
        return new int[]{lowestHour, highestHour};
    }

    /*public static boolean lessonsLeft(DBHandler dbHandler, HWGrade grade, int hour, int day, int week) {
        try {
            return lessonsLeft(selectLessonsFromRepeatType(dbHandler.getTimeTable(grade, day), week), hour, day, week);
        } catch (DBError error) {
            error.printStackTrace();
            return false;
        }
    }

    public static boolean lessonsLeft(HWLesson[] lessons, int hour, int day, int week) {
        lessons = selectLessonsFromRepeatType(lessons, week);
        for (HWLesson lesson : lessons) {
            if (lesson.getDay() == day) {
                for (int lessonHour : lesson.getHours()) {
                    if (lessonHour > hour) {
                        return true;
                    }
                }
            }
        }
        return false;
    }*/

    public static boolean lessonsLeft(HWLesson[] lessons, HWTime time, int week, int day, Context context) {
        int[] hours = lengthOfDay(lessons, week, day, context);
        Log.d(TAG, "lessonsLeft");
        HWTime before = getHourTime(hours[0], time)[0];
        HWTime after = getHourTime(hours[1], time)[1];
        Log.d(TAG, "lessonsLeft, after:" + after.toString());
        Log.d(TAG, "lessonsLeft, time: " + time.toString());
        return time.toDate().before(after.toDate());
    }

    public static String getLessonMessage(HWLesson lesson) {
        return lesson.getSubject() + " in " + lesson.getRoom() + " bei " + lesson.getTeacher();
    }

    public static String getCSV(HWLesson[] hwLessons, String itemSeparator, String lineSeparator) {
        String CSV = "grade" + itemSeparator + "day" + itemSeparator + "hour" + itemSeparator + "teacher" + itemSeparator +
                "subject" + itemSeparator + "room" + itemSeparator + "repeattype" + lineSeparator;
        for (HWLesson lesson : hwLessons) {
            CSV += lesson.getGrade().getGradeName() + itemSeparator +
                    lesson.getDay() + itemSeparator +
                    lesson.getHours()[0] + itemSeparator +
                    lesson.getTeacher() + itemSeparator +
                    lesson.getSubject() + itemSeparator +
                    lesson.getRoom() + itemSeparator +
                    lesson.getRepeatType() + lineSeparator;
        }
        return CSV;
    }

    public static HWLesson[] parseCSV(String CSV, String itemSeparator, String lineSeparator) {
        CSV = CSV.trim();
        Log.v(TAG, CSV);
        ArrayList<HWLesson> lessons = new ArrayList<>();
        String[] lessonStrings = CSV.split(lineSeparator);
        Log.d(TAG, lessonStrings[0]);
        if (!lessonStrings[0].equals("grade" + itemSeparator + "day" + itemSeparator + "hour" + itemSeparator + "teacher" + itemSeparator +
                "subject" + itemSeparator + "room" + itemSeparator + "repeattype")) {
            Log.d(TAG, ("grade" + itemSeparator + "day" + itemSeparator + "hour" + itemSeparator + "teacher" + itemSeparator +
                    "subject" + itemSeparator + "room" + itemSeparator + "repeattype"));
            return new HWLesson[0];
        }
        for (int i = 1; i < lessonStrings.length; i++) {
            String[] lessonInfo = lessonStrings[i].split(Pattern.quote(itemSeparator));
            Log.d(TAG, lessonStrings[i]);
            Integer[] hours = {Integer.parseInt(lessonInfo[2])};
            lessons.add(new HWLesson(new HWGrade(lessonInfo[0]),
                    hours,
                    Integer.parseInt(lessonInfo[1]),

                    lessonInfo[3],
                    lessonInfo[4],
                    lessonInfo[5],
                    lessonInfo[6]
            ));
        }
        return lessons.toArray(new HWLesson[lessons.size()]);
    }

    public static HWLesson[] fillGabs(HWLesson[] lessons, int week, int day, Context context) {
        HWGrade grade = lessons[0].getGrade();
        ArrayList<HWLesson> lessonArrayList = new ArrayList<>(Arrays.asList(lessons));
        int[] hours = lengthOfDay(lessons, week, day, context);
        ArrayList<Integer> placedHours = new ArrayList<>((hours[1] - hours[0] + 1));
        for (HWLesson lesson : lessons) {
            for (int hour : lesson.getHours()) {
                placedHours.add(hour);
            }
        }
        Collections.sort(placedHours);
        int lastHour = 12;
        for (int hour : placedHours) {
            if (hour > (lastHour + 1)) {
                lessonArrayList.add(placedHours.indexOf(hour), new HWLesson(grade, new Integer[]{hour - 1}, day, "", "PAUSE", "", ""));
            }
            lastHour = hour;
        }
        return lessonArrayList.toArray(new HWLesson[lessonArrayList.size()]);
    }

    public static String getDayName(int day){
        switch (day) {
            case 2:
                return "Montag";
            case 3:
                return "Dienstag";
            case 4:
                return "Mittwoch";
            case 5:
                return "Donnerstag";
            case 6:
                return "Freitag";
            case 7:
                return "Samstag";
            default:
                return "Sonntag";
        }
    }
}
