package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.google.gson.Gson;

import DAO.CourseDAO;
import DAO.EnrolledStudentDAO;
import DAO.ExaminationDAO;
import beans.Course;
import beans.EnrolledStudent;
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

@WebServlet("/GetGrade")
public class GetGrade extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetGrade() {
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
		
		HttpSession s = request.getSession(); 
		User user = (User) s.getAttribute("user"); 
		String id = StringEscapeUtils.escapeJava(request.getParameter("id"));
		Integer idExamination = -1;
		
		try {
			idExamination = Integer.parseInt(id);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter id with format number is required");
			return;
		}
		
		ExaminationDAO exDAO = new ExaminationDAO(connection, user.getId());
		List<Integer> possibleExamination = null;
		try {
			possibleExamination = exDAO.getAllStudentExaminations();
		}catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in examination database extraction");
			return;
		}
		
		ExaminationDAO eDAO = new ExaminationDAO(connection, idExamination);
		Exam examination = null;
		try {
			examination = eDAO.getExamination();
		}catch (SQLException e) {
			e.printStackTrace(); 
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in examination database extraction");
			return;
		}
		
		Course course = null;
		CourseDAO cDAO = new CourseDAO(connection, examination.getIdCourse()); 
		
		try {
			course = cDAO.getCourse();
		}catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course database extraction");
			return;
		}
		
		EnrolledStudent studentGrade = null;
		Boolean enrolled = true;
		if(possibleExamination.contains(idExamination)) {
							
			EnrolledStudentDAO esDAO = new EnrolledStudentDAO(connection);
			try {
				studentGrade = esDAO.getStudentGrade(idExamination, user.getId());
			}catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in examination database extraction");
				return;
			}
        
		}else {
			enrolled = false;
		}
        
        String jsonCourse = new Gson().toJson(course);
        String jsonExamination = new Gson().toJson(examination);
        String jsonStudent = new Gson().toJson(studentGrade);
        String jsonEnrolled = new Gson().toJson(enrolled);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(jsonCourse);
		response.getWriter().write(jsonExamination);
		response.getWriter().write(jsonStudent);
		response.getWriter().write(jsonEnrolled);
        
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