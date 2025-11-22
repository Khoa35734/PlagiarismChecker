<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login - Plagiarism Checker</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
            position: relative;
            overflow: hidden;
        }

        /* Animated background circles */
        body::before,
        body::after {
            content: '';
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.1);
            animation: float 20s infinite ease-in-out;
        }

        body::before {
            width: 400px;
            height: 400px;
            top: -200px;
            left: -200px;
        }

        body::after {
            width: 500px;
            height: 500px;
            bottom: -250px;
            right: -250px;
            animation-delay: -10s;
        }

        @keyframes float {
            0%, 100% {
                transform: translateY(0) translateX(0);
            }
            50% {
                transform: translateY(-50px) translateX(50px);
            }
        }

        .back-link {
            position: absolute;
            top: 30px;
            left: 30px;
            text-decoration: none;
            color: white;
            font-weight: 600;
            padding: 12px 24px;
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            transition: all 0.3s ease;
            z-index: 10;
        }

        .back-link:hover {
            background: rgba(255, 255, 255, 0.3);
            transform: translateX(-5px);
        }

        .login-container {
            background: rgba(255, 255, 255, 0.95);
            padding: 50px;
            border-radius: 25px;
            box-shadow: 0 20px 80px rgba(0, 0, 0, 0.3);
            max-width: 450px;
            width: 100%;
            position: relative;
            z-index: 1;
            animation: slideUp 0.5s ease;
        }

        @keyframes slideUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        .login-header {
            text-align: center;
            margin-bottom: 40px;
        }

        .login-icon {
            font-size: 64px;
            margin-bottom: 20px;
            display: inline-block;
            animation: bounce 2s infinite;
        }

        @keyframes bounce {
            0%, 100% {
                transform: translateY(0);
            }
            50% {
                transform: translateY(-10px);
            }
        }

        .login-header h1 {
            color: #333;
            font-size: 32px;
            font-weight: 700;
            margin-bottom: 10px;
        }

        .login-header p {
            color: #666;
            font-size: 15px;
        }

        .message {
            padding: 15px 20px;
            border-radius: 12px;
            margin-bottom: 25px;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 12px;
            animation: slideIn 0.3s ease;
        }

        @keyframes slideIn {
            from {
                opacity: 0;
                transform: translateX(-20px);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        .message.error {
            background: #fee;
            color: #c33;
            border-left: 4px solid #c33;
        }

        .message.success {
            background: #efe;
            color: #3c3;
            border-left: 4px solid #3c3;
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

        .input-wrapper {
            position: relative;
        }

        .input-icon {
            position: absolute;
            left: 18px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 20px;
            color: #999;
        }

        .form-group input {
            width: 100%;
            padding: 16px 16px 16px 50px;
            border: 2px solid #e0e0e0;
            border-radius: 12px;
            font-size: 15px;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
        }

        .form-group input::placeholder {
            color: #bbb;
        }

        .password-toggle {
            position: absolute;
            right: 18px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            font-size: 20px;
            color: #999;
            user-select: none;
        }

        .password-toggle:hover {
            color: #667eea;
        }

        .remember-me {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 25px;
        }

        .remember-me input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .remember-me label {
            color: #666;
            font-size: 14px;
            cursor: pointer;
            user-select: none;
        }

        .submit-btn {
            width: 100%;
            padding: 18px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 12px;
            font-size: 17px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }

        .submit-btn::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
            transition: left 0.5s;
        }

        .submit-btn:hover::before {
            left: 100%;
        }

        .submit-btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 30px rgba(102, 126, 234, 0.4);
        }

        .submit-btn:active {
            transform: translateY(0);
        }

        .divider {
            text-align: center;
            margin: 30px 0;
            position: relative;
        }

        .divider::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 0;
            right: 0;
            height: 1px;
            background: #e0e0e0;
        }

        .divider span {
            background: rgba(255, 255, 255, 0.95);
            padding: 0 15px;
            color: #999;
            font-size: 14px;
            position: relative;
        }

        .info-text {
            text-align: center;
            color: #666;
            font-size: 14px;
            margin-top: 25px;
        }

        .info-text a {
            color: #667eea;
            text-decoration: none;
            font-weight: 600;
        }

        .info-text a:hover {
            text-decoration: underline;
        }

        .security-badge {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            margin-top: 25px;
            padding: 12px;
            background: #f0f4ff;
            border-radius: 10px;
            font-size: 13px;
            color: #667eea;
        }

        @media (max-width: 500px) {
            .login-container {
                padding: 30px 25px;
            }

            .back-link {
                top: 20px;
                left: 20px;
                padding: 10px 20px;
                font-size: 14px;
            }

            .login-header h1 {
                font-size: 26px;
            }

            .login-icon {
                font-size: 48px;
            }
        }
    </style>
</head>
<body>
    <a href="${pageContext.request.contextPath}/upload" class="back-link">
        ← Back to Home
    </a>

    <div class="login-container">
        <div class="login-header">
            <div class="login-icon">🔐</div>
            <h1>Admin Login</h1>
            <p>Access the administrative dashboard</p>
        </div>

        <c:if test="${not empty param.error}">
            <div class="message error">
                <span style="font-size: 20px;">⚠️</span>
                <span>Invalid username or password. Please try again.</span>
            </div>
        </c:if>

        <c:if test="${not empty param.logout}">
            <div class="message success">
                <span style="font-size: 20px;">✓</span>
                <span>You have been successfully logged out.</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
            <div class="form-group">
                <label for="username">Username</label>
                <div class="input-wrapper">
                    <span class="input-icon">👤</span>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        placeholder="Enter your username"
                        required
                        autofocus
                    >
                </div>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <div class="input-wrapper">
                    <span class="input-icon">🔒</span>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        placeholder="Enter your password"
                        required
                    >
                    <span class="password-toggle" onclick="togglePassword()">👁️</span>
                </div>
            </div>

            <div class="remember-me">
                <input type="checkbox" id="remember" name="remember">
                <label for="remember">Remember me for 30 days</label>
            </div>

            <button type="submit" class="submit-btn">
                🚀 Sign In to Dashboard
            </button>
        </form>

        <div class="divider">
            <span>Secure Login</span>
        </div>

        <div class="info-text">
            Not an admin? <a href="${pageContext.request.contextPath}/upload">Check plagiarism without login</a>
        </div>

        <div class="security-badge">
            <span>🛡️</span>
            <span>Secured with SSL encryption</span>
        </div>
    </div>

    <script>
        function togglePassword() {
            const passwordInput = document.getElementById('password');
            const toggle = document.querySelector('.password-toggle');

            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                toggle.textContent = '🙈';
            } else {
                passwordInput.type = 'password';
                toggle.textContent = '👁️';
            }
        }

        // Form submission animation
        const form = document.getElementById('loginForm');
        form.addEventListener('submit', function(e) {
            const btn = form.querySelector('.submit-btn');
            btn.textContent = '⏳ Signing in...';
            btn.disabled = true;
        });
    </script>
</body>
</html>
