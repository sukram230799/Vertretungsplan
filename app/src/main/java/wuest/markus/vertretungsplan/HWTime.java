package wuest.markus.vertretungsplan;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class HWTime {

    private static final String TAG = "HWTime";

    private int hour;
    private int minute;
    private int year;
    private int month;
    private int day;

    public HWTime(HWTime hwTime) {
        this.hour = hwTime.getHour();
        this.minute = hwTime.getMinute();
        this.year = hwTime.getYear();
        this.month = hwTime.getMonth();
        this.day = hwTime.getDay();
    }

    public HWTime(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH) + 1;
        this.day = c.get(Calendar.DAY_OF_MONTH);
        this.hour = hour;
        this.minute = minute;
    }

    public HWTime(int hour, int minute, int year, int month, int day) {
        this.hour = hour;
        this.minute = minute;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public HWTime(Calendar c) {
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minute = c.get(Calendar.MINUTE);
        this.year = c.get(Calendar.YEAR);
        switch (c.get(Calendar.MONTH)){
            case Calendar.JANUARY:
                this.month = 1;
                break;
            case Calendar.FEBRUARY:
                this.month = 2;
                break;
            case Calendar.MARCH:
                this.month = 3;
                break;
            case Calendar.APRIL:
                this.month = 4;
                break;
            case Calendar.MAY:
                this.month = 5;
                break;
            case Calendar.JUNE:
                this.month = 6;
                break;
            case Calendar.JULY:
                this.month = 7;
                break;
            case Calendar.AUGUST:
                this.month = 8;
                break;
            case Calendar.SEPTEMBER:
                this.month = 9;
                break;
            case Calendar.OCTOBER:
                this.month = 10;
                break;
            case Calendar.NOVEMBER:
                this.month = 11;
                break;
            case Calendar.DECEMBER:
                this.month = 12;
                break;
            default:
                this.month = -1;
                break;
        }
        this.day = c.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        calendar.get(Calendar.DAY_OF_WEEK);
        return calendar.getTimeInMillis();
    }

    public Date toDate() {
        return new GregorianCalendar(year, month - 1, day, hour, minute).getTime();
    }

    public String toString() {
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(toDate());
    }

    /*
    public boolean after(HWTime compareTime, String caller) {
        Log.d(TAG, caller);
        if (hour > compareTime.getHour()) {
            return true;
        } else if (hour == compareTime.getHour()) {
            if (minute > compareTime.getMinute()) {
                return true;
            }
        }
        return false;
    }

    public boolean before(HWTime compareTime, String caller) {
        Log.d(TAG, caller);
        if (hour < compareTime.getHour()) {
            return true;
        } else if (hour == compareTime.getHour()) {
            if (minute < compareTime.getHour()) {
                return true;
            }
        }
        return false;
    }

    */
}
