package wuest.markus.vertretungsplan;

public class HWGrade {

    private String GradeName;

    public HWGrade() {
    }

    public HWGrade(String GradeName) {
        this.GradeName = GradeName;
    }


    public void setGradeName(String gradeName) {
        this.GradeName = gradeName;
    }

    public String getGradeName() {
        return GradeName;
    }
}
