package beans;

public class Professor {
	private Integer idProfessor;
	private String name;
	private String surname;
	private String email;
	private String password;
	
	public Professor(Integer idProfessor, String name, String surname, String email, String password) {
		this.idProfessor = idProfessor;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
	}
	
	public void setIdProfessor(Integer id) {
		this.idProfessor = id;
	}
	
	public Integer getIdProfessor() {
		return this.idProfessor;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getSurname() {
		return this.surname;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setPassword(String psw) {
		this.password = psw;
	}
	
	public String getPassword() {
		return this.password;
	}

}
