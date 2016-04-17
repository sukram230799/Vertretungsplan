package wuest.markus.vertretungsplan;

import java.util.Date;

public class VPData {

    private int id;
    private HWGrade grade;
    //private int _hour;
    private String subject;
    private String room;
    private String info1;
    private String info2;
    private Date date;
    private int hour;

    public VPData(int id, HWGrade grade, int hour, String subject, String room, String info1, String info2, Date date) {
        this.id = id;
        this.grade = grade;
        this.hour = hour;
        this.subject = subject;
        this.room = room;
        this.info1 = info1;
        this.info2 = info2;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGrade(HWGrade grade) {
        this.grade = grade;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getId() {
        return id;
    }

    public HWGrade getGrade() {
        return grade;
    }

    public String getSubject() {
        return subject;
    }

    public String getRoom() {
        return room;
    }

    public String getInfo1() {
        return info1;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getInfo2() {
        return info2;
    }

    public int getHour() {
        return hour;
    }
}
