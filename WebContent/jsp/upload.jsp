<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Plagiarism Checker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
<div class="container">
    <h1>Plagiarism Checker</h1>
    <p class="info">Upload PDF, DOCX, or TXT files (total ≤ 25 MB). You do not need an account—each batch receives a private guest link to view results.</p>
    <nav>
        <span>Signed in as <strong>${sessionScope.username}</strong></span>
        <a href="${pageContext.request.contextPath}/upload">Upload</a>
        <a href="${pageContext.request.contextPath}/results">Results</a>
        <a href="${pageContext.request.contextPath}/login?logout=true">Logout</a>
    </nav>
    <section>
        <h2>Submit your work</h2>
        <p>Upload a PDF, DOCX, or TXT file, or paste your content below.</p>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert success">${message}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data" class="form-card">
            <label for="files">Select files (processed sequentially):</label>
            <input type="file" id="files" name="files" accept=".pdf,.docx,.txt" multiple required />
            <small class="helper">Total size of all files must stay under 25 MB. Larger batches will be rejected.</small>

            <label for="textContent">Or paste content for quick checks:</label>
            <textarea id="textContent" name="textContent" rows="8" placeholder="Paste content here..."></textarea>

            <label for="filenameOverride">Optional label (used when pasting text):</label>
            <input type="text" id="filenameOverride" name="filenameOverride" placeholder="my-submission.txt" />

            <button type="submit">Start Check</button>
        </form>
        <c:if test="${not empty sessionScope.guestToken}">
            <div class="info">
                <a href="${pageContext.request.contextPath}/results">View previous results for this session</a>
            </div>
        </c:if>
    </section>
</div>
</body>
</html>
