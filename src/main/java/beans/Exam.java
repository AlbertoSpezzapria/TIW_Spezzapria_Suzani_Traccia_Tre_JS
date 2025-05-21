package beans;

import java.util.Date;

public class Exam {
	private Integer idExam;
	private Date date;
	private Integer idCourse;
	
	public Exam(Integer idExam, Date date, Integer idCourse) {
		this.idExam = idExam;
		this.date = date;
		this.idCourse = idCourse;
	}
	
	public void setIdExam(Integer idExam) {
		this.idExam = idExam;
	}
	
	public Integer getIdExam() {
		return this.idExam;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public void setIdCourse(Integer idCourse) {
		this.idCourse = idCourse;
	}
	
	public Integer getIdCourse() {
		return this.idCourse;
	}
}
