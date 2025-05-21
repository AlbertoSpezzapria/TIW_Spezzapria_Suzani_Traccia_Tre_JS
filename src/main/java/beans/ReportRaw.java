package beans;

public class ReportRaw {
    private Integer idReport;
    private Integer idStudent;
    private Integer grade;

    public ReportRaw(Integer idReport, Integer idStudent, Integer grade) {
        this.idReport = idReport;
        this.idStudent = idStudent;
        this.grade = grade;
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(Integer idReport) {
        this.idReport = idReport;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(Integer idStudent) {
        this.idStudent = idStudent;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}

