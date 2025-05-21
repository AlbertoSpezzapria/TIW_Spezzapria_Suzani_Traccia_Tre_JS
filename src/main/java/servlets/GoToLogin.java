package servlets;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class GoToLogin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public GoToLogin() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String error = request.getParameter("error");
        request.setAttribute("error", error);
    	
        request.getRequestDispatcher("/WEB-INF/templates/login.html").forward(request, response);
    }
}