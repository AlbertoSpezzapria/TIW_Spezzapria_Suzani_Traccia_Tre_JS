package beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Report {
    private Integer idReport;
    private Timestamp timestamp;
    private String courseName;
    private Date examinationDate;
    private List<EnrolledStudent> verbalizedStudent;
    
    public Report(Integer idReport, Timestamp tmsp, String courseName, Date examinationDate) {
        this.idReport = idReport;
        this.timestamp = tmsp;
        this.courseName = courseName;
        this.examinationDate = examinationDate;
        this.verbalizedStudent = new ArrayList<EnrolledStudent>();
    }

    public int getIdReport() {
        return idReport;
    }

    public void setIdReport(Integer idReport) {
        this.idReport = idReport;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp tmsp) {
        this.timestamp = tmsp;
    }

    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String name) {
    	this.courseName = name;
    }
    
    public Date getExaminationDate() {
    	return examinationDate;
    }
    
    public void setExaminationDate(Date date) {
    	this.examinationDate = date;
    }
    
    public void addStudent(EnrolledStudent stud) {
    	verbalizedStudent.add(stud);
    }
    
    public List<EnrolledStudent> getStudentList() {
    	return verbalizedStudent;
    }
    
    
}
