package beans;

public class ReportRaw {
    private Integer idReport;
    private Integer idStudent;
    private String surname;
    private String name;
    private Integer grade;

    public ReportRaw(Integer idReport, Integer idStudent, String surname, String name, Integer grade) {
        this.idReport = idReport;
        this.idStudent = idStudent;
        this.surname = surname;
        this.name = name;
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
    
    public String getSurname() {
    	return surname;
    }
    
    public void setSurname(String surname) {
    	this.surname = surname;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setName(String name) {
    	this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}