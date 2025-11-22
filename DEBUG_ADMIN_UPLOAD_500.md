# DEBUG GUIDE - Admin Upload 500 Error

## Lỗi hiện tại
```
GET http://localhost:8080/PlagiarismChecker_war_exploded/adminUpload 500 (Internal Server Error)
```

## Các bước debug

### 1. Kiểm tra Session
Truy cập: `http://localhost:8080/PlagiarismChecker_war_exploded/debug/session`

**Kiểm tra:**
- ✅ Session tồn tại?
- ✅ `adminLoggedIn` = `true`?
- ✅ `adminId` có giá trị (không null)?

### 2. Nếu Session không tồn tại hoặc thiếu attributes

**Giải pháp:**
```
1. Logout (nếu đã login): http://localhost:8080/PlagiarismChecker_war_exploded/login?logout=true
2. Login lại: http://localhost:8080/PlagiarismChecker_war_exploded/login
   - Username: admin
   - Password: admin123
3. Kiểm tra session debug lại
4. Thử truy cập adminUpload
```

### 3. Kiểm tra Tomcat Logs

**Vị trí log:**
```
E:\PBL4\apache-tomcat-10.1.48\logs\catalina.out
hoặc
E:\PBL4\apache-tomcat-10.1.48\logs\localhost.{date}.log
```

**Tìm kiếm:**
- `AdminUploadServlet`
- `500`
- `Exception`
- `Error`

### 4. Các lỗi thường gặp

#### A. Session bị null
**Nguyên nhân:** Chưa login hoặc session timeout

**Giải pháp:**
```java
// AdminUploadServlet.doGet đã được fix:
HttpSession session = request.getSession(false);
Integer adminId = (session != null) ? (Integer) session.getAttribute("adminId") : null;
Boolean adminLoggedIn = (session != null) ? (Boolean) session.getAttribute("adminLoggedIn") : null;

if (session == null || adminId == null || adminLoggedIn == null || !adminLoggedIn) {
    response.sendRedirect(request.getContextPath() + "/login");
    return;
}
```

#### B. JSP không tìm thấy
**Nguyên nhân:** Path JSP không đúng

**Kiểm tra:**
```
File tồn tại: E:\PBL4\PlagiarismChecker\src\main\webapp\jsp\adminUpload.jsp
Deploy path: target/PlagiarismChecker-1.0-SNAPSHOT/jsp/adminUpload.jsp
```

**Giải pháp:**
```cmd
cd /d E:\PBL4\PlagiarismChecker
mvnw.cmd clean package
# Copy WAR mới vào Tomcat
```

#### C. Database connection error
**Kiểm tra:**
```sql
mysql -u root -p
USE plagiarism_checker;
SELECT * FROM Users WHERE role = 'ADMIN';
```

**Expected output:**
```
+----+----------+-----------+-------+
| id | username | password  | role  |
+----+----------+-----------+-------+
|  1 | admin    | admin123  | ADMIN |
+----+----------+-----------+-------+
```

#### D. MultipartConfig error
**Nguyên nhân:** Request size vượt quá giới hạn

**Config hiện tại:**
```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB
    maxFileSize = 1024 * 1024 * 10,     // 10 MB per file
    maxRequestSize = 1024 * 1024 * 25   // 25 MB total
)
```

### 5. Quick Fix Commands

```cmd
# 1. Rebuild project
cd /d E:\PBL4\PlagiarismChecker
mvnw.cmd clean package

# 2. Stop Tomcat
# (Ctrl+C in Tomcat window or use Tomcat Manager)

# 3. Clear work directory
rmdir /s /q E:\PBL4\apache-tomcat-10.1.48\work\Catalina\localhost\PlagiarismChecker_war_exploded

# 4. Delete old deployment
rmdir /s /q E:\PBL4\apache-tomcat-10.1.48\webapps\PlagiarismChecker_war_exploded

# 5. Copy new WAR
copy target\PlagiarismChecker-1.0-SNAPSHOT.war E:\PBL4\apache-tomcat-10.1.48\webapps\

# 6. Start Tomcat
E:\PBL4\apache-tomcat-10.1.48\bin\startup.bat
```

### 6. Test Flow

```
Step 1: Login
--------
URL: http://localhost:8080/PlagiarismChecker_war_exploded/login
Username: admin
Password: admin123
Expected: Redirect to /admin or /admin/dashboard

Step 2: Check Session
--------
URL: http://localhost:8080/PlagiarismChecker_war_exploded/debug/session
Expected: 
- ✅ adminLoggedIn = true
- ✅ adminId = 1 (or some number)

Step 3: Access Dashboard
--------
URL: http://localhost:8080/PlagiarismChecker_war_exploded/admin/dashboard
Expected: Show list of documents (or empty state)

Step 4: Access Upload
--------
URL: http://localhost:8080/PlagiarismChecker_war_exploded/adminUpload
Expected: Show upload form
Status: Should be 200 OK (not 500)
```

### 7. Code Changes Summary

**File: AdminUploadServlet.java**
```java
// OLD (có thể gây lỗi 500)
@Override
protected void doGet(...) {
    HttpSession session = request.getSession(false);
    if (session == null || session.getAttribute("adminLoggedIn") == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
}

// NEW (robust error handling)
@Override
protected void doGet(...) {
    HttpSession session = request.getSession(false);
    Integer adminId = (session != null) ? (Integer) session.getAttribute("adminId") : null;
    Boolean adminLoggedIn = (session != null) ? (Boolean) session.getAttribute("adminLoggedIn") : null;
    
    if (session == null || adminId == null || adminLoggedIn == null || !adminLoggedIn) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
    
    request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
}
```

### 8. Expected Browser Console Output

**Success:**
```
GET http://localhost:8080/PlagiarismChecker_war_exploded/adminUpload 200 OK
```

**If not logged in:**
```
GET http://localhost:8080/PlagiarismChecker_war_exploded/adminUpload 302 Found
Location: /PlagiarismChecker_war_exploded/login
```

### 9. Rollback Plan

If still 500 error after fixes:

```java
// Temporary: Remove session check from doGet
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    // Bypass authentication for debugging
    request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
}
```

This will help identify if the issue is:
- Session handling → Fix login flow
- JSP rendering → Check JSP syntax
- Other → Check Tomcat logs

---

## 🔍 Current Status

- ✅ AdminUploadServlet.doGet() fixed
- ✅ Session validation improved
- ✅ Build successful
- ⏳ Need to test in browser
- ⏳ Need to check session debug page

---

## 📞 Next Actions

1. **Rebuild & Redeploy:**
   ```cmd
   mvnw.cmd clean package
   # Deploy to Tomcat
   ```

2. **Test Login:**
   - Go to /login
   - Login as admin/admin123
   - Check session debug: /debug/session

3. **Test Upload:**
   - Go to /adminUpload
   - Should show upload form (not 500)

4. **If still 500:**
   - Check Tomcat logs
   - Share error stack trace
   - Use session debug page to verify session state

---

**Last Updated:** 2025-11-22 15:55
**Build Status:** ✅ SUCCESS
**Fix Applied:** ✅ YES
**Testing Required:** ⏳ PENDING

