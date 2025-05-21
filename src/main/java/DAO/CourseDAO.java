package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Course;

public class CourseDAO {
	private Connection dbConnection;
	private int id;
	
	public CourseDAO(Connection dbConnection, int id) {
		this.dbConnection = dbConnection;
		this.id = id;
	}
	
	public List<Course> getCoursesProfessor() throws SQLException {
		List<Course> courseList = new ArrayList<>();
		String query = "SELECT idCourse, name, idProfessor FROM course WHERE idProfessor = ? ORDER BY name DESC";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					Course course = new Course(result.getInt("idCourse"), result.getString("name"), result.getInt("idProfessor"));					
					courseList.add(course);
				}
			}
		}
		return courseList;		
	}
	
	public List<Course> getCoursesStudent() throws SQLException {
		List<Course> courseList = new ArrayList<>();
		String query = "SELECT DISTINCT c.idCourse, c.name, c.idProfessor"
				+ " FROM course c"
				+ " JOIN examination e ON c.idCourse = e.idCourse"
				+ " JOIN registered r ON e.idExamination = r.idExamination"
				+ " WHERE r.idStudent = ?"
				+ " ORDER BY c.name DESC";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					Course course = new Course(result.getInt("idCourse"), result.getString("name"), result.getInt("idProfessor"));					
					courseList.add(course);
				}
			}
		}
		return courseList;		
	}
	
	public Course getCourse() throws SQLException {
		Course course = null;
		String query = "SELECT idCourse, name, idProfessor FROM course WHERE idCourse = ?";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					course = new Course(result.getInt("idCourse"), result.getString("name"), result.getInt("idProfessor"));					
				}
			}
		}
		return course;	
	}
	
}

