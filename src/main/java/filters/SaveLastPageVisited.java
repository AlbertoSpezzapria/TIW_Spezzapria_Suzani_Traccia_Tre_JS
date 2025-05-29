package filters;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SaveLastPageVisited implements Filter {
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession s = req.getSession();
		String path = req.getServletPath();
		
		if(!path.contains("login") && !path.contains("error") && !path.contains(".css")){
			String URL = req.getRequestURI() + 
					(req.getQueryString() != null ? "?" + req.getQueryString() : "");
			s.setAttribute("lastVisited", URL);
		}
				
		chain.doFilter(request, response);
	}
		
}