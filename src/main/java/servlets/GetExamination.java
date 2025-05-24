package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import DAO.CourseDAO;
import DAO.ExaminationDAO;
import beans.Course;
import beans.Exam;
import beans.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/GetExamination")
public class GetExamination extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetExamination() {
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
		
		String id = StringEscapeUtils.escapeJava(request.getParameter("id"));
		Integer idCourse = -1;
		
		try {
			idCourse = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
			return;
		}
		
		CourseDAO cDAO = new CourseDAO(connection, idCourse); 
		Course course = null;
		try {
			course = cDAO.getCourse();
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course database extraction");
			return;
		}
		
		ExaminationDAO eDAO = new ExaminationDAO(connection, idCourse);
		List<Exam> examinations = null;
		try {
			examinations = eDAO.getExaminations();
		}catch (SQLException e) {
			e.printStackTrace(); 
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in examination database extraction");
			return;
		}	
        
		JsonObject responseObj = new JsonObject();
		responseObj.add("course", new Gson().toJsonTree(course));
		responseObj.add("examinations", new Gson().toJsonTree(examinations));
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(responseObj.toString());
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