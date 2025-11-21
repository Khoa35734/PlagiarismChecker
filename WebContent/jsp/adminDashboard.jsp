<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: auto;
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        tr:hover {
            background-color: #f1f1f1;
        }
        .action-links a {
            color: #007bff;
            text-decoration: none;
            margin-right: 10px;
        }
        .action-links a:hover {
            text-decoration: underline;
        }
        .logout-link {
            display: block;
            text-align: right;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="logout-link">
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>
    <h1>Admin Dashboard - Document Repository</h1>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Filename</th>
            <th>Size (bytes)</th>
            <th>Uploaded</th>
            <th>Actions</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="doc" items="${documents}">
            <tr>
                <td>${doc.id}</td>
                <td>${doc.filename}</td>
                <td>${doc.filesize}</td>
                <td><fmt:formatDate value="${doc.uploadTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/download?id=${doc.id}">Download</a>
                    <form action="${pageContext.request.contextPath}/admin/delete" method="post" style="display:inline" onsubmit="return confirm('Delete this document?');">
                        <input type="hidden" name="id" value="${doc.id}" />
                        <button type="submit">Delete</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty documents}">
            <tr>
                <td colspan="5" style="text-align: center;">No documents found.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>
