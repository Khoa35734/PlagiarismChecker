package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.bo.ResultBO;
import model.bean.ResultWithSubmissionBean;

public class ResultServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer userId = session != null ? (Integer) session.getAttribute("userId") : null;
        String role = session != null ? (String) session.getAttribute("role") : null;
        String guestToken = session != null ? (String) session.getAttribute("guestToken") : null;
        String batchToken = request.getParameter("batch");

        ResultBO bo = new ResultBO();
        List<ResultWithSubmissionBean> results = bo.listResults(userId, role, guestToken, batchToken);
        request.setAttribute("results", results);
        request.getRequestDispatcher("/jsp/result.jsp").forward(request, response);
    }
}
