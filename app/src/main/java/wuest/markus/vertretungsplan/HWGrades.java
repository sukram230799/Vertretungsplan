package wuest.markus.vertretungsplan;

public class HWGrades {

    private int _id;
    private HWGrade[] _hwGrades;

    public HWGrades(){}

    public HWGrades(HWGrade[] hwGrades) {
        this._hwGrades = hwGrades;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_GradeName(HWGrade[] hwGrades) {
        this._hwGrades = hwGrades;
    }

    public int get_id() {
        return _id;
    }

    public HWGrade[] get_GradeName() {
        return _hwGrades;
    }
}
