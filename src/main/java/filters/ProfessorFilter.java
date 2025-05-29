package filters;

import java.io.IOException;

import beans.User;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ProfessorFilter implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String homepath = req.getServletContext().getContextPath() + "/GetCourses";

		HttpSession s = req.getSession();
		String path = (String) s.getAttribute("lastVisited");
		User user = (User) s.getAttribute("user");
		if (!user.getRole().equals("professor")) {
			
			String currentUrl = req.getRequestURI() + 
			        (req.getQueryString() != null ? "?" + req.getQueryString() : "");

		    if (path != null && !path.equals(currentUrl)) {
		        res.sendRedirect(path);
		    } else {
		        res.sendRedirect(homepath);
		    }
			return;
		}
		
		chain.doFilter(request, response);
	}
		
}