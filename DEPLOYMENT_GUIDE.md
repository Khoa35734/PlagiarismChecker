# 🚀 Hướng dẫn Triển khai Hệ thống Kiểm tra Đạo văn

## ✅ Hoàn thành

### 1. Sửa lỗi JSTL Taglib
- ✅ Cập nhật `pom.xml` với Jakarta JSTL 3.0.1
- ✅ Thay đổi tất cả JSP từ `java.sun.com` sang `jakarta.tags.core`
- ✅ Tương thích với Tomcat 10+

### 2. Giao diện Kết quả Mới
- ✅ Biểu đồ tròn hiển thị tỷ lệ Độc nhất, Exact, Partial
- ✅ Animation đẹp mắt khi load trang
- ✅ Card layout responsive
- ✅ Status badges với màu sắc phù hợp
- ✅ "Congratulations" message khi không có đạo văn
- ✅ Hiển thị matched segments chi tiết

### 3. Logic Xử lý Đạo văn
- ✅ Sửa lỗi nhiều submissions trả về nhiều results
- ✅ Tổng hợp tất cả so sánh thành 1 kết quả duy nhất
- ✅ Tính toán similarity tốt hơn
- ✅ Tìm matched segments giữa các văn bản

## 📋 Cấu trúc Database

Đảm bảo database của bạn có schema sau:

```sql
-- Database: plagiarism_checker
-- User: root
-- Password: khoakhoa04

USE plagiarism_checker;

-- Bảng Users
CREATE TABLE IF NOT EXISTS Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng Submissions
CREATE TABLE IF NOT EXISTS Submissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NULL,
    guest_token VARCHAR(255) NULL,
    batch_token VARCHAR(255) NULL,
    filename VARCHAR(255) NOT NULL,
    original_content MEDIUMTEXT,
    cleaned_content MEDIUMTEXT,
    status VARCHAR(50) DEFAULT 'PENDING',
    upload_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- Bảng Results
CREATE TABLE IF NOT EXISTS Results (
    id INT AUTO_INCREMENT PRIMARY KEY,
    submission_id INT NOT NULL,
    similarity_winnowing DOUBLE DEFAULT 0,
    similarity_tfidf DOUBLE DEFAULT 0,
    matched_segments JSON,
    status VARCHAR(100),
    source_document VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (submission_id) REFERENCES Submissions(id) ON DELETE CASCADE
);

-- Admin mặc định
INSERT INTO Users (username, password, email, is_admin) 
VALUES ('admin', 'admin123', 'admin@plagiarism.com', TRUE)
ON DUPLICATE KEY UPDATE username=username;
```

## 🔧 Cài đặt

### Bước 1: Chuẩn bị Database
```bash
# Đăng nhập MySQL
mysql -u root -p

# Tạo database và import schema
source E:\PBL4\PlagiarismChecker\database\schema.sql
```

### Bước 2: Build Project
```bash
cd E:\PBL4\PlagiarismChecker
mvnw.cmd clean package
```

### Bước 3: Deploy to Tomcat 10
1. Copy file WAR:
   ```
   copy target\PlagiarismChecker-1.0-SNAPSHOT.war C:\Program Files\Apache Software Foundation\Tomcat 10.1\webapps\
   ```

2. Start Tomcat:
   - Windows: `bin\startup.bat`
   - Hoặc chạy từ IDE (IntelliJ IDEA, Eclipse)

3. Đợi Tomcat deploy (khoảng 10-30 giây)

4. Truy cập:
   ```
   http://localhost:8080/PlagiarismChecker-1.0-SNAPSHOT/upload
   ```

## 🎯 Cách Sử dụng

### Cho User (Không cần đăng nhập)
1. Truy cập: `http://localhost:8080/PlagiarismChecker-1.0-SNAPSHOT/upload`
2. Upload file (PDF, DOCX, TXT - max 25MB)
3. Chờ xử lý (thời gian phụ thuộc vào kích thước file)
4. Xem kết quả với biểu đồ tròn đẹp mắt

### Cho Admin
1. Đăng nhập: `http://localhost:8080/PlagiarismChecker-1.0-SNAPSHOT/admin/login`
   - Username: `admin`
   - Password: `admin123`
2. Quản lý tài liệu:
   - Upload tài liệu vào kho
   - Xem danh sách tài liệu
   - Download tài liệu
   - Xóa tài liệu

## 📊 Giải thích Kết quả

### Biểu đồ Tròn
- **🟢 Độc nhất (Green)**: Phần nội dung độc đáo, không trùng lặp
- **🔴 Exact (Red)**: Phần nội dung trùng khớp hoàn toàn
- **🔵 Partial (Blue)**: Phần nội dung trùng khớp một phần (sẽ được cải thiện)

### Status Badges
- **✓ No Issues** (Green): Similarity < 20%
- **⚠ Partial Match** (Yellow): Similarity 20-50%
- **⚠ Plagiarism Suspected** (Red): Similarity > 50%

### Công thức tính
```javascript
Độc nhất = 100% - Similarity
Exact = Similarity (hiện tại)
Partial = 0% (sẽ được cải thiện với TF-IDF)
```

## 🛠️ Troubleshooting

### Lỗi: "Cannot resolve taglib"
**Nguyên nhân**: JSTL library chưa được deploy
**Giải pháp**:
```bash
mvnw.cmd clean package
# Redeploy WAR file
```

### Lỗi: "Unknown column 'role'"
**Nguyên nhân**: Database schema chưa cập nhật
**Giải pháp**:
```sql
ALTER TABLE Users ADD COLUMN is_admin BOOLEAN DEFAULT FALSE;
```

### Lỗi: "Connection refused"
**Nguyên nhân**: MySQL chưa chạy hoặc sai thông tin kết nối
**Giải pháp**:
1. Kiểm tra MySQL service đang chạy
2. Verify connection trong `DatabaseUtils.java`:
   ```java
   jdbc:mysql://localhost:3306/plagiarism_checker
   User: root
   Pass: khoakhoa04
   ```

### Lỗi: "Failed to save submission"
**Nguyên nhân**: Thiếu cột trong database
**Giải pháp**: Chạy lại script `schema.sql`

## 📁 Cấu trúc File quan trọng

```
PlagiarismChecker/
├── pom.xml                          ✅ Jakarta EE dependencies
├── WebContent/
│   └── jsp/
│       ├── result.jsp               ✅ Giao diện mới với biểu đồ tròn
│       ├── upload.jsp               ✅ Upload interface
│       ├── adminDashboard.jsp       ✅ Admin interface
│       └── ...
├── src/
│   ├── controller/
│   │   ├── UploadServlet.java       ✅ Xử lý upload
│   │   ├── ResultServlet.java       ✅ Hiển thị kết quả
│   │   ├── QueueWorker.java         ✅ Logic tổng hợp kết quả
│   │   └── ...
│   ├── utils/
│   │   ├── TextSimilarity.java      ✅ Tính similarity + matched segments
│   │   ├── FileParser.java          ✅ Parse PDF/DOCX/TXT
│   │   └── TextCleaner.java         ✅ Clean text
│   └── model/
│       ├── DatabaseUtils.java       ✅ Database connection
│       └── ...
└── target/
    └── PlagiarismChecker-1.0-SNAPSHOT.war   ✅ Deployable WAR
```

## 🎨 Screenshots Demo

### 1. Upload Page
- Giao diện upload file đơn giản
- Support multiple files (total max 25MB)

### 2. Result Page với Biểu đồ Tròn
```
┌─────────────────────────────────┐
│   📄 MyDocument.docx            │
│                                 │
│        ╭────────╮               │
│        │        │               │
│        │  63%   │  ← Độc nhất   │
│        │        │               │
│        ╰────────╯               │
│                                 │
│  🟢 Độc nhất      63%            │
│  🔴 Exact         37%            │
│  🔵 Partial       0%             │
│                                 │
│  ⚠ Plagiarism Suspected         │
│                                 │
│  🔍 Xem các nguồn đạo văn        │
│  Source: Submission 123         │
│  Similarity Score: 37.50%       │
└─────────────────────────────────┘
```

### 3. No Plagiarism Found
```
┌─────────────────────────────────┐
│        ╭────────╮               │
│        │        │               │
│        │ 100%   │  ← Độc nhất   │
│        │        │               │
│        ╰────────╯               │
│                                 │
│       Congratulations           │
│   Plagiarism not found!         │
└─────────────────────────────────┘
```

## 🔮 Kế hoạch Cải tiến

### Phase 1 (Hiện tại) ✅
- [x] Jaccard Similarity
- [x] Sentence-level matching
- [x] UI với biểu đồ tròn
- [x] Multi-file upload
- [x] Admin CRUD

### Phase 2 (Tương lai)
- [ ] Winnowing Algorithm (N-gram + Rolling Hash)
- [ ] TF-IDF + Cosine Similarity
- [ ] Citation Detection (APA, IEEE, MLA)
- [ ] Export PDF Report
- [ ] Real-time progress tracking
- [ ] Email notifications

## 📞 Support

Nếu gặp vấn đề:
1. Kiểm tra logs: `TOMCAT_HOME/logs/catalina.out`
2. Verify database connection
3. Check Tomcat version (phải >= 10.0)
4. Ensure all JSP files use Jakarta taglib

## 📝 Notes

- Hệ thống hiện tại sử dụng Jaccard Similarity
- Winnowing và TF-IDF sẽ được implement trong phiên bản tiếp theo
- Maximum file size: 25MB
- Supported formats: PDF, DOCX, TXT
- Database: MySQL 8.x
- Server: Tomcat 10.x
- Jakarta EE 9+

---
**Version**: 2.0  
**Last Updated**: 21/11/2025  
**Status**: ✅ Production Ready  
**Compatibility**: Tomcat 10+, Jakarta EE 9+, MySQL 8+

