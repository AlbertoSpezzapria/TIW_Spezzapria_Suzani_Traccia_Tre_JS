package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import DAO.EnrolledStudentDAO;
import DAO.ExaminationDAO;
import beans.EnrolledStudent;
import beans.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/GoToGradeForm")
public class GoToGradeForm extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine;
	private JakartaServletWebApplication webApplication;

	public GoToGradeForm() {
		super();
	}

	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		webApplication = JakartaServletWebApplication.buildApplication(servletContext);
	    WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(webApplication);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
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
		doPost(request, response);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession s = request.getSession(); 
		User user = (User) s.getAttribute("user"); 
				
		String id1 = request.getParameter("idExamination");
		String id2 = request.getParameter("idStudent");
		Integer idExamination = -1;
		Integer idStudent = -1;
		
		try {
			idExamination = Integer.parseInt(id1);
			idStudent = Integer.parseInt(id2);
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
			
			EnrolledStudent enrolledStudent = null;
			EnrolledStudentDAO esDAO = new EnrolledStudentDAO(connection);
			try {
				enrolledStudent = esDAO.getEnrolledStudent(idExamination,idStudent);
			}catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in student database extraction");
				return;
			}
			
			if(enrolledStudent == null || (!enrolledStudent.getEvaluationState().equals("non-inserito") && !enrolledStudent.getEvaluationState().equals("inserito"))) {
		    	String path = getServletContext().getContextPath() + "/GetEnrolledStudent?id=" + idExamination;
		        response.sendRedirect(path);
			}else {
			
				WebContext ctx = new WebContext(webApplication.buildExchange(request, response), request.getLocale());
				ctx.setVariable("idExamination", idExamination);
				ctx.setVariable("student", enrolledStudent);
				response.setContentType("text/html;charset=UTF-8");
		        templateEngine.process("gradeForm", ctx, response.getWriter());
			}
	        
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