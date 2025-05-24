package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import beans.Exam;

public class ExaminationDAO {
	private Connection dbConnection;
	private int id;
	
	public ExaminationDAO(Connection dbConnection, int id) {
		this.dbConnection = dbConnection;
		this.id = id;
	}
	
	public List<Exam> getExaminations() throws SQLException {
		List<Exam> examinationList = new ArrayList<>();
		String query = "SELECT idExamination, date, idCourse FROM examination WHERE idCourse = ? ORDER BY date DESC";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					Exam exam = new Exam(result.getInt("idExamination"), result.getDate("date"), result.getInt("idCourse"));					
					examinationList.add(exam);
				}
			}
		}
		return examinationList;		
	}
	
	public Exam getExamination() throws SQLException {
		Exam exam = null;
		String query = "SELECT idExamination, date, idCourse FROM examination WHERE idExamination = ?";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					exam = new Exam(result.getInt("idExamination"), result.getDate("date"), result.getInt("idCourse"));					
				}
			}
		}
		return exam;		
	}
	
	public List<Integer> getAllProfessorExaminations() throws SQLException {
		List<Integer> idExaminationList = new ArrayList<>();
		String query = "SELECT distinct(e.idExamination) FROM  course c JOIN examination e ON c.idCourse = e.idCourse WHERE c.idProfessor = ?";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					Integer id = result.getInt("idExamination");					
					idExaminationList.add(id);
				}
			}
		}
		return idExaminationList;		
	}
	
	public List<Integer> getAllStudentExaminations() throws SQLException {
		List<Integer> idExaminationList = new ArrayList<>();
		String query = "SELECT distinct(idExamination) FROM  registered WHERE idStudent = ?";
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, this.id);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					Integer id = result.getInt("idExamination");					
					idExaminationList.add(id);
				}
			}
		}
		return idExaminationList;		
	}
	
	
}

