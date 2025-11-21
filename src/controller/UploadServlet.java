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
import utils.FileParser;
import utils.TextCleaner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

@MultipartConfig
public class UploadServlet extends HttpServlet {
    private static final String SESSION_SUBMISSIONS = "anonymousSubmissions";
    private static final long MAX_TOTAL_BYTES = 25L * 1024L * 1024L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        Integer userId = (Integer) session.getAttribute("userId");
        String guestToken = (String) session.getAttribute("guestToken");
        if (guestToken == null && userId == null) {
            guestToken = UUID.randomUUID().toString();
            session.setAttribute("guestToken", guestToken);
        }

        Collection<Part> parts = request.getParts();
        Deque<FilePayload> stack = new ArrayDeque<>();
        long totalBytes = 0L;

        for (Part part : parts) {
            if (!"files".equals(part.getName()) || part.getSize() <= 0) {
                continue;
            }
            totalBytes += part.getSize();
            if (totalBytes > MAX_TOTAL_BYTES) {
                rejectOversize(request, response);
                return;
            }
            try (InputStream inputStream = part.getInputStream()) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                inputStream.transferTo(buffer);
                byte[] data = buffer.toByteArray();
                String submittedName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
                stack.push(new FilePayload(submittedName, data));
            }
        }

        String textInput = request.getParameter("textContent");
        if (textInput != null && !textInput.isBlank()) {
            byte[] textBytes = textInput.getBytes(StandardCharsets.UTF_8);
            totalBytes += textBytes.length;
            if (totalBytes > MAX_TOTAL_BYTES) {
                rejectOversize(request, response);
                return;
            }
            String filename = request.getParameter("filenameOverride");
            if (filename == null || filename.isBlank()) {
                filename = "text-input.txt";
            }
            stack.push(new FilePayload(filename, textBytes));
        }

        if (stack.isEmpty()) {
            request.setAttribute("error", "Please upload at least one PDF, DOCX, or TXT file (or paste text).");
            request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
            return;
        }

        String batchToken = UUID.randomUUID().toString();
        session.setAttribute("latestBatchToken", batchToken);

        List<Integer> submissionIds = new ArrayList<>();
        List<QueueTicket> tickets = new ArrayList<>();
        int stackOrder = 0;

        while (!stack.isEmpty()) {
            stackOrder++;
            FilePayload payload = stack.pop();
            try (InputStream dataStream = new ByteArrayInputStream(payload.bytes)) {
                String rawContent = FileParser.extractText(dataStream, payload.filename);
                String cleanedContent = TextCleaner.clean(rawContent);
                int submissionId = saveSubmission(batchToken, guestToken, userId, payload.filename, rawContent, cleanedContent, payload.bytes.length, stackOrder);
                submissionIds.add(submissionId);
                tickets.add(new QueueTicket(submissionId, QueueWorker.enqueueSubmission(submissionId)));
                rememberSubmission(session, submissionId);
            } catch (Exception ex) {
                request.setAttribute("error", "Failed to process " + payload.filename + ": " + ex.getMessage());
                request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
                return;
            }
        }

        for (QueueTicket ticket : tickets) {
            try {
                ticket.future().get(Duration.ofMinutes(2).toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS);
            } catch (Exception ex) {
                request.setAttribute("error", "Processing timed out for submission #" + ticket.submissionId());
                request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/results?batch=" + batchToken);
    }

    private void rejectOversize(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("error", "Total upload size must not exceed 25MB.");
        request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
    }

    private void rememberSubmission(HttpSession session, int submissionId) {
        @SuppressWarnings("unchecked")
        List<Integer> submissionIds = (List<Integer>) session.getAttribute(SESSION_SUBMISSIONS);
        if (submissionIds == null) {
            submissionIds = new ArrayList<>();
        }
        submissionIds.add(submissionId);
        session.setAttribute(SESSION_SUBMISSIONS, submissionIds);
    }

    private int saveSubmission(String batchToken, String guestToken, Integer userId, String filename, String rawContent,
                               String cleanedContent, long uploadSize, int stackOrder) throws SQLException {
        String sql = "INSERT INTO Submissions (batch_token, guest_token, user_id, filename, raw_content, cleaned_content, status, stack_order, upload_size) " +
                     "VALUES (?, ?, ?, ?, ?, ?, 'QUEUED', ?, ?)";
        try (Connection connection = DatabaseUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, batchToken);
            statement.setString(2, guestToken);
            if (userId != null) {
                statement.setInt(3, userId);
            } else {
                statement.setNull(3, java.sql.Types.INTEGER);
            }
            statement.setString(4, filename);
            statement.setString(5, rawContent);
            statement.setString(6, cleanedContent);
            statement.setInt(7, stackOrder);
            statement.setLong(8, uploadSize);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to persist submission");
    }

    private record FilePayload(String filename, byte[] bytes) {
    }

    private record QueueTicket(int submissionId, java.util.concurrent.CompletableFuture<Void> future) {
    }
}
