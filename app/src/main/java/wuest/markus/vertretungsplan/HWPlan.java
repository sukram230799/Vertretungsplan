package wuest.markus.vertretungsplan;

import java.util.Date;

public class HWPlan {

    private HWGrade grade;
    private int hour;
    private int day;
    private String teacher;
    private String spSubject;
    private String spRoom;
    private String repeatType;

    private String vpSubject;
    private String vpRoom;
    private String info1;
    private String info2;
    private Date date;

    public HWPlan(HWGrade grade, int hour, int day, String teacher, String spSubject, String spRoom, String repeatType, String vpSubject, String vpRoom, String info1, String info2, Date date) {
        this.grade = grade;
        this.hour = hour;
        this.day = day;
        this.teacher = teacher;
        this.spSubject = spSubject;
        this.spRoom = spRoom;
        this.repeatType = repeatType;
        this.vpSubject = vpSubject;
        this.vpRoom = vpRoom;
        this.info1 = info1;
        this.info2 = info2;
        this.date = date;
    }

    public HWGrade getGrade() {
        return grade;
    }

    public void setGrade(HWGrade grade) {
        this.grade = grade;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getSpSubject() {
        return spSubject;
    }

    public void setSpSubject(String spSubject) {
        this.spSubject = spSubject;
    }

    public String getSpRoom() {
        return spRoom;
    }

    public void setSpRoom(String spRoom) {
        this.spRoom = spRoom;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getVpSubject() {
        return vpSubject;
    }

    public void setVpSubject(String vpSubject) {
        this.vpSubject = vpSubject;
    }

    public String getVpRoom() {
        return vpRoom;
    }

    public void setVpRoom(String vpRoom) {
        this.vpRoom = vpRoom;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
