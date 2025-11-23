# 🏗️ MVC Architecture - Plagiarism Checker System

## 📊 System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
│  (Browser - User Interface)                                      │
└─────────────────┬───────────────────────────────────────────────┘
                  │
                  │ HTTP Requests
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                            │
│                         (VIEW)                                   │
├─────────────────────────────────────────────────────────────────┤
│  JSP Files:                                                      │
│  ├── upload.jsp              (Public file upload interface)     │
│  ├── result.jsp              (Plagiarism results display)       │
│  ├── adminLogin.jsp          (Admin authentication)             │
│  ├── adminDashboard.jsp      (Admin document management)        │
│  ├── adminUpload.jsp         (Admin reference upload)           │
│  └── viewSubmission.jsp      (Document detail view)             │
└─────────────────┬───────────────────────────────────────────────┘
                  │
                  │ Forward/Redirect
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                   CONTROLLER LAYER                               │
│                    (SERVLET)                                     │
├─────────────────────────────────────────────────────────────────┤
│  Public Servlets:                                                │
│  ├── UploadServlet            POST /upload                       │
│  │   └─> Handle file uploads, validation, queue submission      │
│  ├── ResultServlet            GET /result?id=xxx                 │
│  │   └─> Display plagiarism check results                       │
│  └── QueueWorker              Background Thread                  │
│      └─> Process submissions sequentially                        │
│                                                                   │
│  Admin Servlets:                                                 │
│  ├── LoginServlet             POST /login                        │
│  │   └─> Authenticate admin users                               │
│  ├── AdminDashboardServlet    GET /admin/dashboard              │
│  │   └─> Show document list & statistics                        │
│  ├── AdminUploadServlet       POST /adminUpload                 │
│  │   └─> Upload reference documents                             │
│  ├── AdminViewServlet         GET /admin/view?id=xxx            │
│  │   └─> View document details                                  │
│  ├── AdminDownloadServlet     GET /admin/download?id=xxx        │
│  │   └─> Download documents                                     │
│  └── AdminDeleteServlet       POST /admin/delete                │
│      └─> Delete documents from repository                       │
└─────────────────┬───────────────────────────────────────────────┘
                  │
                  │ Business Logic Calls
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                    BUSINESS LOGIC LAYER                          │
│                        (UTILS)                                   │
├─────────────────────────────────────────────────────────────────┤
│  Document Processing:                                            │
│  ├── FileParser.java                                             │
│  │   ├─> extractTextFromPDF()                                    │
│  │   ├─> extractTextFromDOCX()                                   │
│  │   └─> extractTextFromTXT()                                    │
│  │                                                                │
│  ├── TextCleaner.java                                            │
│  │   ├─> cleanText()           (Remove special chars)           │
│  │   ├─> normalizeWhitespace() (Clean spacing)                  │
│  │   └─> tokenize()            (Split into words)               │
│  │                                                                │
│  Plagiarism Detection Algorithms:                                │
│  ├── Winnowing.java                                              │
│  │   ├─> generateNGrams()      (k=5 or 6)                       │
│  │   ├─> computeHash()         (Rolling hash)                   │
│  │   ├─> selectFingerprints()  (Window w=4)                     │
│  │   └─> compareFingerprints() (Detect matches)                 │
│  │                                                                │
│  ├── TFIDF.java                                                  │
│  │   ├─> computeTF()           (Term frequency)                 │
│  │   ├─> computeIDF()          (Inverse doc frequency)          │
│  │   ├─> buildTFIDFVector()    (Document vectors)               │
│  │   └─> cosineSimilarity()    (Compare similarity)             │
│  │                                                                │
│  ├── CitationDetector.java                                       │
│  │   ├─> detectAPACitation()   (Author, Year)                   │
│  │   ├─> detectIEEECitation()  ([Number])                       │
│  │   └─> detectURLCitation()   (http://, www.)                  │
│  │                                                                │
│  └── SimilarityService.java                                      │
│      ├─> compareDocuments()    (Main comparison logic)          │
│      ├─> findMatchedSegments() (Locate similar parts)           │
│      └─> generateReport()      (Create JSON result)             │
│                                                                   │
│  Text Analysis:                                                  │
│  └── TextSimilarity.java                                         │
│      ├─> calculateSimilarity() (Overall similarity)             │
│      └─> getMatchedSegments()  (Find plagiarized parts)         │
└─────────────────┬───────────────────────────────────────────────┘
                  │
                  │ Data Operations
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                      MODEL LAYER                                 │
│                   (DATA MODELS)                                  │
├─────────────────────────────────────────────────────────────────┤
│  Entity Classes:                                                 │
│  ├── User.java                                                   │
│  │   └─> id, username, password, role, created_at               │
│  │                                                                │
│  ├── Submission.java                                             │
│  │   └─> id, filename, filepath, guest_token, upload_time       │
│  │                                                                │
│  ├── Result.java                                                 │
│  │   └─> id, submission_id, similarity_score, status,           │
│  │       matched_segments, created_at                            │
│  │                                                                │
│  └── DatabaseUtils.java                                          │
│      ├─> getConnection()       (DB connection pool)             │
│      ├─> saveSubmission()      (Insert submission)              │
│      ├─> saveResult()          (Store result)                   │
│      ├─> getDocuments()        (Query documents)                │
│      └─> deleteDocument()      (Remove document)                │
└─────────────────┬───────────────────────────────────────────────┘
                  │
                  │ JDBC Queries
                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                     DATABASE LAYER                               │
│                       (MySQL)                                    │
├─────────────────────────────────────────────────────────────────┤
│  Database: plagiarism_checker                                    │
│                                                                   │
│  Tables:                                                         │
│  ├── Users                                                       │
│  │   ├─ id (PK, AUTO_INCREMENT)                                 │
│  │   ├─ username (UNIQUE)                                       │
│  │   ├─ password (hashed)                                       │
│  │   ├─ role (ENUM: 'admin', 'user')                            │
│  │   └─ created_at (TIMESTAMP)                                  │
│  │                                                                │
│  ├── Documents                                                   │
│  │   ├─ id (PK, AUTO_INCREMENT)                                 │
│  │   ├─ owner_id (FK -> Users.id)                               │
│  │   ├─ filename                                                 │
│  │   ├─ filepath                                                 │
│  │   ├─ filesize (BIGINT)                                       │
│  │   ├─ mime_type                                                │
│  │   └─ upload_time (TIMESTAMP)                                 │
│  │                                                                │
│  ├── Submissions                                                 │
│  │   ├─ id (PK, AUTO_INCREMENT)                                 │
│  │   ├─ filename                                                 │
│  │   ├─ filepath                                                 │
│  │   ├─ guest_token (for anonymous users)                       │
│  │   ├─ status (ENUM: 'pending', 'processing', 'completed')    │
│  │   └─ upload_time (TIMESTAMP)                                 │
│  │                                                                │
│  └── Results                                                     │
│      ├─ id (PK, AUTO_INCREMENT)                                 │
│      ├─ submission_id (FK -> Submissions.id)                    │
│      ├─ similarity_winnowing (DECIMAL)                          │
│      ├─ similarity_tfidf (DECIMAL)                              │
│      ├─ matched_segments (JSON)                                 │
│      ├─ status (ENUM: 'original', 'suspected', 'cited')        │
│      └─ created_at (TIMESTAMP)                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Request Flow Diagrams

### 1️⃣ Public User Flow (Anonymous Plagiarism Check)

```
┌──────────┐
│  User    │
│ (Client) │
└─────┬────┘
      │
      │ 1. Upload file(s) via upload.jsp
      ▼
┌─────────────────┐
│ UploadServlet   │
│   /upload       │
└────┬────────────┘
     │
     │ 2. Validate files (size, format)
     ▼
┌────────────────┐     3. Save to disk
│  FileParser    │────────────────────────┐
│  (Utils)       │                        │
└────┬───────────┘                        ▼
     │                              ┌────────────┐
     │ 4. Extract text              │ File System│
     ▼                              │ /documents │
┌────────────────┐                  └────────────┘
│ TextCleaner    │
│ (Utils)        │
└────┬───────────┘
     │
     │ 5. Clean & normalize
     ▼
┌──────────────────┐
│ DatabaseUtils    │
│ saveSubmission() │
└────┬─────────────┘
     │
     │ 6. Insert into Submissions table
     ▼
┌─────────────┐
│  Database   │
│ (MySQL)     │
└─────────────┘
     │
     │ 7. Queue for processing
     ▼
┌──────────────────┐
│   QueueWorker    │◄────────────────┐
│ (Background      │                 │
│  Thread)         │                 │
└────┬─────────────┘                 │
     │                               │
     │ 8. Process sequentially       │
     ▼                               │
┌──────────────────┐                 │
│ Winnowing.java   │                 │
│ TFIDF.java       │                 │
│ CitationDetector │                 │
└────┬─────────────┘                 │
     │                               │
     │ 9. Compare with Documents     │
     │    in database                │
     ▼                               │
┌──────────────────┐                 │
│ saveResult()     │                 │
│ (DatabaseUtils)  │                 │
└────┬─────────────┘                 │
     │                               │
     │ 10. Store results             │
     ▼                               │
┌─────────────┐                      │
│  Database   │                      │
│  Results    │                      │
└─────────────┘                      │
     │                               │
     │ 11. Notify completion         │
     └───────────────────────────────┘
     │
     │ 12. User visits result page
     ▼
┌──────────────────┐
│ ResultServlet    │
│ /result?id=xxx   │
└────┬─────────────┘
     │
     │ 13. Fetch results from DB
     ▼
┌─────────────┐
│ result.jsp  │
│ (View)      │
│             │
│ - Pie Chart │
│ - %Original │
│ - %Copied   │
│ - Segments  │
└─────────────┘
```

---

### 2️⃣ Admin Flow (Document Management)

```
┌──────────┐
│  Admin   │
└─────┬────┘
      │
      │ 1. Login via adminLogin.jsp
      ▼
┌─────────────────┐
│ LoginServlet    │
│   /login        │
└────┬────────────┘
     │
     │ 2. Authenticate credentials
     ▼
┌────────────────┐
│ DatabaseUtils  │
│ authenticate() │
└────┬───────────┘
     │
     │ 3. Query Users table
     ▼
┌─────────────┐
│  Database   │
└─────────────┘
     │
     │ 4. Create session
     ▼
┌───────────────────┐
│ Session           │
│ adminId=1         │
│ adminLoggedIn=true│
└────┬──────────────┘
     │
     │ 5. Redirect to dashboard
     ▼
┌─────────────────────────┐
│ AdminDashboardServlet   │
│ /admin/dashboard        │
└────┬────────────────────┘
     │
     │ 6. Fetch documents
     ▼
┌────────────────┐
│ DatabaseUtils  │
│ getDocuments() │
└────┬───────────┘
     │
     │ 7. Query Documents table
     ▼
┌─────────────┐
│  Database   │
└─────────────┘
     │
     │ 8. Display in dashboard
     ▼
┌────────────────────┐
│ adminDashboard.jsp │
│                    │
│ - Document List    │
│ - Statistics       │
│ - Upload Button    │
│ - Delete Actions   │
└────────────────────┘
```

---

### 3️⃣ Admin Upload Flow

```
┌──────────┐
│  Admin   │
└─────┬────┘
      │
      │ 1. Upload reference docs
      ▼
┌──────────────────────┐
│ adminUpload.jsp      │
│ (Drag & Drop UI)     │
└────┬─────────────────┘
     │
     │ 2. POST files
     ▼
┌──────────────────────┐
│ AdminUploadServlet   │
│ /adminUpload         │
└────┬─────────────────┘
     │
     │ 3. Validate (no size limit for admin)
     ▼
┌────────────────┐
│  FileParser    │
└────┬───────────┘
     │
     │ 4. Extract & save
     ▼
┌────────────────┐
│ DatabaseUtils  │
│ saveDocument() │
└────┬───────────┘
     │
     │ 5. Insert into Documents
     ▼
┌─────────────┐
│  Database   │
└─────────────┘
     │
     │ 6. Show success
     ▼
┌────────────────────┐
│ adminDashboard.jsp │
│ "Upload Success!"  │
└────────────────────┘
```

---

## 📦 Package Structure

```
src/
├── main/
│   ├── java/
│   │   ├── controller/          ← CONTROLLER LAYER
│   │   │   ├── UploadServlet.java
│   │   │   ├── ResultServlet.java
│   │   │   ├── QueueWorker.java
│   │   │   ├── LoginServlet.java
│   │   │   ├── AdminDashboardServlet.java
│   │   │   ├── AdminUploadServlet.java
│   │   │   ├── AdminViewServlet.java
│   │   │   ├── AdminDownloadServlet.java
│   │   │   └── AdminDeleteServlet.java
│   │   │
│   │   ├── model/               ← MODEL LAYER
│   │   │   ├── User.java
│   │   │   ├── Submission.java
│   │   │   ├── Result.java
│   │   │   └── DatabaseUtils.java
│   │   │
│   │   └── utils/               ← BUSINESS LOGIC LAYER
│   │       ├── FileParser.java
│   │       ├── TextCleaner.java
│   │       ├── Winnowing.java
│   │       ├── TFIDF.java
│   │       ├── CitationDetector.java
│   │       ├── SimilarityService.java
│   │       └── TextSimilarity.java
│   │
│   └── webapp/                  ← VIEW LAYER
│       ├── jsp/
│       │   ├── upload.jsp          (Public upload)
│       │   ├── result.jsp          (Results display)
│       │   ├── adminLogin.jsp      (Admin login)
│       │   ├── adminDashboard.jsp  (Admin panel)
│       │   ├── adminUpload.jsp     (Reference upload)
│       │   └── viewSubmission.jsp  (Document details)
│       │
│       ├── css/
│       │   └── style.css
│       │
│       ├── documents/              (Uploaded files)
│       │
│       └── WEB-INF/
│           └── web.xml             (Servlet mappings)
```

---

## 🔐 Security & Access Control

### Public Routes (No Authentication)
- `/upload` - Upload files for plagiarism check
- `/result?id=xxx` - View results (with guest token)

### Protected Routes (Admin Only)
- `/login` - Admin authentication
- `/admin/dashboard` - Document repository
- `/adminUpload` - Upload reference documents
- `/admin/view?id=xxx` - View document details
- `/admin/download?id=xxx` - Download document
- `/admin/delete` - Delete documents

---

## 🎯 Key Features

### For Anonymous Users
✅ No registration required  
✅ Upload up to 25MB total  
✅ Support PDF, DOCX, TXT  
✅ Instant plagiarism detection  
✅ Detailed visual reports  

### For Admins
✅ Secure login system  
✅ CRUD document repository  
✅ Unlimited upload size  
✅ View document statistics  
✅ Download/Delete capabilities  

---

## 🧮 Plagiarism Detection Algorithms

### 1. Winnowing (N-Gram Fingerprinting)
- **Purpose**: Detect exact/near-exact matches
- **Method**: 
  - Create n-grams (k=5 or 6 words)
  - Compute rolling hash
  - Select minimum hash in window (w=4)
  - Compare fingerprints
- **Output**: Matched segments, similarity %

### 2. TF-IDF + Cosine Similarity
- **Purpose**: Measure overall document similarity
- **Method**:
  - Compute term frequency (TF)
  - Compute inverse document frequency (IDF)
  - Build TF-IDF vectors
  - Calculate cosine similarity
- **Output**: Overall similarity score (0-1)

### 3. Citation Detection
- **Purpose**: Distinguish proper citations from plagiarism
- **Method**:
  - Detect APA: (Author, Year)
  - Detect IEEE: [Number]
  - Detect URLs: http://, www.
- **Output**: "Properly Cited" or "Plagiarism Suspected"

---

## 📊 Database Schema

```sql
-- Users Table
CREATE TABLE Users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'user') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Documents Table (Admin uploads)
CREATE TABLE Documents (
    id INT PRIMARY KEY AUTO_INCREMENT,
    owner_id INT,
    filename VARCHAR(255) NOT NULL,
    filepath VARCHAR(500) NOT NULL,
    filesize BIGINT,
    mime_type VARCHAR(100),
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES Users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_filename (owner_id, filename)
);

-- Submissions Table (Public uploads)
CREATE TABLE Submissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    filepath VARCHAR(500) NOT NULL,
    guest_token VARCHAR(64),
    status ENUM('pending', 'processing', 'completed', 'error') DEFAULT 'pending',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_guest_token (guest_token)
);

-- Results Table
CREATE TABLE Results (
    id INT PRIMARY KEY AUTO_INCREMENT,
    submission_id INT,
    similarity_winnowing DECIMAL(5,2),
    similarity_tfidf DECIMAL(5,2),
    matched_segments JSON,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE
);
```

---

## 🚀 Deployment Workflow

1. **Build**: `mvn clean package`
2. **Deploy**: Copy `.war` to Tomcat `webapps/`
3. **Database**: Run `schema.sql` on MySQL
4. **Config**: Update DB credentials in `DatabaseUtils.java`
5. **Start**: Launch Tomcat server
6. **Access**: 
   - Public: `http://localhost:8080/PlagiarismChecker/upload`
   - Admin: `http://localhost:8080/PlagiarismChecker/login`

---

## 📝 Technologies Used

- **Frontend**: JSP, HTML5, CSS3, JavaScript
- **Backend**: Java Servlets, Jakarta EE
- **Database**: MySQL 8.0
- **Libraries**: 
  - Apache PDFBox (PDF parsing)
  - Apache POI (DOCX parsing)
  - MySQL Connector/J (JDBC)
- **Server**: Apache Tomcat 10.x

---

**Last Updated**: November 2025  
**Project**: Plagiarism Checker System  
**Architecture**: MVC (Model-View-Controller)

