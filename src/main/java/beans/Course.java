package beans;

public class Course {

	private Integer idCourse;
	private String name;
	private Integer idProfessor;


	public Course(Integer idCourse, String name, Integer idProfessor) {
		super();
		this.idCourse = idCourse;
		this.name = name;
		this.idProfessor = idProfessor;
	}

	public Integer getIdCourse() {
		return idCourse;
	}

	public void setIdCourse(Integer id) {
		this.idCourse = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getIdProfessor() {
		return idProfessor;
	}

	public void setIdProfessor(Integer id) {
		this.idProfessor = id;
	}
}
