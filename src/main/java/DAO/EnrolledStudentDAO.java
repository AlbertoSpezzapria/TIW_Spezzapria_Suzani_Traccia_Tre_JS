package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.EnrolledStudent;

public class EnrolledStudentDAO {
	private Connection dbConnection;
	
	public EnrolledStudentDAO(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}
	
	public List<EnrolledStudent> getEnrolledStudents(int id) throws SQLException {
		List<EnrolledStudent> enrolledList = new ArrayList<>();
		String query = "SELECT s.idStudent, s.name, s.surname, s.email, s.degree, r.grade, r.evaluationState"
				+ " FROM registered r JOIN student s ON r.idStudent = s.idStudent"
				+ " WHERE r.idExamination = ?";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					EnrolledStudent enrolledStudent = new EnrolledStudent(result.getInt("idStudent"), result.getString("name"), result.getString("surname"), result.getString("email"), result.getString("degree"), result.getInt("grade"), result.getString("evaluationState"));					
					enrolledList.add(enrolledStudent);
				}
			}
		}
		return enrolledList;		
	}
	
	public void professorInsertGrade(int idExamination, int idStudent, int grade) throws SQLException {
	    String query = "UPDATE registered SET grade = ?, evaluationState = 'inserito' WHERE idExamination = ? AND idStudent = ?";	    	
	    try (PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
	    	pstatement.setInt(1, grade);
	    	pstatement.setInt(2, idExamination);
	    	pstatement.setInt(3,  idStudent);
	    	int rowsUpdated = pstatement.executeUpdate();
	    	if(rowsUpdated == 0) {
	    		throw new SQLException("Nessuna riga aggiornata: studente non trovato");
	    	}
	    }
	}
	
	public void professorPublishGrades(int idExamination) throws SQLException {
	    String statusQuery = "UPDATE registered SET evaluationState = 'pubblicato' WHERE idExamination = ? AND evaluationState = 'inserito'";
	    try (PreparedStatement pstatement = dbConnection.prepareStatement(statusQuery)) {
	    	pstatement.setInt(1, idExamination);
	    	pstatement.executeUpdate();
	    }
	}
	
	public void studentRejectGrade(int idExamination, int idStudent) throws SQLException {
	    String statusQuery = "UPDATE registered SET evaluationState = 'rifiutato' WHERE idExamination = ? AND idStudent = ? AND evaluationState = 'pubblicato'";
	    try (PreparedStatement pstatement = dbConnection.prepareStatement(statusQuery)) {
	        pstatement.setInt(1, idExamination);
	        pstatement.setInt(2, idStudent);
	        int rows = pstatement.executeUpdate();
	        if (rows == 0) {
	            throw new SQLException("Voto non rifiutabile (non pubblicato o non trovato)");
	        }
	    }
	}
	
	public EnrolledStudent getEnrolledStudent(int idExamination, int idStudent) throws SQLException {
		EnrolledStudent  enrolledStudent = null;
		String query = "SELECT s.idStudent, s.name, s.surname, s.email, s.degree, r.grade, r.evaluationState"
				+ " FROM registered r JOIN student s ON r.idStudent = s.idStudent"
				+ " WHERE r.idExamination = ? AND s.idStudent = ?";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, idExamination);
			pstatement.setInt(2, idStudent);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					enrolledStudent = new EnrolledStudent(result.getInt("idStudent"), result.getString("name"), result.getString("surname"), result.getString("email"), result.getString("degree"), result.getInt("grade"), result.getString("evaluationState"));					
				}
			}
		}
		return enrolledStudent;		
	}
	
	public EnrolledStudent getStudentGrade(int idExamination, int idStudent) throws SQLException {
		EnrolledStudent  enrolledStudent = null;
		String query = "SELECT s.idStudent, s.name, s.surname, s.email, s.degree, r.grade, r.evaluationState"
				+ " FROM registered r JOIN student s ON r.idStudent = s.idStudent"
				+ " WHERE r.idExamination = ? AND s.idStudent = ? AND (r.evaluationState = 'pubblicato' OR r.evaluationState = 'verbalizzato' OR r.evaluationState = 'rifiutato')";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, idExamination);
			pstatement.setInt(2, idStudent);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					enrolledStudent = new EnrolledStudent(result.getInt("idStudent"), result.getString("name"), result.getString("surname"), result.getString("email"), result.getString("degree"), result.getInt("grade"), result.getString("evaluationState"));					
				}
			}
		}
		return enrolledStudent;		
	}
	
}