# MVC Architecture - Plagiarism Checker System

## 📋 Tổng quan hệ thống

Hệ thống kiểm tra đạo văn được xây dựng theo mô hình **MVC (Model-View-Controller)** sử dụng **JSP/Servlet** với **Jakarta EE** và **MySQL Database**.

---

## 🏗️ Sơ đồ kiến trúc MVC

```
┌─────────────────────────────────────────────────────────────────────┐
│                          CLIENT BROWSER                              │
│                     (HTTP Request/Response)                          │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         WEB CONTAINER                                │
│                        (Apache Tomcat 10)                            │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
        ▼                      ▼                      ▼
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│   CONTROLLER     │◄─►│     MODEL        │◄─►│      VIEW        │
├──────────────────┤   ├──────────────────┤   ├──────────────────┤
│ UploadServlet    │   │ DatabaseUtils    │   │ upload.jsp       │
│ ResultServlet    │   │ Submission       │   │ result.jsp       │
│ QueueWorker      │   │ Result           │   │ adminLogin.jsp   │
│ AdminLogin       │   │ User             │   │ adminDashboard   │
│ AdminDashboard   │   │                  │   │ adminUpload.jsp  │
│ AdminUpload      │   │                  │   │ viewSubmission   │
│ AdminView        │   │                  │   │                  │
│ AdminDownload    │   │                  │   │                  │
│ AdminDelete      │   │                  │   │                  │
│ LoginServlet     │   │                  │   │                  │
│ AppConfig        │   │                  │   │                  │
└────────┬─────────┘   └──────────────────┘   └──────────────────┘
         │                      │                      
         │                      │                      
         ▼                      ▼                      
┌──────────────────┐   ┌──────────────────┐              
│     UTILS        │   │    DATABASE      │              
├──────────────────┤   ├──────────────────┤              
│ FileParser       │   │ MySQL 8.0        │              
│ TextCleaner      │   │ plagiarism_      │              
│ TextSimilarity   │   │   checker DB     │              
│                  │   │                  │              
│ - Winnowing      │   │ Tables:          │              
│ - TF-IDF         │   │ - users          │              
│ - Citation       │   │ - documents      │              
│   Detection      │   │ - submissions    │              
│                  │   │ - results        │              
└──────────────────┘   └──────────────────┘              
```

## 📋 Tổng hợp các Class Java trong hệ thống

### **Bảng mapping đầy đủ các Class**

| Layer | Package | Class Name | Type | Description |
|-------|---------|------------|------|-------------|
| **MODEL** | `model` | `DatabaseUtils.java` | Utility | Database connection & CRUD operations |
| **MODEL** | `model` | `Submission.java` | Entity | Submission entity (submissions table) |
| **MODEL** | `model` | `Result.java` | Entity | Result entity (results table) |
| **MODEL** | `model` | `User.java` | Entity | User entity (users table) |
| **CONTROLLER** | `controller` | `UploadServlet.java` | Servlet | Handle guest file upload |
| **CONTROLLER** | `controller` | `ResultServlet.java` | Servlet | Display plagiarism result |
| **CONTROLLER** | `controller` | `QueueWorker.java` | Runnable | Background plagiarism processing |
| **CONTROLLER** | `controller` | `AdminLoginServlet.java` | Servlet | Admin authentication |
| **CONTROLLER** | `controller` | `AdminDashboardServlet.java` | Servlet | Admin dashboard display |
| **CONTROLLER** | `controller` | `AdminUploadServlet.java` | Servlet | Admin file upload |
| **CONTROLLER** | `controller` | `AdminViewServlet.java` | Servlet | View document details |
| **CONTROLLER** | `controller` | `AdminDownloadServlet.java` | Servlet | Download document |
| **CONTROLLER** | `controller` | `AdminDeleteServlet.java` | Servlet | Delete document |
| **CONTROLLER** | `controller` | `LoginServlet.java` | Servlet | Legacy login handler |
| **CONTROLLER** | `controller` | `SessionDebugServlet.java` | Servlet | Debug session info |
| **CONTROLLER** | `controller` | `AppConfig.java` | Listener | App initialization config |
| **UTILS** | `utils` | `FileParser.java` | Utility | Extract text from PDF/DOCX/TXT |
| **UTILS** | `utils` | `TextCleaner.java` | Utility | Clean and normalize text |
| **UTILS** | `utils` | `TextSimilarity.java` | Utility | Winnowing + TF-IDF algorithms |
| **VIEW** | - | `upload.jsp` | JSP | Guest upload interface |
| **VIEW** | - | `result.jsp` | JSP | Result display with chart |
| **VIEW** | - | `adminLogin.jsp` | JSP | Admin login form |
| **VIEW** | - | `adminDashboard.jsp` | JSP | Admin dashboard |
| **VIEW** | - | `adminUpload.jsp` | JSP | Admin upload interface |
| **VIEW** | - | `viewSubmission.jsp` | JSP | Document detail view |

---

## 🔗 Relationship Map (Class Dependencies)

```
┌─────────────────────────────────────────────────────────────────┐
│                         RELATIONSHIP MAP                         │
└─────────────────────────────────────────────────────────────────┘

📄 UploadServlet.java
   ├──→ FileParser.extractText()          [Parse uploaded files]
   ├──→ TextCleaner.clean()               [Clean extracted text]
   ├──→ DatabaseUtils.saveSubmission()    [Save to DB]
   ├──→ QueueWorker.startWorker()         [Trigger background process]
   └──→ Submission (entity)               [Create submission object]

📄 ResultServlet.java
   ├──→ DatabaseUtils.getSubmissionByToken()  [Query submission]
   ├──→ DatabaseUtils.getResultBySubmissionId()  [Query result]
   ├──→ Result (entity)                   [Process result object]
   └──→ result.jsp                        [Forward to view]

📄 QueueWorker.java (Background Thread)
   ├──→ DatabaseUtils.getSubmissionById()      [Get submission]
   ├──→ DatabaseUtils.getAllDocumentContents() [Get corpus]
   ├──→ TextCleaner.clean()               [Clean text]
   ├──→ TextSimilarity.calculateWinnowing()  [Winnowing algorithm]
   ├──→ TextSimilarity.calculateTFIDF()   [TF-IDF algorithm]
   ├──→ TextSimilarity.findMatchedSegments()  [Find matches]
   ├──→ TextSimilarity.hasCitation()      [Check citations]
   ├──→ DatabaseUtils.saveResult()        [Save result]
   └──→ Result (entity)                   [Create result object]

📄 AdminLoginServlet.java
   ├──→ DatabaseUtils.authenticateUser()  [Verify credentials]
   ├──→ User (entity)                     [User object]
   └──→ HttpSession                       [Create session]

📄 AdminDashboardServlet.java
   ├──→ DatabaseUtils.getAllDocuments()   [Query all documents]
   └──→ adminDashboard.jsp                [Forward to view]

📄 AdminUploadServlet.java
   ├──→ FileParser.extractText()          [Parse files]
   ├──→ TextCleaner.clean()               [Clean text]
   └──→ DatabaseUtils.saveDocument()      [Save to DB]

📄 AdminViewServlet.java
   ├──→ DatabaseUtils.getDocumentById()   [Query document]
   └──→ viewSubmission.jsp                [Forward to view]

📄 AdminDownloadServlet.java
   └──→ DatabaseUtils.getDocumentById()   [Query document]

📄 AdminDeleteServlet.java
   └──→ DatabaseUtils.deleteDocument()    [Delete from DB]

📄 FileParser.java
   ├──→ Apache PDFBox (PDDocument, Loader)
   └──→ Apache POI (XWPFDocument)

📄 TextCleaner.java
   └──→ Java String utilities

📄 TextSimilarity.java
   └──→ Math algorithms (rolling hash, cosine similarity)

📄 DatabaseUtils.java
   ├──→ MySQL JDBC Connector
   ├──→ Submission (entity)
   ├──→ Result (entity)
   └──→ User (entity)
```

---

## 🔍 Chi tiết các file trong từng khối MVC

### 📦 **CONTROLLER Layer** (package: controller)
```
controller/
├── UploadServlet.java          → Xử lý upload file từ guest user
├── ResultServlet.java          → Hiển thị kết quả kiểm tra đạo văn
├── QueueWorker.java            → Background worker xử lý plagiarism
├── AdminLoginServlet.java      → Đăng nhập admin
├── AdminDashboardServlet.java  → Dashboard quản lý tài liệu
├── AdminUploadServlet.java     → Upload tài liệu admin
├── AdminViewServlet.java       → Xem chi tiết tài liệu
├── AdminDownloadServlet.java   → Download tài liệu
├── AdminDeleteServlet.java     → Xóa tài liệu
├── LoginServlet.java           → Xử lý đăng nhập (legacy)
├── SessionDebugServlet.java    → Debug session (dev only)
└── AppConfig.java              → Cấu hình database connection
```

### 📊 **MODEL Layer** (package: model)
```
model/
├── DatabaseUtils.java          → CRUD operations, connection pool
├── Submission.java             → Entity class cho submissions table
├── Result.java                 → Entity class cho results table
└── User.java                   → Entity class cho users table
```

### 🛠️ **UTILS Layer** (package: utils)
```
utils/
├── FileParser.java             → Parse PDF/DOCX/TXT files
├── TextCleaner.java            → Chuẩn hóa và làm sạch văn bản
└── TextSimilarity.java         → Thuật toán Winnowing, TF-IDF
```

### 🎨 **VIEW Layer** (JSP files)
```
webapp/jsp/
├── upload.jsp                  → Trang upload cho guest user
├── result.jsp                  → Hiển thị kết quả đạo văn
├── adminLogin.jsp              → Form đăng nhập admin
├── adminDashboard.jsp          → Dashboard CRUD documents
├── adminUpload.jsp             → Upload documents admin
├── viewSubmission.jsp          → Xem chi tiết submission
├── login.jsp                   → Login page (legacy)
└── sessionDebug.jsp            → Debug session (dev only)
```

---

## 📦 Cấu trúc thư mục dự án

```
PlagiarismChecker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── controller/          # CONTROLLER LAYER
│   │   │   │   ├── UploadServlet.java
│   │   │   │   ├── ResultServlet.java
│   │   │   │   ├── QueueWorker.java
│   │   │   │   ├── AdminLoginServlet.java
│   │   │   │   ├── AdminDashboardServlet.java
│   │   │   │   ├── AdminUploadServlet.java
│   │   │   │   ├── AdminViewServlet.java
│   │   │   │   ├── AdminDownloadServlet.java
│   │   │   │   ├── AdminDeleteServlet.java
│   │   │   │   ├── LoginServlet.java
│   │   │   │   └── AppConfig.java
│   │   │   │
│   │   │   ├── model/               # MODEL LAYER
│   │   │   │   ├── DatabaseUtils.java
│   │   │   │   ├── Submission.java
│   │   │   │   ├── Result.java
│   │   │   │   └── User.java
│   │   │   │
│   │   │   └── utils/               # UTILITY LAYER
│   │   │       ├── FileParser.java
│   │   │       ├── TextCleaner.java
│   │   │       └── TextSimilarity.java
│   │   │
│   │   └── webapp/                  # VIEW LAYER
│   │       ├── WEB-INF/
│   │       │   └── web.xml
│   │       ├── jsp/
│   │       │   ├── upload.jsp
│   │       │   ├── result.jsp
│   │       │   ├── adminLogin.jsp
│   │       │   ├── adminDashboard.jsp
│   │       │   ├── adminUpload.jsp
│   │       │   └── viewSubmission.jsp
│   │       └── css/
│   │           └── style.css
│   └── test/
├── database/
│   └── schema.sql                   # DATABASE SCHEMA
└── pom.xml                          # MAVEN DEPENDENCIES
```

---

## 🎯 Chi tiết các layer trong MVC

### 1️⃣ **MODEL Layer** (Mô hình dữ liệu)

#### 📄 **model/DatabaseUtils.java**
**Package**: `model`  
**Chức năng**: Quản lý kết nối database, cung cấp phương thức truy vấn

**Constants**:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/plagiarism_checker"
private static final String DB_USER = "root"
private static final String DB_PASSWORD = "khoakhoa04"
```

**Methods**:
```java
// Connection Management
+ public static Connection getConnection() throws SQLException
+ private static void initializeConnectionPool()

// Submission Operations
+ public static int saveSubmission(Submission submission) throws SQLException
+ public static Submission getSubmissionById(int id) throws SQLException
+ public static Submission getSubmissionByToken(String token) throws SQLException
+ public static List<Submission> getAllSubmissions() throws SQLException

// Result Operations
+ public static void saveResult(Result result) throws SQLException
+ public static Result getResultBySubmissionId(int submissionId) throws SQLException
+ public static List<Result> getAllResults() throws SQLException

// Document Operations (Admin)
+ public static int saveDocument(String filename, String content, long fileSize, int userId) throws SQLException
+ public static List<Map<String, Object>> getAllDocuments() throws SQLException
+ public static Map<String, Object> getDocumentById(int id) throws SQLException
+ public static void deleteDocument(int id) throws SQLException
+ public static List<String> getAllDocumentContents() throws SQLException

// User Operations
+ public static User authenticateUser(String username, String password) throws SQLException
+ public static User getUserById(int id) throws SQLException
```

---

#### 📄 **model/Submission.java**
**Package**: `model`  
**Entity**: Đại diện cho một lần nộp tài liệu (bảng `submissions`)

**Properties**:
```java
private int id;                      // PRIMARY KEY
private String filename;             // Tên file upload
private String content;              // Nội dung text đã parse
private Timestamp uploadedAt;        // Thời gian upload
private String guestToken;           // Token cho guest user (UUID)
private Integer userId;              // Foreign key → users.id (nullable)
```

**Methods**:
```java
// Constructors
+ public Submission()
+ public Submission(String filename, String content, String guestToken)
+ public Submission(String filename, String content, Integer userId)

// Getters & Setters
+ public int getId()
+ public void setId(int id)
+ public String getFilename()
+ public void setFilename(String filename)
+ public String getContent()
+ public void setContent(String content)
+ public Timestamp getUploadedAt()
+ public void setUploadedAt(Timestamp uploadedAt)
+ public String getGuestToken()
+ public void setGuestToken(String guestToken)
+ public Integer getUserId()
+ public void setUserId(Integer userId)
```

---

#### 📄 **model/Result.java**
**Package**: `model`  
**Entity**: Đại diện cho kết quả kiểm tra đạo văn (bảng `results`)

**Properties**:
```java
private int id;                          // PRIMARY KEY
private int submissionId;                // Foreign key → submissions.id
private double similarityWinnowing;      // Winnowing similarity (0.0 - 1.0)
private double similarityTfidf;          // TF-IDF similarity (0.0 - 1.0)
private String matchedSegments;          // JSON array của các đoạn trùng
private String status;                   // "Plagiarism Suspected" / "Original"
private Timestamp createdAt;             // Thời gian tạo kết quả
```

**Methods**:
```java
// Constructors
+ public Result()
+ public Result(int submissionId, double winnowing, double tfidf, String segments, String status)

// Getters & Setters
+ public int getId()
+ public void setId(int id)
+ public int getSubmissionId()
+ public void setSubmissionId(int submissionId)
+ public double getSimilarityWinnowing()
+ public void setSimilarityWinnowing(double similarityWinnowing)
+ public double getSimilarityTfidf()
+ public void setSimilarityTfidf(double similarityTfidf)
+ public String getMatchedSegments()
+ public void setMatchedSegments(String matchedSegments)
+ public String getStatus()
+ public void setStatus(String status)
+ public Timestamp getCreatedAt()
+ public void setCreatedAt(Timestamp createdAt)

// Utility Methods
+ public double getOverallSimilarity() // Trung bình Winnowing và TF-IDF
+ public List<MatchedSegment> parseMatchedSegments() // Parse JSON → Object
```

---

#### 📄 **model/User.java**
**Package**: `model`  
**Entity**: Đại diện cho tài khoản admin (bảng `users`)

**Properties**:
```java
private int id;                   // PRIMARY KEY
private String username;          // Unique username
private String password;          // Hashed password (BCrypt)
private String email;             // Email address
private Timestamp createdAt;      // Thời gian tạo tài khoản
```

**Methods**:
```java
// Constructors
+ public User()
+ public User(String username, String password, String email)

// Getters & Setters
+ public int getId()
+ public void setId(int id)
+ public String getUsername()
+ public void setUsername(String username)
+ public String getPassword()
+ public void setPassword(String password)
+ public String getEmail()
+ public void setEmail(String email)
+ public Timestamp getCreatedAt()
+ public void setCreatedAt(Timestamp createdAt)

// Utility Methods
+ public boolean checkPassword(String plainPassword) // Verify BCrypt hash
+ public static String hashPassword(String plainPassword) // Hash password
```

---

### 2️⃣ **VIEW Layer** (Giao diện người dùng - JSP)

#### 🌐 **User Interface (Anonymous Users)**

##### **upload.jsp**
- **Mục đích**: Trang chủ upload file cho guest user
- **Chức năng**:
  - Upload multiple files (PDF, DOCX, TXT)
  - Giới hạn tổng dung lượng: 25MB
  - Hiển thị progress bar khi upload
  - Hiển thị danh sách file đã chọn
  - Nút "Check Plagiarism" để submit

##### **result.jsp**
- **Mục đích**: Hiển thị kết quả kiểm tra đạo văn
- **Hiển thị**:
  - Biểu đồ tròn (Pie Chart): Original %, Partial Match %, Exact Match %
  - Danh sách các đoạn văn bản trùng khớp
  - Source file gốc đã trùng
  - Highlight đoạn text plagiarism
  - Citation status (Properly Cited / Plagiarism Suspected)

#### 🔐 **Admin Interface**

##### **adminLogin.jsp**
- **Mục đích**: Trang đăng nhập cho admin
- **Form**:
  - Username
  - Password
  - Login button

##### **adminDashboard.jsp**
- **Mục đích**: Dashboard quản lý tài liệu
- **Hiển thị**:
  - Thống kê: Total Documents, Total Size, Recent Uploads
  - Bảng danh sách documents (id, filename, upload date, size, actions)
  - Actions: View, Download, Delete
  - Link đến Admin Upload

##### **adminUpload.jsp**
- **Mục đích**: Upload tài liệu vào kho lưu trữ (không giới hạn dung lượng)
- **Chức năng**:
  - Upload multiple files
  - Hiển thị progress bar cho từng file
  - Nút "X" để hủy upload từng file
  - Nút "Upload" để xác nhận

##### **viewSubmission.jsp**
- **Mục đích**: Xem chi tiết submission
- **Hiển thị**:
  - Metadata (filename, upload date, size)
  - Full text content
  - Download button

---

### 3️⃣ **CONTROLLER Layer** (Xử lý logic nghiệp vụ - Servlet)

#### 🔄 **Anonymous User Controllers**

##### **controller/UploadServlet.java**
**Package**: `controller`  
**URL Pattern**: `/upload`  
**Annotations**: `@WebServlet(urlPatterns = {"/upload"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - Redirect to upload page
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    response.sendRedirect(request.getContextPath() + "/jsp/upload.jsp");
}

// POST - Handle file upload
+ protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow**:
1. Parse multipart/form-data sử dụng `request.getParts()`
2. Validate file type (PDF, DOCX, TXT)
3. Validate tổng dung lượng ≤ 25MB
4. Gọi `FileParser.extractText()` để parse nội dung
5. Tạo `guestToken` (UUID)
6. Lưu vào DB qua `DatabaseUtils.saveSubmission()`
7. Trigger `QueueWorker` để xử lý background
8. Redirect: `response.sendRedirect("/result?token=" + guestToken)`

**Key Methods**:
```java
- private boolean isValidFileType(String filename)
- private long calculateTotalSize(Collection<Part> parts)
- private String extractFileExtension(String filename)
```

---

##### **controller/ResultServlet.java**
**Package**: `controller`  
**URL Pattern**: `/result`  
**Annotations**: `@WebServlet(urlPatterns = {"/result"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - Display plagiarism result
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow**:
1. Nhận `token` từ query parameter: `request.getParameter("token")`
2. Query submission: `DatabaseUtils.getSubmissionByToken(token)`
3. Query result: `DatabaseUtils.getResultBySubmissionId(submissionId)`
4. Parse matched segments JSON
5. Calculate chart data (Original %, Partial %, Exact %)
6. Set attributes: `request.setAttribute("result", result)`
7. Forward: `request.getRequestDispatcher("/jsp/result.jsp").forward()`

**Key Methods**:
```java
- private Map<String, Double> calculateChartData(Result result)
- private List<MatchedSegment> parseMatchedSegments(String json)
```

**Attributes Set**:
```java
request.setAttribute("submission", submission);
request.setAttribute("result", result);
request.setAttribute("chartData", chartData);
request.setAttribute("matchedSegments", segments);
```

---

##### **controller/QueueWorker.java**
**Package**: `controller`  
**Type**: Background Worker (Runnable)  
**Implements**: `Runnable`

**Purpose**: Xử lý plagiarism check trong background thread

**Constructor**:
```java
+ public QueueWorker(int submissionId)
```

**Methods**:
```java
// Main processing method
+ public void run() {
    try {
        processSubmission(submissionId);
    } catch (Exception e) {
        logger.error("Error processing submission", e);
    }
}

// Core processing logic
- private void processSubmission(int submissionId) throws SQLException {
    // 1. Get submission
    Submission submission = DatabaseUtils.getSubmissionById(submissionId);
    
    // 2. Clean text
    String cleanedText = TextCleaner.clean(submission.getContent());
    
    // 3. Get all documents from DB
    List<String> corpus = DatabaseUtils.getAllDocumentContents();
    
    // 4. Calculate similarities
    List<MatchedSegment> matches = new ArrayList<>();
    double maxWinnowing = 0.0;
    double maxTfidf = 0.0;
    
    for (String docContent : corpus) {
        double winnowing = TextSimilarity.calculateWinnowing(cleanedText, docContent);
        double tfidf = TextSimilarity.calculateTFIDF(cleanedText, docContent);
        
        if (winnowing > 0.3 || tfidf > 0.3) {
            // Find matched segments
            List<String> segments = TextSimilarity.findMatchedSegments(cleanedText, docContent);
            // Check citations
            // Add to matches
        }
        
        maxWinnowing = Math.max(maxWinnowing, winnowing);
        maxTfidf = Math.max(maxTfidf, tfidf);
    }
    
    // 5. Save result
    String status = (maxWinnowing > 0.6 || maxTfidf > 0.6) ? "Plagiarism Suspected" : "Original";
    Result result = new Result(submissionId, maxWinnowing, maxTfidf, toJson(matches), status);
    DatabaseUtils.saveResult(result);
}

// Start worker thread
+ public static void startWorker(int submissionId) {
    QueueWorker worker = new QueueWorker(submissionId);
    new Thread(worker).start();
}
```

---

#### 🔐 **Admin Controllers**

##### **controller/AdminLoginServlet.java**
**Package**: `controller`  
**URL Pattern**: `/adminLogin`  
**Annotations**: `@WebServlet(urlPatterns = {"/adminLogin"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - Show login form
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    request.getRequestDispatcher("/jsp/adminLogin.jsp").forward(request, response);
}

// POST - Process login
+ protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow (POST)**:
1. Get username/password: `request.getParameter("username")`
2. Authenticate: `User user = DatabaseUtils.authenticateUser(username, password)`
3. Create session:
   ```java
   HttpSession session = request.getSession();
   session.setAttribute("userId", user.getId());
   session.setAttribute("username", user.getUsername());
   session.setMaxInactiveInterval(3600); // 1 hour
   ```
4. Redirect: `response.sendRedirect(request.getContextPath() + "/admin/dashboard")`

---

##### **controller/AdminDashboardServlet.java**
**Package**: `controller`  
**URL Pattern**: `/admin/dashboard`  
**Annotations**: `@WebServlet(urlPatterns = {"/admin/dashboard"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - Display dashboard
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow**:
1. Check authentication:
   ```java
   HttpSession session = request.getSession(false);
   if (session == null || session.getAttribute("userId") == null) {
       response.sendRedirect(request.getContextPath() + "/adminLogin");
       return;
   }
   ```
2. Query documents: `List<Map<String, Object>> docs = DatabaseUtils.getAllDocuments()`
3. Calculate statistics:
   ```java
   int totalDocs = docs.size();
   long totalSize = docs.stream().mapToLong(d -> (Long)d.get("file_size")).sum();
   ```
4. Set attributes and forward to JSP

**Inner Class**:
```java
public static class DocumentRow {
    private int id;
    private String filename;
    private long fileSize;
    private String uploadedAt;
    private String uploadedBy;
    // Getters & Setters
}
```

---

##### **controller/AdminUploadServlet.java**
**Package**: `controller`  
**URL Pattern**: `/admin/upload`  
**Annotations**: `@WebServlet(urlPatterns = {"/admin/upload"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - Show upload form
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException {
    checkAuth(request, response);
    request.getRequestDispatcher("/jsp/adminUpload.jsp").forward(request, response);
}

// POST - Handle upload
+ protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow (POST)**:
1. Check authentication
2. Parse multipart files (NO size limit for admin)
3. Extract text: `FileParser.extractText()`
4. Save to DB:
   ```java
   int userId = (Integer) request.getSession().getAttribute("userId");
   DatabaseUtils.saveDocument(filename, content, fileSize, userId);
   ```
5. Return JSON response:
   ```json
   {"success": true, "message": "File uploaded successfully", "documentId": 123}
   ```

---

##### **controller/AdminViewServlet.java**
**Package**: `controller`  
**URL Pattern**: `/admin/view`  
**Annotations**: `@WebServlet(urlPatterns = {"/admin/view"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - View document details
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow**:
1. Check authentication
2. Get document ID: `int id = Integer.parseInt(request.getParameter("id"))`
3. Query document: `Map<String, Object> doc = DatabaseUtils.getDocumentById(id)`
4. Set attributes: `request.setAttribute("document", doc)`
5. Forward: `request.getRequestDispatcher("/jsp/viewSubmission.jsp").forward()`

---

##### **controller/AdminDownloadServlet.java**
**Package**: `controller`  
**URL Pattern**: `/admin/download`  
**Annotations**: `@WebServlet(urlPatterns = {"/admin/download"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// GET - Download document
+ protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow**:
1. Check authentication
2. Get document from DB
3. Set response headers:
   ```java
   response.setContentType("application/octet-stream");
   response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
   ```
4. Write content to output stream

---

##### **controller/AdminDeleteServlet.java**
**Package**: `controller`  
**URL Pattern**: `/admin/delete`  
**Annotations**: `@WebServlet(urlPatterns = {"/admin/delete"})`  
**Extends**: `HttpServlet`

**HTTP Methods**:
```java
// POST - Delete document
+ protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    throws ServletException, IOException
```

**Process Flow**:
1. Check authentication
2. Get document ID
3. Delete: `DatabaseUtils.deleteDocument(id)`
4. Return JSON:
   ```json
   {"success": true, "message": "Document deleted successfully"}
   ```

---

##### **controller/AppConfig.java**
**Package**: `controller`  
**Type**: Configuration Class  
**Annotations**: `@WebListener`  
**Implements**: `ServletContextListener`

**Purpose**: Initialize application on startup

**Methods**:
```java
// Called when app starts
+ public void contextInitialized(ServletContextEvent sce) {
    // Initialize database connection pool
    // Load configuration
    // Setup logging
}

// Called when app stops
+ public void contextDestroyed(ServletContextEvent sce) {
    // Close database connections
    // Cleanup resources
}
```

**Configuration Constants**:
```java
public static final String DB_URL = "jdbc:mysql://localhost:3306/plagiarism_checker";
public static final String DB_USER = "root";
public static final String DB_PASSWORD = "khoakhoa04";
public static final int MAX_FILE_SIZE = 25 * 1024 * 1024; // 25MB
```

---

### 4️⃣ **UTILITY Layer** (Công cụ hỗ trợ)

#### 🛠️ **utils/FileParser.java**
**Package**: `utils`  
**Chức năng**: Trích xuất text từ các định dạng file khác nhau

**Dependencies**:
- Apache PDFBox 3.0.2 (PDF parsing)
- Apache POI 5.2.5 (DOCX parsing)

**Public Methods**:
```java
/**
 * Main entry point - extract text based on file type
 * @param input InputStream of uploaded file
 * @param fileType "pdf", "docx", or "txt"
 * @return Extracted text content
 */
+ public static String extractText(InputStream input, String fileType) 
    throws IOException {
    switch (fileType.toLowerCase()) {
        case "pdf":
            return extractFromPDF(input);
        case "docx":
            return extractFromDOCX(input);
        case "txt":
            return extractFromTXT(input);
        default:
            throw new IllegalArgumentException("Unsupported file type: " + fileType);
    }
}
```

**Private Methods**:
```java
/**
 * Extract text from PDF using PDFBox
 * Uses Loader.loadPDF() and PDFTextStripper
 */
- private static String extractFromPDF(InputStream input) throws IOException {
    try (PDDocument document = Loader.loadPDF(input.readAllBytes())) {
        PDFTextStripper stripper = new PDFTextStripper();
        return stripper.getText(document);
    }
}

/**
 * Extract text from DOCX using Apache POI
 * Parses XWPFDocument and extracts paragraphs
 */
- private static String extractFromDOCX(InputStream input) throws IOException {
    XWPFDocument document = new XWPFDocument(input);
    StringBuilder text = new StringBuilder();
    for (XWPFParagraph para : document.getParagraphs()) {
        text.append(para.getText()).append("\n");
    }
    document.close();
    return text.toString();
}

/**
 * Extract text from TXT file
 * Simple UTF-8 reading
 */
- private static String extractFromTXT(InputStream input) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
    StringBuilder text = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        text.append(line).append("\n");
    }
    return text.toString();
}
```

**Usage Example**:
```java
Part filePart = request.getPart("file");
InputStream fileContent = filePart.getInputStream();
String extractedText = FileParser.extractText(fileContent, "pdf");
```

---

#### 🧹 **utils/TextCleaner.java**
**Package**: `utils`  
**Chức năng**: Chuẩn hóa và làm sạch văn bản trước khi so sánh

**Public Methods**:
```java
/**
 * Main cleaning pipeline
 * @param text Raw text from document
 * @return Cleaned and normalized text
 */
+ public static String clean(String text) {
    if (text == null || text.isEmpty()) {
        return "";
    }
    
    text = toLowerCase(text);
    text = removeSpecialChars(text);
    text = normalizeWhitespace(text);
    text = removeDiacritics(text); // Optional - giữ nguyên dấu tiếng Việt
    
    return text.trim();
}

/**
 * Tokenize text into words
 * @param text Cleaned text
 * @return List of tokens
 */
+ public static List<String> tokenize(String text) {
    // Split by whitespace
    String[] tokens = text.split("\\s+");
    
    // Remove empty strings
    List<String> result = new ArrayList<>();
    for (String token : tokens) {
        if (!token.isEmpty()) {
            result.add(token);
        }
    }
    
    return result;
}
```

**Private Methods**:
```java
/**
 * Convert to lowercase
 */
- private static String toLowerCase(String text) {
    return text.toLowerCase(Locale.ROOT);
}

/**
 * Remove special characters, keep letters, numbers, spaces
 * Giữ dấu tiếng Việt: áàảãạăắằẳẵặâấầẩẫậéèẻẽẹêếềểễệ...
 */
- private static String removeSpecialChars(String text) {
    // Keep Vietnamese characters + alphanumeric + spaces
    return text.replaceAll("[^a-zA-Z0-9\\s\\p{L}]", " ");
}

/**
 * Normalize whitespace (multiple spaces → single space)
 */
- private static String normalizeWhitespace(String text) {
    return text.replaceAll("\\s+", " ");
}

/**
 * Remove Vietnamese diacritics (optional)
 * "Tiếng Việt" → "tieng viet"
 */
- private static String removeDiacritics(String text) {
    String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
    return normalized.replaceAll("\\p{M}", "");
}
```

**Usage Example**:
```java
String rawText = "Hệ thống kinh tế... !@#$%";
String cleaned = TextCleaner.clean(rawText);
List<String> tokens = TextCleaner.tokenize(cleaned);
```

---

#### 🔍 **utils/TextSimilarity.java**
**Package**: `utils`  
**Chức năng**: Thuật toán kiểm tra đạo văn (Winnowing + TF-IDF + Citation Detection)

**Constants**:
```java
private static final int NGRAM_SIZE = 5;           // k-gram size
private static final int WINDOW_SIZE = 4;          // Winnowing window
private static final long PRIME = 101;             // Rolling hash prime
private static final long BASE = 256;              // Rolling hash base
```

---

##### **A. Winnowing Algorithm Methods**

```java
/**
 * Calculate Winnowing similarity between two texts
 * @return Similarity score (0.0 - 1.0)
 */
+ public static double calculateWinnowing(String text1, String text2) {
    // 1. Generate fingerprints
    Set<Long> fingerprints1 = generateFingerprints(text1);
    Set<Long> fingerprints2 = generateFingerprints(text2);
    
    // 2. Compare fingerprints (Jaccard similarity)
    return compareFingerprints(fingerprints1, fingerprints2);
}

/**
 * Generate Winnowing fingerprints
 */
- private static Set<Long> generateFingerprints(String text) {
    // 1. Generate n-grams
    List<String> ngrams = generateNGrams(text, NGRAM_SIZE);
    
    // 2. Compute rolling hashes
    List<Long> hashes = computeRollingHash(ngrams);
    
    // 3. Select fingerprints (min hash in each window)
    return selectFingerprints(hashes, WINDOW_SIZE);
}

/**
 * Generate n-grams from text
 * Example: "hello world" (k=3) → ["hel", "ell", "llo", "lo ", "o w", " wo", "wor", "orl", "rld"]
 */
- private static List<String> generateNGrams(String text, int k) {
    List<String> ngrams = new ArrayList<>();
    for (int i = 0; i <= text.length() - k; i++) {
        ngrams.add(text.substring(i, i + k));
    }
    return ngrams;
}

/**
 * Compute Rabin-Karp rolling hash for each n-gram
 */
- private static List<Long> computeRollingHash(List<String> ngrams) {
    List<Long> hashes = new ArrayList<>();
    for (String ngram : ngrams) {
        long hash = 0;
        for (char c : ngram.toCharArray()) {
            hash = (hash * BASE + c) % PRIME;
        }
        hashes.add(hash);
    }
    return hashes;
}

/**
 * Select minimum hash in each sliding window (Winnowing)
 * Window size = 4: [h1, h2, h3, h4] → select min
 */
- private static Set<Long> selectFingerprints(List<Long> hashes, int windowSize) {
    Set<Long> fingerprints = new HashSet<>();
    
    for (int i = 0; i <= hashes.size() - windowSize; i++) {
        long minHash = Long.MAX_VALUE;
        for (int j = i; j < i + windowSize; j++) {
            minHash = Math.min(minHash, hashes.get(j));
        }
        fingerprints.add(minHash);
    }
    
    return fingerprints;
}

/**
 * Compare two sets of fingerprints using Jaccard similarity
 * Jaccard = |A ∩ B| / |A ∪ B|
 */
- private static double compareFingerprints(Set<Long> fp1, Set<Long> fp2) {
    Set<Long> intersection = new HashSet<>(fp1);
    intersection.retainAll(fp2);
    
    Set<Long> union = new HashSet<>(fp1);
    union.addAll(fp2);
    
    if (union.isEmpty()) return 0.0;
    
    return (double) intersection.size() / union.size();
}
```

---

##### **B. TF-IDF Algorithm Methods**

```java
/**
 * Calculate TF-IDF cosine similarity
 * @return Similarity score (0.0 - 1.0)
 */
+ public static double calculateTFIDF(String text1, String text2) {
    // 1. Tokenize
    List<String> tokens1 = TextCleaner.tokenize(text1);
    List<String> tokens2 = TextCleaner.tokenize(text2);
    
    // 2. Compute TF
    Map<String, Double> tf1 = computeTF(tokens1);
    Map<String, Double> tf2 = computeTF(tokens2);
    
    // 3. Compute IDF (from corpus - simplified: use both documents)
    List<List<String>> corpus = Arrays.asList(tokens1, tokens2);
    Map<String, Double> idf = computeIDF(corpus);
    
    // 4. Create TF-IDF vectors
    Map<String, Double> tfidf1 = createTFIDFVector(tf1, idf);
    Map<String, Double> tfidf2 = createTFIDFVector(tf2, idf);
    
    // 5. Compute cosine similarity
    return cosineSimilarity(tfidf1, tfidf2);
}

/**
 * Compute Term Frequency
 * TF(word) = count(word) / total_words
 */
- private static Map<String, Double> computeTF(List<String> tokens) {
    Map<String, Integer> wordCount = new HashMap<>();
    for (String token : tokens) {
        wordCount.put(token, wordCount.getOrDefault(token, 0) + 1);
    }
    
    Map<String, Double> tf = new HashMap<>();
    int totalWords = tokens.size();
    for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
        tf.put(entry.getKey(), (double) entry.getValue() / totalWords);
    }
    
    return tf;
}

/**
 * Compute Inverse Document Frequency
 * IDF(word) = log(total_docs / docs_containing_word)
 */
- private static Map<String, Double> computeIDF(List<List<String>> corpus) {
    Map<String, Integer> docCount = new HashMap<>();
    
    for (List<String> doc : corpus) {
        Set<String> uniqueWords = new HashSet<>(doc);
        for (String word : uniqueWords) {
            docCount.put(word, docCount.getOrDefault(word, 0) + 1);
        }
    }
    
    Map<String, Double> idf = new HashMap<>();
    int totalDocs = corpus.size();
    for (Map.Entry<String, Integer> entry : docCount.entrySet()) {
        idf.put(entry.getKey(), Math.log((double) totalDocs / entry.getValue()));
    }
    
    return idf;
}

/**
 * Create TF-IDF vector
 * TF-IDF(word) = TF(word) × IDF(word)
 */
- private static Map<String, Double> createTFIDFVector(Map<String, Double> tf, Map<String, Double> idf) {
    Map<String, Double> tfidf = new HashMap<>();
    for (Map.Entry<String, Double> entry : tf.entrySet()) {
        String word = entry.getKey();
        double tfValue = entry.getValue();
        double idfValue = idf.getOrDefault(word, 0.0);
        tfidf.put(word, tfValue * idfValue);
    }
    return tfidf;
}

/**
 * Compute cosine similarity between two vectors
 * cos(θ) = (A · B) / (||A|| × ||B||)
 */
- private static double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2) {
    // Dot product
    double dotProduct = 0.0;
    for (String key : vec1.keySet()) {
        if (vec2.containsKey(key)) {
            dotProduct += vec1.get(key) * vec2.get(key);
        }
    }
    
    // Magnitudes
    double magnitude1 = Math.sqrt(vec1.values().stream().mapToDouble(v -> v * v).sum());
    double magnitude2 = Math.sqrt(vec2.values().stream().mapToDouble(v -> v * v).sum());
    
    if (magnitude1 == 0.0 || magnitude2 == 0.0) return 0.0;
    
    return dotProduct / (magnitude1 * magnitude2);
}
```

---

##### **C. Citation Detection Methods**

```java
/**
 * Check if text segment contains proper citation
 * @return true if citation found
 */
+ public static boolean hasCitation(String segment) {
    return detectAPAFormat(segment) 
        || detectIEEEFormat(segment) 
        || detectURL(segment) 
        || detectFootnote(segment);
}

/**
 * Detect APA format: (Author, Year)
 * Pattern: (Nguyen, 2020), (Smith et al., 2019)
 */
- private static boolean detectAPAFormat(String text) {
    Pattern pattern = Pattern.compile("\\([A-Z][a-z]+(\\s+et\\s+al\\.)?\\s*,\\s*\\d{4}\\)");
    return pattern.matcher(text).find();
}

/**
 * Detect IEEE format: [1], [2], [3-5]
 * Pattern: [number] or [number-number]
 */
- private static boolean detectIEEEFormat(String text) {
    Pattern pattern = Pattern.compile("\\[\\d+([-–]\\d+)?\\]");
    return pattern.matcher(text).find();
}

/**
 * Detect URL: http://, https://, www.
 */
- private static boolean detectURL(String text) {
    Pattern pattern = Pattern.compile("(https?://|www\\.)\\S+");
    return pattern.matcher(text).find();
}

/**
 * Detect footnote: ^1, ^2
 */
- private static boolean detectFootnote(String text) {
    Pattern pattern = Pattern.compile("\\^\\d+");
    return pattern.matcher(text).find();
}

/**
 * Find matched segments between two texts
 * @return List of matched text segments
 */
+ public static List<String> findMatchedSegments(String text1, String text2) {
    List<String> matches = new ArrayList<>();
    
    // Split into sentences
    String[] sentences1 = text1.split("[.!?]");
    String[] sentences2 = text2.split("[.!?]");
    
    // Compare sentences
    for (String s1 : sentences1) {
        for (String s2 : sentences2) {
            double similarity = calculateWinnowing(s1.trim(), s2.trim());
            if (similarity > 0.7) { // Threshold: 70% similar
                matches.add(s1.trim());
                break;
            }
        }
    }
    
    return matches;
}
```

**Usage Example**:
```java
// Winnowing
double winnowingSim = TextSimilarity.calculateWinnowing(text1, text2);

// TF-IDF
double tfidfSim = TextSimilarity.calculateTFIDF(text1, text2);

// Citation check
List<String> segments = TextSimilarity.findMatchedSegments(text1, text2);
for (String segment : segments) {
    boolean cited = TextSimilarity.hasCitation(segment);
    // ... process
}
```

---

## 🗄️ Database Schema

### **Tables**

#### **users** (Admin accounts)
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### **documents** (Admin uploaded documents)
```sql
CREATE TABLE documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    file_size BIGINT,
    uploaded_by INT,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE
);
```

#### **submissions** (Guest uploaded files)
```sql
CREATE TABLE submissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    content LONGTEXT NOT NULL,
    guest_token VARCHAR(100),
    user_id INT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
```

#### **results** (Plagiarism check results)
```sql
CREATE TABLE results (
    id INT PRIMARY KEY AUTO_INCREMENT,
    submission_id INT NOT NULL,
    similarity_winnowing DOUBLE,
    similarity_tfidf DOUBLE,
    matched_segments JSON,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES submissions(id) ON DELETE CASCADE
);
```

---

## 🔄 Luồng xử lý (Data Flow)

### **Luồng 1: Guest User Upload & Check Plagiarism**

```
┌──────────────────────────────────────────────────────────────────────────┐
│                      GUEST USER PLAGIARISM CHECK FLOW                     │
└──────────────────────────────────────────────────────────────────────────┘

[Browser] → GET /upload
              ↓
         UploadServlet.doGet()
              ↓
         redirect → upload.jsp
              ↓
[User selects files (PDF/DOCX/TXT) - Max 25MB]
              ↓
         POST /upload (multipart/form-data)
              ↓
    ┌─────────────────────────────────┐
    │  UploadServlet.doPost()         │
    │  -------------------------      │
    │  1. Parse request.getParts()    │
    │  2. Validate file type          │
    │  3. Validate size (≤ 25MB)      │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  FileParser.extractText()       │
    │  -------------------------      │
    │  • PDF  → Apache PDFBox         │
    │  • DOCX → Apache POI            │
    │  • TXT  → BufferedReader        │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  TextCleaner.clean()            │
    │  -------------------------      │
    │  • toLowerCase()                │
    │  • removeSpecialChars()         │
    │  • normalizeWhitespace()        │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  new Submission()               │
    │  -------------------------      │
    │  filename: "essay.pdf"          │
    │  content: "cleaned text..."     │
    │  guestToken: UUID.random()      │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  DatabaseUtils.saveSubmission() │
    │  -------------------------      │
    │  INSERT INTO submissions        │
    │  Returns: submissionId          │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  QueueWorker.startWorker()      │
    │  -------------------------      │
    │  new Thread(worker).start()     │
    │  [Background Processing]        │
    └─────────────────────────────────┘
              ↓
         redirect → /result?token=xxx
              ↓
    ┌──────────────────────────────────────────────────────────┐
    │  [BACKGROUND THREAD] QueueWorker.run()                   │
    │  -------------------------------------------------------  │
    │  1. Get submission from DB                               │
    │  2. Get all documents corpus                             │
    │  3. For each document:                                   │
    │     • TextSimilarity.calculateWinnowing()                │
    │     • TextSimilarity.calculateTFIDF()                    │
    │     • TextSimilarity.findMatchedSegments()               │
    │     • TextSimilarity.hasCitation()                       │
    │  4. Create Result object                                 │
    │  5. DatabaseUtils.saveResult()                           │
    └──────────────────────────────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  ResultServlet.doGet()          │
    │  -------------------------      │
    │  1. Get token from query param  │
    │  2. Query submission by token   │
    │  3. Query result by submissionId│
    │  4. Calculate chart data        │
    │  5. Parse matched segments      │
    └─────────────────────────────────┘
              ↓
         forward → result.jsp
              ↓
    ┌─────────────────────────────────┐
    │  result.jsp                     │
    │  -------------------------      │
    │  • Pie Chart (Chart.js)         │
    │  • Similarity percentages       │
    │  • Matched segments list        │
    │  • Citation indicators          │
    └─────────────────────────────────┘
              ↓
         [Display Result to User]
```

### **Luồng 2: Admin Upload Document**

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          ADMIN UPLOAD FLOW                                │
└──────────────────────────────────────────────────────────────────────────┘

[Browser] → GET /adminLogin
              ↓
         AdminLoginServlet.doGet()
              ↓
         forward → adminLogin.jsp
              ↓
[Admin enters username/password]
              ↓
         POST /adminLogin
              ↓
    ┌─────────────────────────────────┐
    │  AdminLoginServlet.doPost()     │
    │  -------------------------      │
    │  1. Get username/password       │
    │  2. DatabaseUtils.authenticate()│
    │  3. Verify password (BCrypt)    │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  Create Session                 │
    │  -------------------------      │
    │  session.setAttribute(          │
    │    "userId", user.getId()       │
    │    "username", user.getName()   │
    │  )                              │
    └─────────────────────────────────┘
              ↓
         redirect → /admin/dashboard
              ↓
    ┌─────────────────────────────────┐
    │  AdminDashboardServlet.doGet()  │
    │  -------------------------      │
    │  1. Check session auth          │
    │  2. Get all documents from DB   │
    │  3. Calculate statistics        │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  DatabaseUtils.getAllDocuments()│
    │  -------------------------      │
    │  SELECT * FROM documents        │
    │  Returns: List<Map>             │
    └─────────────────────────────────┘
              ↓
         forward → adminDashboard.jsp
              ↓
    ┌─────────────────────────────────┐
    │  adminDashboard.jsp             │
    │  -------------------------      │
    │  • Statistics cards             │
    │  • Documents table              │
    │  • Action buttons (CRUD)        │
    └─────────────────────────────────┘
              ↓
[Admin clicks "Upload Document"]
              ↓
         GET /admin/upload
              ↓
    ┌─────────────────────────────────┐
    │  AdminUploadServlet.doGet()     │
    │  -------------------------      │
    │  1. Check session auth          │
    │  2. Forward to adminUpload.jsp  │
    └─────────────────────────────────┘
              ↓
[Admin selects files - NO SIZE LIMIT]
              ↓
         POST /admin/upload
              ↓
    ┌─────────────────────────────────┐
    │  AdminUploadServlet.doPost()    │
    │  -------------------------      │
    │  1. Check session auth          │
    │  2. Parse multipart files       │
    │  3. Process each file:          │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  FileParser.extractText()       │
    │  -------------------------      │
    │  Extract content from file      │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  TextCleaner.clean()            │
    │  -------------------------      │
    │  Clean extracted text           │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  DatabaseUtils.saveDocument()   │
    │  -------------------------      │
    │  INSERT INTO documents          │
    │  (filename, content, fileSize,  │
    │   uploaded_by)                  │
    └─────────────────────────────────┘
              ↓
         Return JSON {success: true, documentId: 123}
              ↓
[JavaScript displays success & refreshes dashboard]
```

---

### **Luồng 3: Admin View/Download/Delete Document**

```
┌──────────────────────────────────────────────────────────────────────────┐
│                      ADMIN CRUD OPERATIONS FLOW                           │
└──────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│  OPERATION 1: VIEW DOCUMENT                                    │
└────────────────────────────────────────────────────────────────┘

[Admin Dashboard] → Click "View" button
              ↓
         GET /admin/view?id=123
              ↓
    ┌─────────────────────────────────┐
    │  AdminViewServlet.doGet()       │
    │  -------------------------      │
    │  1. Check session auth          │
    │  2. Get id from param           │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  DatabaseUtils.getDocumentById()│
    │  -------------------------      │
    │  SELECT * FROM documents        │
    │  WHERE id = ?                   │
    └─────────────────────────────────┘
              ↓
         request.setAttribute("document", doc)
              ↓
         forward → viewSubmission.jsp
              ↓
    ┌─────────────────────────────────┐
    │  viewSubmission.jsp             │
    │  -------------------------      │
    │  • Document metadata            │
    │  • Full text content            │
    │  • Download button              │
    └─────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│  OPERATION 2: DOWNLOAD DOCUMENT                                │
└────────────────────────────────────────────────────────────────┘

[Admin Dashboard] → Click "Download" button
              ↓
         GET /admin/download?id=123
              ↓
    ┌─────────────────────────────────┐
    │  AdminDownloadServlet.doGet()   │
    │  -------------------------      │
    │  1. Check session auth          │
    │  2. Get id from param           │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  DatabaseUtils.getDocumentById()│
    │  -------------------------      │
    │  SELECT * FROM documents        │
    │  WHERE id = ?                   │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  Set Response Headers           │
    │  -------------------------      │
    │  Content-Type: application/...  │
    │  Content-Disposition: attachment│
    │  filename="document.pdf"        │
    └─────────────────────────────────┘
              ↓
         Stream content to browser
              ↓
[Browser downloads file]

┌────────────────────────────────────────────────────────────────┐
│  OPERATION 3: DELETE DOCUMENT                                  │
└────────────────────────────────────────────────────────────────┘

[Admin Dashboard] → Click "Delete" button
              ↓
         Confirm dialog (JavaScript)
              ↓
         POST /admin/delete
         Body: id=123
              ↓
    ┌─────────────────────────────────┐
    │  AdminDeleteServlet.doPost()    │
    │  -------------------------      │
    │  1. Check session auth          │
    │  2. Get id from request body    │
    └─────────────────────────────────┘
              ↓
    ┌─────────────────────────────────┐
    │  DatabaseUtils.deleteDocument() │
    │  -------------------------      │
    │  DELETE FROM documents          │
    │  WHERE id = ?                   │
    └─────────────────────────────────┘
              ↓
         Return JSON {success: true, message: "Deleted"}
              ↓
[JavaScript removes row from table & shows notification]
```

---

## 🧪 Thuật toán kiểm tra đạo văn

### **A. Winnowing Algorithm (N-Gram + Rolling Hash)**

**Bước 1**: Tạo N-Gram (k=5)
```
Text: "kinh tế thị trường định hướng XHCN"
N-Gram: ["kinh tế thị trường định", "tế thị trường định hướng", ...]
```

**Bước 2**: Tính Rolling Hash cho mỗi n-gram
```
Hash function: Rabin-Karp rolling hash
hash("kinh tế thị trường định") = 12345678
```

**Bước 3**: Trượt cửa sổ (window=4), chọn min hash
```
Window [h1, h2, h3, h4] → select min → Fingerprint
```

**Bước 4**: So sánh fingerprint
```
Similarity = (số fingerprint trùng) / (tổng số fingerprint)
```

### **B. TF-IDF + Cosine Similarity**

**Bước 1**: Tính TF (Term Frequency)
```
TF(word) = (số lần xuất hiện của word) / (tổng số từ)
```

**Bước 2**: Tính IDF (Inverse Document Frequency)
```
IDF(word) = log(tổng số documents / số documents chứa word)
```

**Bước 3**: Tạo vector TF-IDF
```
TF-IDF(word) = TF(word) × IDF(word)
Vector = [0.12, 0.45, 0.08, ...]
```

**Bước 4**: Tính Cosine Similarity
```
Similarity = (Vector1 · Vector2) / (||Vector1|| × ||Vector2||)
```

### **C. Citation Detection**

**Patterns**:
```regex
APA: \(([A-Z][a-z]+,\s*\d{4})\)
IEEE: \[\d+\]
URL: https?://[^\s]+
Footnote: \^\d+
```

**Logic**:
```
IF (matched_segment contains citation_pattern) THEN
    status = "Properly Cited"
ELSE
    status = "Plagiarism Suspected"
END IF
```

---

## 📊 Output JSON Format

```json
{
  "submissionId": 123,
  "filename": "MyEssay.docx",
  "uploadedAt": "2025-11-23T10:30:00Z",
  "similarity": {
    "winnowing": 0.63,
    "tfidf": 0.52,
    "overall": 0.575
  },
  "matchedSegments": [
    {
      "text": "kinh tế thị trường định hướng XHCN là mô hình...",
      "source": "GiaoTrinh_KinhTe.pdf",
      "sourceId": 45,
      "similarity": 0.95,
      "hasCitation": false,
      "startIndex": 120,
      "endIndex": 350
    },
    {
      "text": "theo nghiên cứu của Nguyễn (2020)...",
      "source": "LuanVan_ABC.docx",
      "sourceId": 78,
      "similarity": 0.85,
      "hasCitation": true,
      "citationType": "APA"
    }
  ],
  "status": "Plagiarism Suspected",
  "chart": {
    "original": 42.5,
    "partialMatch": 35.0,
    "exactMatch": 22.5
  }
}
```

---

## 🔒 Authentication & Authorization

### **Session Management**

```java
// Admin login
HttpSession session = request.getSession();
session.setAttribute("userId", user.getId());
session.setAttribute("username", user.getUsername());
session.setMaxInactiveInterval(3600); // 1 hour

// Guest upload
String guestToken = UUID.randomUUID().toString();
// Store in DB, use for result retrieval
```

### **Admin Filter**

```java
// Check admin authentication before accessing /admin/*
if (session.getAttribute("userId") == null) {
    response.sendRedirect("/adminLogin");
    return;
}
```

---

## 🚀 Deployment

### **Requirements**
- Java 17+
- Apache Tomcat 10.x
- MySQL 8.0+
- Maven 3.8+

### **Build & Deploy**

```bash
# 1. Clone project
git clone <repository-url>
cd PlagiarismChecker

# 2. Configure database
mysql -u root -p < database/schema.sql

# 3. Update database credentials in AppConfig.java
# DB_URL = "jdbc:mysql://localhost:3306/plagiarism_checker"
# DB_USER = "root"
# DB_PASS = "khoakhoa04"

# 4. Build WAR file
mvn clean package

# 5. Deploy to Tomcat
cp target/PlagiarismChecker-1.0-SNAPSHOT.war %CATALINA_HOME%/webapps/

# 6. Start Tomcat
%CATALINA_HOME%/bin/startup.bat
```

### **Access URLs**
- **Guest Upload**: `http://localhost:8080/PlagiarismChecker/`
- **Admin Login**: `http://localhost:8080/PlagiarismChecker/adminLogin`
- **Admin Dashboard**: `http://localhost:8080/PlagiarismChecker/admin/dashboard`

---

## 📚 Class & Method Reference (Quick Lookup)

### **MODEL Layer Classes**

#### **DatabaseUtils.java**
```java
package model;

public class DatabaseUtils {
    // Connection
    public static Connection getConnection() throws SQLException
    
    // Submissions
    public static int saveSubmission(Submission s) throws SQLException
    public static Submission getSubmissionById(int id) throws SQLException
    public static Submission getSubmissionByToken(String token) throws SQLException
    
    // Results
    public static void saveResult(Result r) throws SQLException
    public static Result getResultBySubmissionId(int submissionId) throws SQLException
    
    // Documents (Admin)
    public static int saveDocument(String filename, String content, long size, int userId) throws SQLException
    public static List<Map<String, Object>> getAllDocuments() throws SQLException
    public static Map<String, Object> getDocumentById(int id) throws SQLException
    public static void deleteDocument(int id) throws SQLException
    public static List<String> getAllDocumentContents() throws SQLException
    
    // Users
    public static User authenticateUser(String username, String password) throws SQLException
}
```

#### **Submission.java**
```java
package model;

public class Submission {
    private int id;
    private String filename;
    private String content;
    private Timestamp uploadedAt;
    private String guestToken;
    private Integer userId;
    
    // Constructors, Getters, Setters...
}
```

#### **Result.java**
```java
package model;

public class Result {
    private int id;
    private int submissionId;
    private double similarityWinnowing;
    private double similarityTfidf;
    private String matchedSegments; // JSON
    private String status;
    private Timestamp createdAt;
    
    public double getOverallSimilarity()
    // Constructors, Getters, Setters...
}
```

#### **User.java**
```java
package model;

public class User {
    private int id;
    private String username;
    private String password; // BCrypt hashed
    private String email;
    private Timestamp createdAt;
    
    public boolean checkPassword(String plainPassword)
    public static String hashPassword(String plainPassword)
    // Constructors, Getters, Setters...
}
```

---

### **CONTROLLER Layer Classes**

#### **UploadServlet.java**
```java
package controller;

@WebServlet(urlPatterns = {"/upload"})
public class UploadServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    
    private boolean isValidFileType(String filename)
    private long calculateTotalSize(Collection<Part> parts)
}
```

#### **ResultServlet.java**
```java
package controller;

@WebServlet(urlPatterns = {"/result"})
public class ResultServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    
    private Map<String, Double> calculateChartData(Result result)
}
```

#### **QueueWorker.java**
```java
package controller;

public class QueueWorker implements Runnable {
    private int submissionId;
    
    public QueueWorker(int submissionId)
    
    public void run()
    
    private void processSubmission(int submissionId) throws SQLException
    
    public static void startWorker(int submissionId) // Start background thread
}
```

#### **AdminLoginServlet.java**
```java
package controller;

@WebServlet(urlPatterns = {"/adminLogin"})
public class AdminLoginServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
}
```

#### **AdminDashboardServlet.java**
```java
package controller;

@WebServlet(urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    
    public static class DocumentRow {
        // Inner class for dashboard display
    }
}
```

#### **AdminUploadServlet.java**
```java
package controller;

@WebServlet(urlPatterns = {"/admin/upload"})
public class AdminUploadServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
}
```

---

### **UTILS Layer Classes**

#### **FileParser.java**
```java
package utils;

public class FileParser {
    public static String extractText(InputStream input, String fileType) throws IOException
    
    private static String extractFromPDF(InputStream input) throws IOException
    private static String extractFromDOCX(InputStream input) throws IOException
    private static String extractFromTXT(InputStream input) throws IOException
}
```

#### **TextCleaner.java**
```java
package utils;

public class TextCleaner {
    public static String clean(String text)
    public static List<String> tokenize(String text)
    
    private static String toLowerCase(String text)
    private static String removeSpecialChars(String text)
    private static String normalizeWhitespace(String text)
}
```

#### **TextSimilarity.java**
```java
package utils;

public class TextSimilarity {
    // Winnowing
    public static double calculateWinnowing(String text1, String text2)
    private static Set<Long> generateFingerprints(String text)
    private static List<String> generateNGrams(String text, int k)
    private static List<Long> computeRollingHash(List<String> ngrams)
    private static Set<Long> selectFingerprints(List<Long> hashes, int windowSize)
    private static double compareFingerprints(Set<Long> fp1, Set<Long> fp2)
    
    // TF-IDF
    public static double calculateTFIDF(String text1, String text2)
    private static Map<String, Double> computeTF(List<String> tokens)
    private static Map<String, Double> computeIDF(List<List<String>> corpus)
    private static Map<String, Double> createTFIDFVector(Map<String, Double> tf, Map<String, Double> idf)
    private static double cosineSimilarity(Map<String, Double> vec1, Map<String, Double> vec2)
    
    // Citation Detection
    public static boolean hasCitation(String segment)
    private static boolean detectAPAFormat(String text)
    private static boolean detectIEEEFormat(String text)
    private static boolean detectURL(String text)
    private static boolean detectFootnote(String text)
    
    // Segment Matching
    public static List<String> findMatchedSegments(String text1, String text2)
}
```

---

## 📝 API Endpoints

| Endpoint | Method | Authentication | Description |
|----------|--------|----------------|-------------|
| `/` | GET | None | Redirect to `/jsp/upload.jsp` |
| `/upload` | POST | None | Upload files for plagiarism check |
| `/result` | GET | None (token required) | View plagiarism result |
| `/adminLogin` | GET/POST | None | Admin login page/form |
| `/admin/dashboard` | GET | Admin | View all documents |
| `/admin/upload` | GET/POST | Admin | Upload documents to storage |
| `/admin/view` | GET | Admin | View document details |
| `/admin/download` | GET | Admin | Download document |
| `/admin/delete` | POST | Admin | Delete document |

---

## 🎨 UI Components

### **Upload Page Features**
- Drag & drop file upload
- Multiple file selection
- File type validation (PDF, DOCX, TXT)
- File size validation (max 25MB total)
- Progress bar for each file
- Remove file button (X)
- Submit button (Check Plagiarism)

### **Result Page Features**
- Pie chart (Original, Partial, Exact)
- Overall similarity percentage
- Color-coded segments:
  - 🟢 Green: Original (0-30%)
  - 🟡 Yellow: Partial Match (31-60%)
  - 🔴 Red: Exact Match (61-100%)
- Matched segments list with source
- Citation status indicator
- Download report button

### **Admin Dashboard Features**
- Statistics cards (Total Docs, Size, Recent)
- Sortable data table
- Search/filter functionality
- Actions dropdown (View, Download, Delete)
- Pagination
- Responsive design

---

## 🔧 Technologies Used

| Category | Technology | Version |
|----------|-----------|---------|
| **Backend** | Jakarta Servlet API | 6.1.0 |
| | JSP (JavaServer Pages) | 3.1 |
| **Database** | MySQL Connector/J | 8.2.0 |
| **File Processing** | Apache PDFBox | 3.0.2 |
| | Apache POI | 5.2.5 |
| **Frontend** | JSTL | 3.0.1 |
| | Bootstrap | 5.3 |
| | Chart.js | 4.4.0 |
| **Build Tool** | Maven | 3.8+ |
| **Server** | Apache Tomcat | 10.1.48 |

---

## 🐛 Error Handling

### **Common Errors**

1. **File Too Large**
   ```java
   if (totalSize > 25 * 1024 * 1024) {
       response.sendError(413, "Total file size exceeds 25MB");
   }
   ```

2. **Invalid File Type**
   ```java
   if (!Arrays.asList("pdf", "docx", "txt").contains(ext)) {
       response.sendError(400, "Unsupported file type");
   }
   ```

3. **Database Connection Failed**
   ```java
   try {
       conn = DatabaseUtils.getConnection();
   } catch (SQLException e) {
       logger.error("DB connection failed", e);
       response.sendError(500, "Database error");
   }
   ```

4. **Unauthorized Access**
   ```java
   if (session.getAttribute("userId") == null) {
       response.sendRedirect("/adminLogin");
   }
   ```

---

## 📈 Performance Optimization

1. **Connection Pooling**: Sử dụng HikariCP
2. **Lazy Loading**: Chỉ load content khi cần thiết
3. **Caching**: Cache IDF vectors để tránh tính lại
4. **Async Processing**: QueueWorker chạy trong background thread
5. **Index Database**: Index trên `guest_token`, `uploaded_at`, `filename`

---

## 🔮 Future Enhancements

1. **Real-time Progress**: WebSocket cho progress upload
2. **Batch Processing**: Xử lý nhiều file parallel
3. **Export Report**: PDF/Excel report
4. **Email Notification**: Gửi kết quả qua email
5. **Advanced Analytics**: Dashboard charts cho admin
6. **Language Detection**: Tự động detect tiếng Việt/Anh
7. **OCR Support**: Scan ảnh trong PDF
8. **API REST**: Expose API cho external integration

---

## 📞 Support

- **Developer**: PBL4 Team
- **Database**: `plagiarism_checker`
- **Admin Default**: 
  - Username: `admin`
  - Password: `admin123`

---

## 📄 License

This project is developed for educational purposes (PBL4 - Database Project).

---

**Last Updated**: November 23, 2025

