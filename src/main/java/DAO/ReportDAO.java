package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import beans.Exam;
import beans.Report;
import beans.ReportRaw;

public class ReportDAO {
	private Connection dbConnection;
	
	public ReportDAO(Connection dbConnection) {
		this.dbConnection = dbConnection;
	}
	
	public int createReport(int idExamination) throws SQLException {	    
	    String insertReport = "INSERT INTO report (dateReport, idExamination) VALUES (NOW(), ?)";
	    	    
	    String updateRegistered = "UPDATE registered SET evaluationState = 'verbalizzato' WHERE idExamination = ? AND (evaluationState = 'pubblicato' OR evaluationState = 'rifiutato')";
	    
	    String updateRejected = "UPDATE registered SET grade = '-2' WHERE idExamination = ? AND evaluationState = 'rifiutato'";
	    
	    String selectVerbalized = "SELECT idStudent, grade FROM registered WHERE idExamination = ? AND (evaluationState = 'pubblicato' OR evaluationState = 'rifiutato')";

	    String insertReportRaw = "INSERT INTO reportlline (idReport, idStudent, grade) VALUES (?, ?, ?)";
	    
	    int reportId = -1;

	    try {
	    	dbConnection.setAutoCommit(false); 
	        
	        try (PreparedStatement ps1 = dbConnection.prepareStatement(updateRejected)) {
	            ps1.setInt(1, idExamination);
	            ps1.executeUpdate();
	        }
	        
	        List<int[]> studentGrades = new ArrayList<>();
	        try (PreparedStatement ps = dbConnection.prepareStatement(selectVerbalized)) {
	            ps.setInt(1, idExamination);
	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    studentGrades.add(new int[]{rs.getInt("idStudent"), rs.getInt("grade")});
	                }
	            }
	        }
	        
	        if(!studentGrades.isEmpty()) {
	        	
	        	try (PreparedStatement ps2 = dbConnection.prepareStatement(insertReport, Statement.RETURN_GENERATED_KEYS)) {
		            ps2.setInt(1, idExamination);
		            ps2.executeUpdate();
		            try (ResultSet rs = ps2.getGeneratedKeys()) {
		                if (rs.next()) {
		                    reportId = rs.getInt(1);
		                } else {
		                    throw new SQLException("Creazione report fallita, nessun ID ottenuto.");
		                }
		            }
		        }	 
	        	
	        	
	        	try (PreparedStatement ps = dbConnection.prepareStatement(insertReportRaw)) {
		            for (int[] sg : studentGrades) {
		                ps.setInt(1, reportId);
		                ps.setInt(2, sg[0]);
		                ps.setInt(3, sg[1]);
		                ps.addBatch();
		            }
		            ps.executeBatch();
	            
	        	}
	        }
	        
	        try (PreparedStatement ps3 = dbConnection.prepareStatement(updateRegistered)) {
	            ps3.setInt(1, idExamination);
	            ps3.executeUpdate();
	        }
	        dbConnection.commit();

        } catch (SQLException e) {
        	dbConnection.rollback();
	        throw e;
	    } finally {
	    	dbConnection.setAutoCommit(true);
	    }
	    
	    return reportId;
    }
	
	public List<Report> getAllReports(int idProfessor) throws SQLException {
	    String query = "SELECT r.idReport, e.idExamination, r.dateReport, c.name, e.date " +
	                   "FROM report r JOIN examination e ON r.idExamination = e.idExamination " +
	                   "JOIN course c ON e.idCourse = c.idCourse " +
	                   "WHERE c.idProfessor = ? " +
	                   "ORDER BY c.name ASC, e.date ASC";

	    List<Report> reportList = new ArrayList<>();

	    try (PreparedStatement ps = dbConnection.prepareStatement(query)) {
	        ps.setInt(1, idProfessor);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Report report = new Report(rs.getInt("idReport"), rs.getInt("idExamination"), rs.getTimestamp("dateReport"), rs.getString("name"), rs.getDate("date"));
	                reportList.add(report);
	            }
	        }
	    }
	    return reportList;
	}
	
	public List<ReportRaw> getReportRaw(int idReport) throws SQLException {
	    String query = "SELECT rr.idReport, rr.idStudent, s.surname, s.name, rr.grade " +
	                   "FROM reportlline rr JOIN student s ON rr.idStudent = s.idStudent " +
	                   "WHERE rr.idReport = ?";

	    List<ReportRaw> reportRaws = new ArrayList<>();

	    try (PreparedStatement ps = dbConnection.prepareStatement(query)) {
	        ps.setInt(1, idReport);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                ReportRaw raw = new ReportRaw(rs.getInt("idReport"), rs.getInt("idStudent"), rs.getString("surname"), rs.getString("name"), rs.getInt("grade"));
	                reportRaws.add(raw);
	            }
	        }
	    }
	    return reportRaws;
	}
	
	public Report getReportById(int idReport) throws SQLException {
		String query = "SELECT rr.idReport, ee.idExamination, rr.dateReport, cc.name, ee.date " +
	               "FROM report rr " +
	               "JOIN examination ee ON rr.idExamination = ee.idExamination " +
	               "JOIN course cc ON ee.idCourse = cc.idCourse " +
	               "WHERE rr.idReport = ?";
		 
		Report report = null;
		
		try(PreparedStatement pstatement = dbConnection.prepareStatement(query)) {
			pstatement.setInt(1, idReport);
			try(ResultSet result = pstatement.executeQuery()) {
				while(result.next()) {
					report = new Report(result.getInt("idReport"),result.getInt("idExamination"),result.getTimestamp("dateReport"),result.getString("name"),result.getDate("date"));					
				}
			}
		}
		return report;
		
	}
	
}