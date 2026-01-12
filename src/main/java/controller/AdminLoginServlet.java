package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bo.UserBO;

import java.io.IOException;
 

public class AdminLoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/adminLogin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserBO userBO = new UserBO();
        Integer adminId = userBO.authenticateAdmin(username, password);
        if (adminId != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("adminLoggedIn", true);
            session.setAttribute("adminId", adminId);
            response.sendRedirect(request.getContextPath() + "/admin");
            return;
        }

        request.getRequestDispatcher("/jsp/adminLogin.jsp").forward(request, response);
    }

    // Authentication moved to model.bo.UserBO
}
