package wuest.markus.vertretungsplan;

public class HWGrade {

    private int _id;
    private String _GradeName;

    public HWGrade(){}

    public HWGrade(String GradeName) {
        this._GradeName = GradeName;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void set_GradeName(String _GradeName) {
        this._GradeName = _GradeName;
    }

    public int get_id() {
        return _id;
    }

    public String get_GradeName() {
        return _GradeName;
    }
}
