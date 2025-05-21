package beans;

public class Student {
    private Integer idStudent;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String degree;

    public Student(Integer idStudent, String name, String surname, String email, String password, String degree) {
        this.idStudent = idStudent;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.degree = degree;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }
}
