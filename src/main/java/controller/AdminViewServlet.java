package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/admin/view")
public class AdminViewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? (Integer) session.getAttribute("adminId") : null;
        if (session == null || session.getAttribute("adminLoggedIn") == null || adminId == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/adminLogin.jsp");
            return;
        }

        // Use DocumentBO to fetch documents instead of inline SQL
        model.bo.DocumentBO docBo = new model.bo.DocumentBO();
        java.util.List<model.bean.DocumentBean> docs = docBo.listDocumentsByOwner(adminId);
        List<DocumentRow> documents = new ArrayList<>();
        for (model.bean.DocumentBean d : docs) {
            DocumentRow row = new DocumentRow();
            row.id = d.getId();
            row.filename = d.getOriginalName();
            row.filesize = d.getSize();
            java.time.Instant t = d.getUploadedAt();
            row.uploadTime = t == null ? null : java.sql.Timestamp.from(t);
            documents.add(row);
        }

        request.setAttribute("documents", documents);
        request.getRequestDispatcher("/jsp/viewSubmission.jsp").forward(request, response);
    }

    public static class DocumentRow {
        public int id;
        public String filename;
        public long filesize;
        public java.sql.Timestamp uploadTime;
    }
}
