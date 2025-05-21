package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.EnrolledStudent;
import beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  idUser, username, role FROM user WHERE username = ? AND password = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) 
					return null;
				else {
					result.next();
					User user = new User(result.getInt("idUser"), result.getString("username"), result.getString("role"));
					return user;
				}
			}
		}
	}
	
	public int idStudentUser(int idUser) throws SQLException {
		int id = -1;
		String query = "SELECT idStudent FROM student WHERE idUser = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idUser);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					id = result.getInt("idStudent");
				}				
			}
		}
		return id;
	}
	
	public int idProfessorUser(int idUser) throws SQLException {
		int id = -1;
		String query = "SELECT idProfessor FROM professor WHERE idUser = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, idUser);
			try (ResultSet result = pstatement.executeQuery();) {
				while(result.next()) {
					id = result.getInt("idProfessor");
				}
			}
		}
		return id;
	}
}