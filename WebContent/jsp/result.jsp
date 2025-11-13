<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Similarity Results</title>
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
        <h2>Similarity Overview</h2>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>
        <c:choose>
            <c:when test="${empty results}">
                <p>No comparison results yet. Submit new content to start the analysis.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>Submission</th>
                        <th>Compared With</th>
                        <th>Similarity (%)</th>
                        <th>Uploaded</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${results}">
                        <tr>
                            <td>${row.submissionName}</td>
                            <td><c:out value="${row.comparedName}" default="-" /></td>
                            <td><fmt:formatNumber value="${row.similarity * 100}" type="number" maxFractionDigits="2" />%</td>
                            <td><fmt:formatDate value="${row.uploadTime}" pattern="yyyy-MM-dd HH:mm" /></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </section>
</div>
</body>
</html>
