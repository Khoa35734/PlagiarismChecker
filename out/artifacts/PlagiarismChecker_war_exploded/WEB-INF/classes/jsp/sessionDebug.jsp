<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Session Debug</title>
    <style>
        body {
            font-family: monospace;
            padding: 20px;
            background: #f0f0f0;
        }
        .info {
            background: white;
            padding: 15px;
            margin: 10px 0;
            border-radius: 5px;
            border-left: 4px solid #4CAF50;
        }
        .error {
            background: white;
            padding: 15px;
            margin: 10px 0;
            border-radius: 5px;
            border-left: 4px solid #f44336;
        }
        h1 {
            color: #333;
        }
        code {
            background: #eee;
            padding: 2px 5px;
            border-radius: 3px;
        }
    </style>
</head>
<body>
    <h1>🔍 Session Debug Info</h1>

    <div class="info">
        <h3>Session Status:</h3>
        <%
            HttpSession sess = request.getSession(false);
            if (sess == null) {
        %>
            <p class="error">❌ No session found!</p>
        <%
            } else {
        %>
            <p>✅ Session exists</p>
            <p><strong>Session ID:</strong> <%= sess.getId() %></p>
            <p><strong>Creation Time:</strong> <%= new java.util.Date(sess.getCreationTime()) %></p>
            <p><strong>Last Accessed:</strong> <%= new java.util.Date(sess.getLastAccessedTime()) %></p>
        <%
            }
        %>
    </div>

    <div class="info">
        <h3>Session Attributes:</h3>
        <%
            if (sess != null) {
                java.util.Enumeration<String> attrs = sess.getAttributeNames();
                boolean hasAttrs = false;
                while (attrs.hasMoreElements()) {
                    hasAttrs = true;
                    String attr = attrs.nextElement();
                    Object value = sess.getAttribute(attr);
        %>
                    <p><code><%= attr %></code> = <strong><%= value %></strong> (<%= value.getClass().getSimpleName() %>)</p>
        <%
                }
                if (!hasAttrs) {
        %>
                    <p class="error">⚠️ No attributes in session</p>
        <%
                }
            }
        %>
    </div>

    <div class="info">
        <h3>Expected Admin Attributes:</h3>
        <%
            if (sess != null) {
                Object adminLoggedIn = sess.getAttribute("adminLoggedIn");
                Object adminId = sess.getAttribute("adminId");
        %>
                <p><code>adminLoggedIn</code>:
                    <% if (adminLoggedIn != null && (Boolean)adminLoggedIn) { %>
                        <span style="color: green;">✅ <%= adminLoggedIn %></span>
                    <% } else { %>
                        <span style="color: red;">❌ <%= adminLoggedIn %></span>
                    <% } %>
                </p>
                <p><code>adminId</code>:
                    <% if (adminId != null) { %>
                        <span style="color: green;">✅ <%= adminId %></span>
                    <% } else { %>
                        <span style="color: red;">❌ null</span>
                    <% } %>
                </p>
        <%
            }
        %>
    </div>

    <div class="info">
        <h3>Quick Actions:</h3>
        <p><a href="<%= request.getContextPath() %>/login" style="color: #2196F3;">🔐 Go to Login</a></p>
        <p><a href="<%= request.getContextPath() %>/admin/dashboard" style="color: #2196F3;">📊 Go to Dashboard</a></p>
        <p><a href="<%= request.getContextPath() %>/adminUpload" style="color: #2196F3;">📤 Go to Upload</a></p>
    </div>
</body>
</html>

