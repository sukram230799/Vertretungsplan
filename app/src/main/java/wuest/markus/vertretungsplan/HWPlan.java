package wuest.markus.vertretungsplan;

import java.util.Date;

public class HWPlan {

    private HWGrade grade;
    private int hour;
    private String hourString;

    public String getHourString() {
        if(hourString == null || hourString.isEmpty()) {
            return String.valueOf(hour);
        }
        return hourString;
    }

    public void setHourString(String hourString) {
        this.hourString = hourString;
    }

    private int day;

    public int getSpId() {
        return spId;
    }

    public void setSpId(int spId) {
        this.spId = spId;
    }

    public int getVpId() {
        return vpId;
    }

    public void setVpId(int vpId) {
        this.vpId = vpId;
    }

    private int spId;
    private String spTeacher;
    private String spSubject;
    private String spRoom;
    private String spRepeatType;

    private int vpId;
    private String vpSubject;
    private String vpRoom;
    private String vpInfo1;
    private String vpInfo2;
    private Date vpDate;

    public HWPlan(HWGrade grade, int hour, int day, int spId, String spTeacher, String spSubject, String spRoom, String spRepeatType, int vpId, String vpSubject, String vpRoom, String vpInfo1, String vpInfo2, Date vpDate) {
        this.grade = grade;
        this.hour = hour;
        this.day = day;
        this.spId = spId;
        this.spTeacher = spTeacher;
        this.spSubject = spSubject;
        this.spRoom = spRoom;
        this.spRepeatType = spRepeatType;
        this.vpId = vpId;
        this.vpSubject = vpSubject;
        this.vpRoom = vpRoom;
        this.vpInfo1 = vpInfo1;
        this.vpInfo2 = vpInfo2;
        this.vpDate = vpDate;
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

    public String getSpTeacher() {
        return spTeacher;
    }

    public void setSpTeacher(String spTeacher) {
        this.spTeacher = spTeacher;
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

    public String getSpRepeatType() {
        return spRepeatType;
    }

    public void setSpRepeatType(String spRepeatType) {
        this.spRepeatType = spRepeatType;
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

    public String getVpInfo1() {
        return vpInfo1;
    }

    public void setVpInfo1(String vpInfo1) {
        this.vpInfo1 = vpInfo1;
    }

    public String getVpInfo2() {
        return vpInfo2;
    }

    public void setVpInfo2(String vpInfo2) {
        this.vpInfo2 = vpInfo2;
    }

    public Date getVpDate() {
        return vpDate;
    }

    public void setVpDate(Date vpDate) {
        this.vpDate = vpDate;
    }
}
