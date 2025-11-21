<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin Upload</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h2>Admin Document Repository</h2>
        <p class="info">Admins may upload, download, and manage their private reference corpus. Each batch must stay under 25&nbsp;MB in total.</p>
        <form action="${pageContext.request.contextPath}/adminUpload" method="post" enctype="multipart/form-data">
            <label for="file">Choose one or more files to upload (PDF, DOCX, TXT):</label>
            <input type="file" id="file" name="file" accept=".pdf,.docx,.txt" multiple required>
            <small class="helper">Total size of the selected files must not exceed 25&nbsp;MB. Files will be processed sequentially.</small>
            <button type="submit">Upload</button>
        </form>
    </div>
</body>
</html>
