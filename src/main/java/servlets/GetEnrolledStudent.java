package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.google.gson.Gson;

import DAO.CourseDAO;
import DAO.ExaminationDAO;
import DAO.EnrolledStudentDAO; 
import beans.Course;
import beans.Exam;
import beans.User;
import beans.EnrolledStudent;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/GetEnrolledStudent")
public class GetEnrolledStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	private JakartaServletWebApplication webApplication;

	public GetEnrolledStudent() {
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

		String id = request.getParameter("id");
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
			possibleExamination = exDAO.getAllExaminations();
		}catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in examination database extraction");
			return;
		}
		
		if(possibleExamination.contains(idExamination)) {
			
			ExaminationDAO eDAO = new ExaminationDAO(connection, idExamination);
			Exam examination = null;
			try {
				examination = eDAO.getExamination();
			}catch (SQLException e) {
				e.printStackTrace(); 
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in examination database extraction");
				return;
			}
			
			CourseDAO cDAO = new CourseDAO(connection, examination.getIdCourse()); 
			Course course = null;
			try {
				course = cDAO.getCourse();
			}catch (SQLException e) {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in course database extraction");
				return;
			}
			
			EnrolledStudentDAO esDAO = new EnrolledStudentDAO(connection);
			List<EnrolledStudent> enrolledStudents = new ArrayList<>();
			try {
				enrolledStudents = esDAO.getEnrolledStudents(idExamination);
			}catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student database extraction");
				return;
			}
			
			WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
			ctx.setVariable("course", course);
			ctx.setVariable("examination", examination);
			ctx.setVariable("students", enrolledStudents);
			response.setContentType("text/html;charset=UTF-8");
	        templateEngine.process("enrolled", ctx, response.getWriter());
	        
	        String jsonCourse = new Gson().toJson(course);
	        String jsonExamination = new Gson().toJson(examination);
	        String jsonStudents = new Gson().toJson(enrolledStudents);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(jsonCourse);
			response.getWriter().write(jsonExamination);
			response.getWriter().write(jsonStudents);
	        
		}else {
			
			String path = getServletContext().getContextPath() + "/GetCourses";
	        response.sendRedirect(path);
		}
				
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