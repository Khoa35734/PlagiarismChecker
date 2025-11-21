<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://jakarta.ee/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://jakarta.ee/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Similarity Results</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
<div class="container">
    <h1>Plagiarism Checker</h1>
    <section>
        <h2>Similarity Overview</h2>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>
        <c:choose>
            <c:when test="${empty results}">
                <p>No comparison results yet. Upload files to start the analysis.</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>File</th>
                        <th>Status</th>
                        <th>Winnowing %</th>
                        <th>TF-IDF %</th>
                        <th>Source</th>
                        <th>Uploaded</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="row" items="${results}">
                        <tr>
                            <td>${row.filename}</td>
                            <td>${row.status}</td>
                            <td><fmt:formatNumber value="${row.simWinnowing * 100}" maxFractionDigits="2" />%</td>
                            <td><fmt:formatNumber value="${row.simTfidf * 100}" maxFractionDigits="2" />%</td>
                            <td><c:out value="${row.sourceDocument}" default="-" /></td>
                            <td><fmt:formatDate value="${row.uploadTime}" pattern="yyyy-MM-dd HH:mm" /></td>
                        </tr>
                        <tr>
                            <td colspan="6"><strong>Segments:</strong> ${row.matchedSegmentsJson}</td>
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
