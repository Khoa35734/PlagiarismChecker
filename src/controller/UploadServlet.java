package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.DatabaseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@MultipartConfig
public class UploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (ensureAuthenticated(request, response) == null) {
            return;
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/upload.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer userId = ensureAuthenticated(request, response);
        if (userId == null) {
            return;
        }

        Part filePart = request.getPart("file");
        String textInput = request.getParameter("textContent");
        String filename = request.getParameter("filenameOverride");

        String fileContent = null;
        if (filePart != null && filePart.getSize() > 0) {
            String submittedName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            if (submittedName != null && !submittedName.isBlank()) {
                filename = submittedName;
            }
            fileContent = readPartContent(filePart);
        }

        if ((fileContent == null || fileContent.isBlank()) && (textInput == null || textInput.isBlank())) {
            request.setAttribute("error", "Please upload a text file or paste your content.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/upload.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (filename == null || filename.isBlank()) {
            filename = "text-input.txt";
        }

        String contentToPersist = (fileContent != null && !fileContent.isBlank()) ? fileContent : textInput;

        try {
            int submissionId = saveSubmission(userId, filename, contentToPersist);
            QueueWorker.enqueueSubmission(submissionId);
            request.setAttribute("message", "Submission queued for plagiarism check.");
        } catch (SQLException ex) {
            request.setAttribute("error", "Failed to save submission: " + ex.getMessage());
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/upload.jsp");
        dispatcher.forward(request, response);
    }

    private Integer ensureAuthenticated(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return (Integer) session.getAttribute("userId");
    }

    private String readPartContent(Part part) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private int saveSubmission(int userId, String filename, String content) throws SQLException {
        String sql = "INSERT INTO Submissions (user_id, filename, content) VALUES (?, ?, ?)";
           try (Connection connection = DatabaseUtils.getConnection();
               PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.setString(2, filename);
            statement.setString(3, content);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }
        throw new SQLException("No generated key returned for submission");
    }
}
