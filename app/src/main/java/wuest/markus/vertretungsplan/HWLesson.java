package wuest.markus.vertretungsplan;

public class HWLesson {

    private HWGrade grade;
    private Integer[] hours;
    private int day;
    private String teacher;
    private String subject;
    private String room;
    private String repeatType;

    public boolean changedSubject;
    public boolean changedRoom;

    public HWLesson(HWGrade grade, Integer[] hours, int day, String teacher, String subject, String room, String repeatType) {
        this.grade = grade;
        this.hours = hours;
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

    public Integer[] getHours() {
        return hours;
    }

    public void setHours(Integer[] hours) {
        this.hours = hours;
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
