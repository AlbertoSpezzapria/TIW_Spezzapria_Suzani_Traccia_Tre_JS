package servlets;

import DAO.UserDAO;
import beans.User;
import jakarta.servlet.ServletContext;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.sql.Connection;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CheckLogin() {
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
		
		String usrn = request.getParameter("username");
		String pwd = request.getParameter("pwd");
		
		if (usrn == null || usrn.isEmpty() || pwd == null || pwd.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
			return;
		}
		
		UserDAO uDAO = new UserDAO(connection);
		User user = null;
		try {
			user = uDAO.checkCredentials(usrn, pwd);
		}catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
			return;
		}
		
		Integer id = -1;
		String path = getServletContext().getContextPath();
		if (user != null) {	
			try {
				if(user.getRole().equals("professor")) {
					id = uDAO.idProfessorUser(user.getIdUser());
				}else {
					id = uDAO.idStudentUser(user.getIdUser());
				}
			}catch (SQLException e) {
				e.printStackTrace();
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
				return;
			}
			
			if(id != -1) {
				user.setId(id);
			}else {
				response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking");
				return;
			}
		
			request.getSession().setAttribute("user", user);
			String target = "/GetCourses";
			path = path + target;
		} else {
			path = getServletContext().getContextPath() + "/login";
		}
		response.sendRedirect(path);
	}
}
	