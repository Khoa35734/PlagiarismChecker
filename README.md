# 🎓 Plagiarism Checker - Hệ thống Kiểm tra Đạo văn

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]()
[![Java Version](https://img.shields.io/badge/Java-8+-blue)]()
[![Tomcat](https://img.shields.io/badge/Tomcat-10+-orange)]()
[![License](https://img.shields.io/badge/license-MIT-green)]()

## 🌟 Tính năng chính

### Cho Người dùng
- ✅ **Upload không cần đăng nhập** - Kiểm tra đạo văn ngay lập tức
- ✅ **Hỗ trợ nhiều định dạng** - PDF, DOCX, TXT (max 25MB)
- ✅ **Kết quả trực quan** - Biểu đồ tròn đẹp mắt hiển thị tỷ lệ độc nhất
- ✅ **Chi tiết matched segments** - Xem chính xác đoạn nào bị trùng lặp

### Cho Admin
- 🔐 **Đăng nhập bảo mật** - Quản lý tài liệu riêng tư
- 📚 **CRUD tài liệu** - Upload, View, Download, Delete
- 📊 **Dashboard quản lý** - Theo dõi tất cả submissions
- 🔍 **Tìm kiếm nâng cao** - Filter theo ngày, status, filename

## 🚀 Quick Start

### 1. Chuẩn bị Database
```sql
mysql -u root -p
CREATE DATABASE plagiarism_checker;
USE plagiarism_checker;
source database/schema.sql;
```

### 2. Build & Deploy
```bash
cd E:\PBL4\PlagiarismChecker
mvnw.cmd clean package
# Copy file WAR vào Tomcat webapps
copy target\PlagiarismChecker-1.0-SNAPSHOT.war C:\Tomcat\webapps\
```

### 3. Truy cập
- User: http://localhost:8080/PlagiarismChecker-1.0-SNAPSHOT/upload
- Admin: http://localhost:8080/PlagiarismChecker-1.0-SNAPSHOT/admin/login
  - Username: `admin`
  - Password: `admin123`

## 📊 Giao diện Kết quả

### Khi không có đạo văn (100% Độc nhất)
![No Plagiarism](https://via.placeholder.com/400x300/9BCF53/FFFFFF?text=100%25+Original)

### Khi có đạo văn (37% Exact match)
![Plagiarism Found](https://via.placeholder.com/400x300/FF6B6B/FFFFFF?text=37%25+Plagiarism)

## 🔧 Công nghệ sử dụng

### Backend
- **Java 8+** - Core language
- **Jakarta Servlet 6.1** - Web framework
- **MySQL 8.x** - Database
- **Apache POI 5.2.5** - DOCX parsing
- **Apache PDFBox 3.0.2** - PDF parsing

### Frontend
- **JSP + JSTL** - View template
- **CSS3** - Styling with animations
- **SVG** - Circular progress charts

### Build & Deploy
- **Maven** - Dependency management
- **Apache Tomcat 10+** - Application server

## 📁 Cấu trúc Project

```
PlagiarismChecker/
├── src/
│   ├── controller/          # Servlets
│   │   ├── UploadServlet.java
│   │   ├── ResultServlet.java
│   │   ├── QueueWorker.java
│   │   ├── AdminDashboardServlet.java
│   │   └── ...
│   ├── model/              # Data models
│   │   ├── DatabaseUtils.java
│   │   ├── Submission.java
│   │   └── Result.java
│   └── utils/              # Utilities
│       ├── TextSimilarity.java
│       ├── FileParser.java
│       └── TextCleaner.java
├── WebContent/
│   ├── jsp/                # View templates
│   │   ├── result.jsp      ← Biểu đồ tròn đẹp!
│   │   ├── upload.jsp
│   │   └── ...
│   └── css/
│       └── style.css
├── database/
│   └── schema.sql          # Database schema
├── pom.xml                 # Maven config
└── README.md               # This file
```

## 🎯 Thuật toán Kiểm tra

### Hiện tại (v2.0)
1. **Text Extraction** - Parse PDF/DOCX/TXT
2. **Text Cleaning** - Normalize, lowercase, remove special chars
3. **Jaccard Similarity** - Token-based comparison
4. **Sentence Matching** - Find exact matched segments

### Tương lai (v3.0)
1. **Winnowing Algorithm** - N-gram fingerprinting
2. **TF-IDF + Cosine Similarity** - Semantic comparison
3. **Citation Detection** - Identify proper citations
4. **Cross-language Support** - Vietnamese + English

## 📈 Kết quả Demo

| File | Size | Độc nhất | Exact | Status |
|------|------|----------|-------|--------|
| Essay1.docx | 1.2 MB | 100% | 0% | ✅ No Issues |
| Report2.pdf | 850 KB | 63% | 37% | ⚠️ Plagiarism Suspected |
| Thesis3.txt | 2.5 MB | 85% | 15% | ⚠️ Partial Match |

## 🔐 Security

- ✅ Prepared statements (SQL injection prevention)
- ✅ Session management
- ✅ Admin authentication
- ✅ File type validation
- ✅ Size limit enforcement (25MB)

## 🐛 Known Issues

- [ ] Partial similarity calculation cần TF-IDF
- [ ] Citation detection chưa implement
- [ ] Multi-language support limited

## 📚 Documentation

- [📖 Deployment Guide](DEPLOYMENT_GUIDE.md)
- [🔄 UI Update Notes](PLAGIARISM_CHECK_UI_UPDATE.md)
- [🗄️ Database Schema](database/schema.sql)

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Team

- **Backend Developer** - Servlet, Database, Algorithms
- **Frontend Developer** - JSP, CSS, UI/UX
- **QA Engineer** - Testing, Bug fixing

## 📞 Contact

- 📧 Email: support@plagiarismchecker.com
- 🌐 Website: https://plagiarismchecker.com
- 💬 Discord: https://discord.gg/plagiarism-checker

## 🙏 Acknowledgments

- Apache POI team for DOCX parsing
- Apache PDFBox team for PDF parsing
- Bootstrap community for design inspiration
- Stack Overflow community for solutions

---

**Made with ❤️ by PBL4 Team**

⭐ Star us on GitHub if you find this useful!

