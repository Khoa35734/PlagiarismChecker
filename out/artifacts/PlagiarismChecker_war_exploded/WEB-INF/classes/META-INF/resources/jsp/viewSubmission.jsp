<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<head>
    <title>Admin Documents</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }
        .container {
            max-width: 900px;
            margin: auto;
            background: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            border-bottom: 2px solid #f2f2f2;
            padding-bottom: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f2f2f2;
        }
        tr:hover {
            background-color: #f9f9f9;
        }
        .back-link {
            margin-top: 20px;
            display: inline-block;
        }
        .back-link a {
            color: #007bff;
            text-decoration: none;
        }
        .back-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Your Document Repository</h1>
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
                <td>${doc.uploadTime}</td>
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
            <tr><td colspan="5">No documents uploaded yet.</td></tr>
        </c:if>
        </tbody>
    </table>
    <p><a href="${pageContext.request.contextPath}/admin/dashboard">← Back to Dashboard</a></p>
</div>
</body>
</html>
