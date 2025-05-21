package beans;

public class User {
	
	private Integer idUser;
	private String username;
	private String role;
	private Integer id;
	
	
	public User(Integer idUser, String username, String role, Integer id) {
		this.idUser = idUser;
		this.username = username;
		this.role = role;
		this.id = id;		
	}
	
	public User(Integer idUser, String username, String role) {
		this.idUser = idUser;
		this.username = username;
		this.role = role;		
	}

	public int getIdUser() {
		return idUser;
	}

	public String getRole() {
		return role;
	}

	public String getUsername() {
		return username;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public void setUsername(String un) {
		this.username = un;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public void setRole(String role) {
		this.role = role;
	}

}