package beans;

public class EnrolledStudent {
    private Integer idStudent;
    private String name;
    private String surname;
    private String email;
    private String degree;
    private Integer grade;
    private String evaluationState; 

    public EnrolledStudent(Integer idStudent, String name, String surname, String email, String degree) {
        this.idStudent = idStudent;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.degree = degree;
        this.grade = null;
        this.evaluationState = "non-inserito";
    }
    
    public EnrolledStudent(Integer idStudent, String name, String surname, String email, String degree, Integer grade, String evaluationState) {
        this.idStudent = idStudent;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.degree = degree;
        this.grade = grade;
        this.evaluationState = evaluationState;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(Integer idStudent) {
        this.idStudent = idStudent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    public int getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
    
    public String getEvaluationState() {
        return evaluationState;
    }

    public void setEvaluationState(String evaluationState) {
        this.evaluationState = evaluationState;
    }
}
