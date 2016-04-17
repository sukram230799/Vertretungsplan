package wuest.markus.vertretungsplan;

import java.util.Date;

public class HWLesson {

    private int id;
    private HWGrade grade;
    //private Integer[] hours;
    private int hour;
    private int day;
    private String teacher;
    private String subject;
    private String room;
    private String repeatType;

    public HWTime getAssignedTime() {
        return assignedTime;
    }

    public void setAssignedTime(HWTime assignedTime) {
        this.assignedTime = assignedTime;
    }

    private HWTime assignedTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public HWLesson(int id, HWGrade grade, int hour, int day, String teacher, String subject, String room, String repeatType) {
        this.id = id;

        this.grade = grade;
        this.hour = hour;
        this.day = day;
        this.teacher = teacher;
        this.subject = subject;
        this.room = room;
        this.repeatType = repeatType;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }
}
