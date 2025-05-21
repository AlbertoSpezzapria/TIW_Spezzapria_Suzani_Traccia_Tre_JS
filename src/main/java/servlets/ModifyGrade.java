package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import DAO.EnrolledStudentDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ModifyGrade")
public class ModifyGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public ModifyGrade() {
		super();
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String id1 = request.getParameter("idExamination");
		String id2 = request.getParameter("idStudent");
		String g = request.getParameter("grade");
		Integer idExamination = -1;
		Integer idStudent = -1;
		Integer grade = -1;
		
		if (id1 == null || id1.isEmpty() || id2 == null || id2.isEmpty() || g == null || g.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		try {
		    if (g.equalsIgnoreCase("30L")) {
		        grade = 31;
		    } else {
		        grade = Integer.parseInt(g);
		        if (!((grade >= 18 && grade <= 30) || grade == 31 || grade == -1 || grade == -2 || grade == -3)) {
		            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Grade out of bound");
		            return;
		        }
		    }
		} catch (NumberFormatException e) {
		    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid grade format");
		    return;
		}
		
		try {
			idExamination = Integer.parseInt(id1);
			idStudent = Integer.parseInt(id2);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
			return;
		}
				
		EnrolledStudentDAO esDAO = new EnrolledStudentDAO(connection);
		try {
			esDAO.professorInsertGrade(idExamination,idStudent,grade);
		}catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in enrolled database extraction");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetEnrolledStudent?id=" + id1;
		response.sendRedirect(path);
	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}
}