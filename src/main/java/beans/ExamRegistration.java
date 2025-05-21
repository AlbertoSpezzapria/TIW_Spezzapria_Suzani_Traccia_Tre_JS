package beans;

public class ExamRegistration {
    private int idExam;
    private int idStudent;
    private int grade;
    private boolean isLode;
    private String evaluationState;

    public ExamRegistration(Integer idExam, Integer idStudent, Integer grade, boolean isLode, String evaluationState) {
        this.idExam = idExam;
        this.idStudent = idStudent;
        this.grade = grade;
        this.isLode = isLode;
        this.evaluationState = evaluationState;
    }

    public int getIdExam() {
        return idExam;
    }

    public void setIdExam(Integer idExam) {
        this.idExam = idExam;
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
    
    public boolean getIsLode() {
    	return isLode;
    }
    
    public void setIsLode() {
    	this.isLode = isLode;
    }

    public String getEvaluationState() {
        return evaluationState;
    }

    public void setEvaluationState(String evaluationState) {
        this.evaluationState = evaluationState;
    }
}
