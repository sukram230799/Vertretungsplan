package wuest.markus.vertretungsplan;

import java.util.Date;

public class VPData {

    private int _id;
    private HWGrade _grade;
    //private int _hour;
    private String _subject;
    private String _room;
    private String _info1;
    private String _info2;
    private Date _date;
    private Integer[] _hours;

    public VPData() {
    }

    public VPData(HWGrade grade, Integer[] hours, String subject, String room, String info1, String info2, Date date) {
        this._grade = grade;
        this._hours = hours;
        this._subject = subject;
        this._room = room;
        this._info1 = info1;
        this._info2 = info2;
        this._date = date;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_grade(HWGrade _grade) {
        this._grade = _grade;
    }

    public void set_subject(String _subject) {
        this._subject = _subject;
    }

    public void set_room(String _room) {
        this._room = _room;
    }

    public void set_info1(String _info1) {
        this._info1 = _info1;
    }

    public void set_info2(String _info2) {
        this._info2 = _info2;
    }

    public void set_hours(Integer[] _hours) {
        this._hours = _hours;
    }

    public int get_id() {
        return _id;
    }

    public HWGrade get_grade() {
        return _grade;
    }

    public String get_subject() {
        return _subject;
    }

    public String get_room() {
        return _room;
    }

    public String get_info1() {
        return _info1;
    }

    public Date get_date() {
        return _date;
    }

    public void set_date(Date _date) {
        this._date = _date;
    }

    public String get_info2() {
        return _info2;
    }

    public Integer[] get_hours() {
        return _hours;
    }
}
