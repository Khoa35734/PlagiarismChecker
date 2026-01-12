<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Upload - Reference Documents</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            max-width: 700px;
            width: 100%;
            background: white;
            padding: 40px;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.15);
        }

        .header {
            text-align: center;
            margin-bottom: 30px;
        }

        .header h1 {
            color: #333;
            font-size: 28px;
            margin-bottom: 10px;
        }

        .header p {
            color: #666;
            font-size: 14px;
        }

        .info-box {
            background: #e7f3ff;
            border-left: 4px solid #667eea;
            padding: 20px;
            margin-bottom: 30px;
            border-radius: 8px;
        }

        .info-box h3 {
            color: #667eea;
            margin-bottom: 10px;
            font-size: 16px;
        }

        .info-box ul {
            margin-left: 20px;
            color: #555;
            line-height: 1.8;
        }

        .message {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
            font-weight: 500;
        }

        .message.success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .message.error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }

        .upload-form {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 15px;
            margin-bottom: 20px;
        }

        .form-group {
            margin-bottom: 25px;
        }

        .form-group label {
            display: block;
            color: #333;
            font-weight: 600;
            margin-bottom: 10px;
            font-size: 15px;
        }

        .file-input-wrapper {
            position: relative;
            overflow: hidden;
            display: inline-block;
            width: 100%;
        }

        .file-input-wrapper input[type=file] {
            position: absolute;
            left: -9999px;
        }

        .file-input-label {
            display: block;
            padding: 20px;
            background: white;
            border: 2px dashed #667eea;
            border-radius: 10px;
            text-align: center;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .file-input-label:hover {
            background: #f0f4ff;
            border-color: #764ba2;
        }

        .file-input-label .icon {
            font-size: 48px;
            margin-bottom: 10px;
        }

        .file-input-label .text {
            color: #667eea;
            font-weight: 600;
            font-size: 16px;
        }

        .file-input-label .subtext {
            color: #999;
            font-size: 13px;
            margin-top: 8px;
        }

        .selected-files {
            margin-top: 15px;
            padding: 15px;
            background: white;
            border-radius: 8px;
            border: 1px solid #e0e0e0;
            display: none;
        }

        .selected-files.active {
            display: block;
        }

        .selected-files h4 {
            color: #333;
            margin-bottom: 10px;
            font-size: 14px;
        }

        .file-list {
            list-style: none;
        }

        .file-item {
            padding: 12px;
            border-bottom: 1px solid #f0f0f0;
            color: #555;
            font-size: 14px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: background-color 0.2s ease;
        }

        .file-item:hover {
            background-color: #f8f9fa;
        }

        .file-item:last-child {
            border-bottom: none;
        }

        .file-item button {
            transition: all 0.2s ease;
        }

        .file-item button:hover {
            background-color: #c82333 !important;
            transform: scale(1.1);
        }

        .helper-text {
            color: #999;
            font-size: 13px;
            margin-top: 10px;
            line-height: 1.6;
        }

        .helper-text strong {
            color: #667eea;
        }

        .btn-group {
            display: flex;
            gap: 15px;
        }

        .btn {
            flex: 1;
            padding: 15px 30px;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            text-align: center;
            text-decoration: none;
            display: inline-block;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
        }

        .btn-primary:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #5a6268;
        }

        .progress-wrapper {
            margin-top: 20px;
            display: none;
        }

        .progress-label {
            font-weight: 600;
            color: #333;
            margin-bottom: 8px;
        }

        .progress-track {
            width: 100%;
            height: 18px;
            background: #e0e7ff;
            border-radius: 9px;
            overflow: hidden;
        }

        .progress-bar {
            height: 100%;
            width: 0;
            background: linear-gradient(135deg, #42e695 0%, #3bb2b8 100%);
            transition: width 0.2s ease;
        }

        .progress-status {
            margin-top: 6px;
            font-size: 13px;
            color: #555;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📤 Upload Reference Documents</h1>
            <p>Build your plagiarism detection reference corpus</p>
        </div>

        <div id="message-container">
            <c:if test="${not empty message}">
                <div class="message success">
                    ✓ ${message}
                </div>
            </c:if>

            <c:if test="${not empty error}">
                <div class="message error">
                    ⚠ ${error}
                </div>
            </c:if>
        </div>

        <div class="info-box">
            <h3>ℹ️ How It Works</h3>
            <ul>
                <li>Upload reference documents (PDF, DOCX, TXT) to build your corpus</li>
                <li>When users check for plagiarism, their content is compared against these documents</li>
                <li>Maximum batch size: <strong>
                    <c:choose>
                        <c:when test="${sessionScope.adminLoggedIn}">No limit (admin)</c:when>
                        <c:otherwise>25 MB</c:otherwise>
                    </c:choose>
                </strong></li>
                <li>All uploaded documents are stored in your private repository</li>
            </ul>
        </div>

        <form action="${pageContext.request.contextPath}/adminUpload" method="post" enctype="multipart/form-data" class="upload-form" id="adminUploadForm">
            <div class="form-group">
                <label>Select Files to Upload</label>
                <div class="file-input-wrapper">
                    <input type="file" id="file" name="file" accept=".pdf,.docx,.txt" multiple required onchange="updateFileList(this)">
                    <label for="file" class="file-input-label">
                        <div class="icon">📁</div>
                        <div class="text">Click to browse or drag files here</div>
                        <div class="subtext">PDF, DOCX, TXT files only</div>
                    </label>
                </div>
                <div id="selectedFiles" class="selected-files">
                    <h4>Selected Files:</h4>
                    <ul id="fileList" class="file-list"></ul>
                    <div class="helper-text" id="sizeInfo"></div>
                </div>
                <div class="helper-text">
                    <strong>Note:</strong>
                    <c:choose>
                        <c:when test="${sessionScope.adminLoggedIn}">
                            Admins have no client-side batch size limit here; server/container limits may still apply.
                        </c:when>
                        <c:otherwise>
                            Total size of all selected files must not exceed 25 MB.
                        </c:otherwise>
                    </c:choose>
                    Supported formats: PDF, DOCX, TXT
                </div>

                <div class="progress-wrapper" id="uploadProgress">
                    <div class="progress-label">Uploading...</div>
                    <div class="progress-track">
                        <div class="progress-bar" id="progressBar"></div>
                    </div>
                    <div class="progress-status" id="progressText">0%</div>
                </div>
            </div>

            <div class="btn-group">
                <button type="submit" class="btn btn-primary" id="uploadBtn" disabled>
                    📤 Upload Documents
                </button>
                <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">
                    ← Back to Dashboard
                </a>
            </div>
        </form>
    </div>

    <script>
        let selectedFilesArray = [];

        function updateFileList(input) {
            const fileList = document.getElementById('fileList');
            const selectedFiles = document.getElementById('selectedFiles');
            const sizeInfo = document.getElementById('sizeInfo');
            const uploadBtn = document.getElementById('uploadBtn');

            selectedFilesArray = Array.from(input.files);
            fileList.innerHTML = '';
            let totalSize = 0;
            // server-provided max size in bytes; -1 = no client-side limit (admin)
            const maxSize = <c:choose><c:when test="${sessionScope.adminLoggedIn}">-1</c:when><c:otherwise>26214400</c:otherwise></c:choose>;

            if (selectedFilesArray.length > 0) {
                selectedFiles.classList.add('active');

                selectedFilesArray.forEach((file, index) => {
                    totalSize += file.size;

                    const li = document.createElement('li');
                    li.className = 'file-item';
                    li.style.display = 'flex';
                    li.style.justifyContent = 'space-between';
                    li.style.alignItems = 'center';

                    const fileInfo = document.createElement('span');
                    // Avoid JSP EL parsing of dollar-curly patterns inside JS template literals by using string concatenation
                    fileInfo.textContent = file.name + ' (' + (file.size / 1024).toFixed(2) + ' KB)';

                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.textContent = '✕';
                    removeBtn.style.cssText = 'background: #dc3545; color: white; border: none; border-radius: 4px; padding: 4px 8px; cursor: pointer; font-size: 12px;';
                    removeBtn.onclick = () => removeFile(index);

                    li.appendChild(fileInfo);
                    li.appendChild(removeBtn);
                    fileList.appendChild(li);
                });

                const totalMB = (totalSize / 1024 / 1024).toFixed(2);
                // Display max size or 'No limit' when admin
                const maxDisplay = (maxSize === -1) ? 'No limit' : ((maxSize / 1024 / 1024).toFixed(2) + ' MB');
                sizeInfo.innerHTML = '<strong>Total Size:</strong> ' + totalMB + ' MB / ' + maxDisplay;

                if (maxSize !== -1 && totalSize > maxSize) {
                    sizeInfo.style.color = '#dc3545';
                    sizeInfo.innerHTML += '<br><strong>⚠ Error:</strong> Total size exceeds ' + maxDisplay + ' limit!';
                    uploadBtn.disabled = true;
                } else {
                    sizeInfo.style.color = '#28a745';
                    uploadBtn.disabled = false;
                }
            } else {
                selectedFiles.classList.remove('active');
                uploadBtn.disabled = true;
            }
        }

        function removeFile(index) {
            selectedFilesArray.splice(index, 1);
            const dataTransfer = new DataTransfer();
            selectedFilesArray.forEach(file => dataTransfer.items.add(file));
            document.getElementById('file').files = dataTransfer.files;
            updateFileList(document.getElementById('file'));
        }

        const form = document.getElementById('adminUploadForm');
        const progressWrapper = document.getElementById('uploadProgress');
        const progressBar = document.getElementById('progressBar');
        const progressText = document.getElementById('progressText');
        const uploadBtn = document.getElementById('uploadBtn');
        const messageContainer = document.getElementById('message-container');

        form.addEventListener('submit', function (event) {
            if (!form.checkValidity() || uploadBtn.disabled) {
                return;
            }
            event.preventDefault();

            if (selectedFilesArray.length === 0) {
                displayMessage('Please select at least one file to upload.', 'error');
                return;
            }

            messageContainer.innerHTML = '';
            progressWrapper.style.display = 'block';
            progressBar.style.width = '0%';
            progressText.textContent = 'Preparing to upload ' + selectedFilesArray.length + ' file(s)...';
            uploadBtn.disabled = true;

            uploadFilesSequentially(0);
        });

        function uploadFilesSequentially(index) {
            if (index >= selectedFilesArray.length) {
                // All files uploaded successfully
                progressBar.style.backgroundColor = '#28a745';
                progressText.textContent = 'All files uploaded successfully!';
                displayMessage('Successfully uploaded ' + selectedFilesArray.length + ' file(s).', 'success');

                setTimeout(() => {
                    form.reset();
                    selectedFilesArray = [];
                    updateFileList(document.getElementById('file'));
                    progressWrapper.style.display = 'none';
                    uploadBtn.disabled = true;

                    // Redirect to dashboard to see the uploaded files
                    window.location.href = '${pageContext.request.contextPath}/admin/dashboard';
                }, 1500);
                return;
            }

            const file = selectedFilesArray[index];
            const formData = new FormData();
            formData.append('file', file);

            const xhr = new XMLHttpRequest();

            // Avoid JSP EL parsing by using concatenation
            progressText.textContent = 'Uploading file ' + (index + 1) + ' of ' + selectedFilesArray.length + ': ' + file.name;
            progressBar.style.width = '0%';

            xhr.open('POST', form.action, true);

            xhr.upload.addEventListener('progress', function (e) {
                if (e.lengthComputable) {
                    const percent = Math.round((e.loaded / e.total) * 100);
                    const overallProgress = ((index + (e.loaded / e.total)) / selectedFilesArray.length) * 100;
                    progressBar.style.width = percent + '%';
                    // Build string with concatenation to prevent JSP EL parsing of dollar-curly patterns
                    progressText.textContent = 'Uploading file ' + (index + 1) + '/' + selectedFilesArray.length + ': ' + file.name + ' - ' + percent + '% (Overall: ' + overallProgress.toFixed(0) + '%)';
                }
            });

            xhr.onreadystatechange = function () {
                if (xhr.readyState === XMLHttpRequest.DONE) {
                    if (xhr.status >= 200 && xhr.status < 300) {
                        // Success, upload next file
                        uploadFilesSequentially(index + 1);
                    } else {
                        // Error occurred
                        uploadBtn.disabled = false;
                        progressBar.style.backgroundColor = '#dc3545';
                        progressText.textContent = 'Upload failed for: ' + file.name;

                        try {
                            const response = xhr.responseText;
                            displayMessage('Failed to upload ' + file.name + ': ' + (response || 'Unknown error'), 'error');
                        } catch (e) {
                            displayMessage('Failed to upload ' + file.name + ': Server error', 'error');
                        }
                    }
                }
            };

            xhr.onerror = function () {
                uploadBtn.disabled = false;
                progressBar.style.backgroundColor = '#dc3545';
                progressText.textContent = 'Network error for: ' + file.name;
                displayMessage('Network error while uploading ' + file.name, 'error');
            };

            xhr.send(formData);
        }

        function displayMessage(msg, type) {
            const messageDiv = document.createElement('div');
            // Use concatenation to prevent JSP EL from trying to evaluate ${type}
            messageDiv.className = 'message ' + type;
            messageDiv.textContent = (type === 'success' ? '✓ ' : '⚠ ') + msg;
            messageContainer.innerHTML = '';
            messageContainer.appendChild(messageDiv);
        }
    </script>
</body>
</html>
