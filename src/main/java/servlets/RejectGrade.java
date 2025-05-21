package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import DAO.EnrolledStudentDAO;
import beans.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/RejectGrade")
public class RejectGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public RejectGrade() {
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String id1 = request.getParameter("id");
		HttpSession session = request.getSession(false); 
		User user = null;
		if (session != null) {
		    user = (User) session.getAttribute("user");
		}
		int idExamination = -1;
		int idStudent = -1;
		
		if (id1 == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters id1 null");
			return;
		}
		
		if (id1.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters id1 empty");
			return;
		}
		
		if (user == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters user null");
			return;
		}
		
		try {
			idExamination = Integer.parseInt(id1);
			idStudent = user.getId();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
			return;
		}
				
		EnrolledStudentDAO esDAO = new EnrolledStudentDAO(connection);
		try {
			esDAO.studentRejectGrade(idExamination, idStudent);
		}catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in worker's project database extraction");
			return;
		}
		
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetGrade?id=" + idExamination;
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