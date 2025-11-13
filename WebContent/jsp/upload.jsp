<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Upload Submission</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
<div class="container">
    <h1>Plagiarism Checker</h1>
    <nav>
        <span>Signed in as <strong>${sessionScope.username}</strong></span>
        <a href="${pageContext.request.contextPath}/upload">Upload</a>
        <a href="${pageContext.request.contextPath}/results">Results</a>
        <a href="${pageContext.request.contextPath}/login?logout=true">Logout</a>
    </nav>
    <section>
        <h2>Submit your work</h2>
        <p>Upload a .txt file or paste your content below.</p>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert success">${message}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/upload" method="post" enctype="multipart/form-data" class="form-card">
            <label for="file">Upload .txt file</label>
            <input type="file" id="file" name="file" accept="text/plain" />

            <label for="textContent">Or paste content</label>
            <textarea id="textContent" name="textContent" rows="8" placeholder="Paste content here..."></textarea>

            <label for="filenameOverride">Optional filename</label>
            <input type="text" id="filenameOverride" name="filenameOverride" placeholder="my-submission.txt" />

            <button type="submit">Upload</button>
        </form>
    </section>
</div>
</body>
</html>
