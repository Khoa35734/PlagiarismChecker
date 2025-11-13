<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Plagiarism Checker - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
</head>
<body>
<div class="container">
    <h1>Plagiarism Checker</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/login">Login</a>
    </nav>
    <section>
        <h2>Sign in</h2>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>
        <c:if test="${not empty message}">
            <div class="alert success">${message}</div>
        </c:if>
        <form action="${pageContext.request.contextPath}/login" method="post" class="form-card">
            <label for="username">Username</label>
            <input type="text" name="username" id="username" required />

            <label for="password">Password</label>
            <input type="password" name="password" id="password" required />

            <button type="submit">Login</button>
        </form>
        <p class="info">Use credentials seeded in the database.</p>
    </section>
</div>
</body>
</html>
