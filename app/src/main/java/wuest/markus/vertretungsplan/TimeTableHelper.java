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

    public static HWLesson[] selectLessonsFromTime(HWLesson[] hwLessons, HWTime time, String[] subscribedSubjects, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time.toDate());
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        ArrayList<HWLesson> hwLessonArrayList = new ArrayList<>(hwLessons.length);
        for (HWLesson lesson : hwLessons) {
            if (lesson.getAssignedTime() != null && lesson.getAssignedTime().toDate().equals(time.toDate())) {
                hwLessonArrayList.add(lesson);
            } else if (lesson.getDay() == weekDay && isRepeatType(lesson.getRepeatType(), week, context) && isSubscribedSubject(lesson.getSubject(), subscribedSubjects)) {
                lesson.setAssignedTime(time);
                hwLessonArrayList.add(lesson);
            }
        }
        return hwLessonArrayList.toArray(new HWLesson[hwLessonArrayList.size()]);
    }

    /*public static HWLesson[] selectLessonsFromDayRepeatType(HWLesson[] hwLessons, int week, int day, String[] subscribedSubjects, Context context) {
        ArrayList<HWLesson> hwLessonArrayList = new ArrayList<>(hwLessons.length);
        for (HWLesson lesson : hwLessons) {
            if (lesson.getDay() == day) {
                hwLessonArrayList.add(lesson);
            }
        }
        return selectLessonsFromRepeatType(hwLessonArrayList.toArray(new HWLesson[hwLessonArrayList.size()]), week, subscribedSubjects, context);
    }

    public static HWLesson[] selectLessonsFromRepeatType(HWLesson[] hwLessons, int week, String[] subscribedSubjects, Context context) {
        ArrayList<HWLesson> hwLessonArrayList = new ArrayList<>(hwLessons.length);
        for (HWLesson lesson : hwLessons) {
            if (isRepeatType(lesson.getRepeatType(), week, context) && isSubscribedSubject(lesson.getSubject(), subscribedSubjects)) {
                hwLessonArrayList.add(lesson);
            }
        }
        return hwLessonArrayList.toArray(new HWLesson[hwLessonArrayList.size()]);
    }*/

    private static boolean isSubscribedSubject(String subject, String[] subscribedSubjects) {
        if (subscribedSubjects == null) {
            return true;
        }
        String[] parts = subject.split("-");
        String subIndicator = parts[parts.length - 1];
        if (subIndicator.equals("W") || subIndicator.equals("WP") || subject.equals("KR") || subject.equals("ETH") || subject.equals("EVR")) {
            for (String subscribedSubject : subscribedSubjects) {
                if (subject.equals(subscribedSubject)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    public static boolean isRepeatType(String repeatType, int week, Context context) {
        //Log.d(TAG, sdf.format(calendar.getTime()) + "isRepeatType");
        //int week = calendar.get(GregorianCalendar.WEEK_OF_YEAR);
        //int year = calendar.get(GregorianCalendar.YEAR);
        String[] repeatTypes = repeatType.split("/");
        if (repeatTypes.length > 0) {
            repeatType = repeatTypes[0];
        }
        if (repeatTypes.length > 1) {
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
            } else if (repeatType.equals("G1") && !secondHalf(week, q3s, q4e)) {
                return true;
            } else if (repeatType.equals("G151") && !secondHalf(week, q3s, q4e) && week < q1e) {
                return true;
            } else if (repeatType.equals("G152") && !secondHalf(week, q3s, q4e) && week > q2s) {
                return true;
            } else if (repeatType.equals("G2") && secondHalf(week, q3s, q4e)) {
                return true;
            } else if (repeatType.equals("G251") && secondHalf(week, q3s, q4e) && week < q3e) {
                return true;
            } else if (repeatType.equals("G252") && secondHalf(week, q3s, q4e) && week > q4s) {
                return true;
            }
        } else if (repeatType.startsWith("U") && !evenWeek) {
            if (repeatType.equals("U")) {
                return true;
            } else if (repeatType.equals("U1") && !secondHalf(week, q3s, q4e)) {
                return true;
            } else if (repeatType.equals("U2") && secondHalf(week, q3s, q4e)) {
                return true;
            }
        } else if (repeatType.startsWith("T")) {
            if (checkT(week, Integer.parseInt(repeatType.charAt(1) + ""))) {
                if (repeatType.length() > 2) {
                    if (repeatType.charAt(2) == '1' && !secondHalf(week, q3s, q4e)) {
                        return true;
                    } else if (repeatType.charAt(2) == '2' && secondHalf(week, q3s, q4e)) {
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

    public static boolean secondHalf(int week, int q3s, int q4e) {
        return (week >= q3s) && (week <= q4e);
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
        int lowestHour = 12;
        int highestHour = 0;
        for (HWLesson lesson : lessons) {
            if (lesson.getDay() == day) {
                if (lowestHour > lesson.getHour()) {
                    lowestHour = lesson.getHour();
                }
                if (highestHour < lesson.getHour()) {
                    highestHour = lesson.getHour();
                }
            }
        }
        if (lowestHour > highestHour) {
            return new int[]{0, 0};
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
                    lesson.getHour() + itemSeparator +
                    lesson.getTeacher() + itemSeparator +
                    lesson.getSubject() + itemSeparator +
                    lesson.getRoom() + itemSeparator +
                    lesson.getRepeatType() + lineSeparator;
        }
        return CSV;
    }

    public static String getURLForShare(HWLesson[] hwLessons, HWGrade grade, String itemSeparator, String lineSeparator) {
        ArrayList<HWLesson[]> combinedData = new ArrayList<>();
        for(HWLesson lesson : hwLessons){
            boolean found = false;
            for(HWLesson[] usedLessonArray : combinedData) {
                for (HWLesson usedLesson: usedLessonArray){
                    if(usedLesson == lesson){
                        found = true;
                        break;
                    }
                }
            }
            if(!found){
                combinedData.add(CombineData.getSimilarLessons(lesson, hwLessons));
            }
        }
        if (hwLessons.length > 0) {
            String URL = "http://vp-edit.ga/?grade=" + grade.getGradeName() + "&table=";
            for (HWLesson[] lessons : combinedData) {
                    String hours = "";
                    for (HWLesson lesson : lessons) {
                        hours += lesson.getHour() + "_";
                    }
                    URL += lessons[0].getDay() + itemSeparator +
                            hours + itemSeparator +
                            lessons[0].getTeacher() + itemSeparator +
                            lessons[0].getSubject() + itemSeparator +
                            lessons[0].getRoom() + itemSeparator +
                            lessons[0].getRepeatType() + lineSeparator;
                    Log.d(TAG, hours);
            }
            return URL;
            /*try {
                return URLEncoder.encode(URL, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
        }
        return "";
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
            lessons.add(new HWLesson(-1, new HWGrade(lessonInfo[0]),
                    Integer.parseInt(lessonInfo[2]),
                    Integer.parseInt(lessonInfo[1]),
                    lessonInfo[3],
                    lessonInfo[4],
                    lessonInfo[5],
                    lessonInfo[6]
            ));
        }
        return lessons.toArray(new HWLesson[lessons.size()]);
    }

    public static HWLesson[] parseURL(String URL, Context context) {
        String lineSeparator = Preferences.readStringFromPreferences(context, context.getString(R.string.LINE_SEP), "\\+");
        String itemSeparator = Preferences.readStringFromPreferences(context, context.getString(R.string.ITEM_SEP), ";");
        URL = URL.trim();
        Log.v(TAG, URL);
        String[] urlParts = URL.split("=");
        for (String part : urlParts) {
            Log.v(TAG, part);
        }
        if (urlParts.length >= 3) {
            if (urlParts[0].equals("http://vp-edit.ga/?grade")) {
                ArrayList<HWLesson> hwLessons = new ArrayList<>();
                Log.v(TAG, urlParts[1]);
                HWGrade grade = new HWGrade(urlParts[1].split("&")[0]);
                Log.v(TAG, grade.getGradeName());
                String[] lessons = urlParts[2].split(lineSeparator);
                for (String lesson : lessons) {
                    Log.v(TAG, lesson);
                    String[] lessonInfo = lesson.split(itemSeparator);
                    ArrayList<Integer> hours = new ArrayList<>();
                    for (String hour : lessonInfo[1].split("_")) {
                        hwLessons.add(new HWLesson(-1, grade, Integer.parseInt(hour),
                                Integer.parseInt(lessonInfo[0]),
                                lessonInfo[2],
                                lessonInfo[3],
                                lessonInfo[4],
                                lessonInfo[5]));
                    }
                }
                return hwLessons.toArray(new HWLesson[hwLessons.size()]);
            }
        }
        return new HWLesson[0];
    }

    public static HWLesson[] fillGabs(HWLesson[] lessons, int week, int day, Context context) {
        return fillGabs(lessons, week, day, context, false);
    }

    public static HWLesson[] fillGabs(HWLesson[] lessons, int week, int day, Context context, boolean fillWholeDay) {
        HWGrade grade;
        if (lessons.length > 0 || fillWholeDay) {
            if (lessons.length <= 0) {
                grade = new HWGrade(Preferences.readStringFromPreferences(context, context.getString(R.string.SELECTED_GRADE), ""));
            } else {
                grade = lessons[0].getGrade();
            }
            ArrayList<HWLesson> lessonArrayList = new ArrayList<>(Arrays.asList(lessons));
            int[] hours;
            if (!fillWholeDay) {
                hours = lengthOfDay(lessons, week, day, context);
            } else {
                hours = new int[]{1, 12};
            }

            Log.d(TAG, String.valueOf(hours[0]));
            Log.d(TAG, String.valueOf(hours[1]));
            ArrayList<Integer> placedHours = new ArrayList<>((hours[1] - hours[0] + 1));
            for (HWLesson lesson : lessons) {
                placedHours.add(lesson.getHour());
            }
            Collections.sort(placedHours);
            int lastHour = 12;
            for (int hour : placedHours) {
                if (hour > (lastHour + 1)) {
                    Integer[] breakHours = new Integer[hour - lastHour - 1];
                    for (int breakHour = lastHour + 1; breakHour < hour; breakHour++) {
                        Log.d(TAG, breakHour + "_" + lastHour + "_" + hour);
                        breakHours[hour - breakHour - 1] = breakHour;
                    }
                    Arrays.sort(breakHours);
                    for (int currentHour : breakHours) {
                        lessonArrayList.add(placedHours.indexOf(hour), new HWLesson(-1, grade, currentHour, day, "", "PAUSE", "", ""));
                    }
                }
                lastHour = hour;
            }
            return lessonArrayList.toArray(new HWLesson[lessonArrayList.size()]);
        }
        return new HWLesson[0];
    }

    public static String getDayName(int day, Context context) {
        switch (day) {
            case Calendar.MONDAY:
                return context.getString(R.string.dayMonday);

            case Calendar.TUESDAY:
                return context.getString(R.string.dayTuesday);

            case Calendar.WEDNESDAY:
                return context.getString(R.string.dayWednesday);

            case Calendar.THURSDAY:
                return context.getString(R.string.dayThursday);

            case Calendar.FRIDAY:
                return context.getString(R.string.dayFriday);

            case Calendar.SATURDAY:
                return context.getString(R.string.daySaturday);

            case Calendar.SUNDAY:
                return context.getString(R.string.daySunday);

            default:
                return context.getString(R.string.dayInvalid);
        }
    }

    public static VPData[] selectVPDataFromWeekDay(VPData[] vpDataArrray, int weekDay) {
        ArrayList<VPData> dataArrayList = new ArrayList<>(vpDataArrray.length);
        for (VPData data : vpDataArrray) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(data.getDate());
            if (calendar.get(Calendar.DAY_OF_WEEK) == weekDay) {
                dataArrayList.add(data);
            }
        }
        dataArrayList.trimToSize();
        return dataArrayList.toArray(new VPData[dataArrayList.size()]);
    }

    public static VPData[] selectVPDataFromSubscribedSubjects(VPData[] vpDataArray, String[] subscribedSubjects) {
        ArrayList<VPData> vpDataArrayList = new ArrayList<>();
        for (VPData data : vpDataArray) {
            if (isSubscribedSubject(data.getSubject(), subscribedSubjects)) {
                vpDataArrayList.add(data);
            }
        }
        return vpDataArrayList.toArray(new VPData[vpDataArrayList.size()]);
    }

    public static HWTime[] getHWTimes(Integer[] lessonDays, int pastDays, int futureDays) {
        ArrayList<HWTime> times = new ArrayList<>();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        HWTime referenceTime = getNextHWTime(new HWTime(yesterday), lessonDays);
        times.add(referenceTime);
        for (int i = 0; i < pastDays; i++) {
            times.add(0, getPreviousHWTime(times.get(0), lessonDays));
        }
        for (int i = 0; i < futureDays; i++) {
            times.add(getNextHWTime(times.get(times.size() - 1), lessonDays));
        }
        Calendar c = Calendar.getInstance();
        Log.d(TAG, "Test" + times.size());
        for (HWTime time : times) {
            c.setTime(time.toDate());
            Log.d(TAG, "" + c.get(Calendar.DAY_OF_WEEK));
        }
        return times.toArray(new HWTime[times.size()]);
    }

    public static HWTime getNextHWTime(HWTime hwTime, Integer[] lessonDays) {
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTime(hwTime.toDate());
        int weekDay = oldCalendar.get(Calendar.DAY_OF_WEEK);
        for (int daysToAdd = 1; daysToAdd < 7; daysToAdd++) {
            for (int lessonDay : lessonDays) {
                if (weekDay + daysToAdd >= 7) {
                    weekDay -= 7;
                }
                if (lessonDay == weekDay + daysToAdd) {
                    oldCalendar.add(Calendar.DATE, daysToAdd);
                    Log.d(TAG, weekDay + " " + daysToAdd);
                    return new HWTime(oldCalendar);
                }
            }
        }
        return null;
    }

    public static HWTime getPreviousHWTime(HWTime hwTime, Integer[] lessonDays) {
        Calendar oldCalendar = Calendar.getInstance();
        oldCalendar.setTime(hwTime.toDate());
        int weekDay = oldCalendar.get(Calendar.DAY_OF_WEEK);
        for (int daysToAdd = 1; daysToAdd < 7; daysToAdd++) {
            for (int lessonDay : lessonDays) {
                if (weekDay - daysToAdd <= 0) {
                    weekDay += 7;
                }
                if (lessonDay == weekDay - daysToAdd) {
                    oldCalendar.add(Calendar.DATE, -daysToAdd);
                    return new HWTime(oldCalendar);
                }
            }
        }
        return null;
    }

    public static String[] findSubscribableSubjects(HWLesson[] lessons) {
        ArrayList<String> subjectArrayList = new ArrayList<>();
        for (HWLesson lesson : lessons) {
            String subject = lesson.getSubject();
            String[] parts = subject.split("-");
            String subIndicator = parts[parts.length - 1];
            if (subIndicator.equals("W") || subIndicator.equals("WP") || subject.equals("KR") || subject.equals("ETH") || subject.equals("EVR")) {
                if (!subjectArrayList.contains(subject)) {
                    subjectArrayList.add(subject);
                }
            }
        }
        return subjectArrayList.toArray(new String[subjectArrayList.size()]);
    }
}
