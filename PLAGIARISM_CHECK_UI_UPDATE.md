# Cập nhật Giao diện Kiểm tra Đạo văn

## Tóm tắt Thay đổi

### 1. Sửa lỗi JSTL Taglib (Jakarta EE)
- **Vấn đề**: Lỗi `Cannot resolve taglib with uri jakarta.tags.core`
- **Giải pháp**: 
  - Cập nhật `pom.xml` để sử dụng Jakarta JSTL API 3.0.1
  - Thay đổi tất cả JSP files từ `http://java.sun.com/jsp/jstl/core` sang `jakarta.tags.core`
  - Tương thích với Tomcat 10+

### 2. Cải thiện Logic Kiểm tra Đạo văn

#### QueueWorker.java
- **Trước**: Mỗi submission so sánh tạo nhiều kết quả riêng lẻ trong bảng Results
- **Sau**: Tổng hợp tất cả các so sánh thành 1 kết quả duy nhất
  - Tìm độ tương đồng cao nhất (maxSimilarity)
  - Thu thập tất cả matched segments vào một JSON array
  - Chỉ lưu 1 record trong Results table cho mỗi submission

#### TextSimilarity.java
- Thêm method `findMatchedSegments()`:
  - Tìm các câu giống nhau giữa 2 văn bản
  - Ngưỡng similarity > 60% cho exact match
  - Trả về JSON format với text, source, similarity, hasCitation

### 3. Giao diện Kết quả Mới (result.jsp)

#### Tính năng chính:
✅ **Biểu đồ tròn đẹp mắt** (như trong hình mẫu):
   - Hiển thị % Độc nhất (màu xanh lá #9BCF53)
   - Hiển thị % Exact match (màu đỏ #FF6B6B)  
   - Hiển thị % Partial match (màu xanh dương #4ECDC4)
   - Animation khi load

✅ **Card layout responsive**:
   - Grid layout tự động điều chỉnh
   - Hover effect đẹp mắt
   - Box shadow và border radius

✅ **Status badges**:
   - 🟢 "No Issues" - màu xanh
   - 🟡 "Partial Match" - màu vàng
   - 🔴 "Plagiarism Suspected" - màu đỏ

✅ **Congratulations section**:
   - Hiển thị khi không có đạo văn
   - Message "Plagiarism not found!"

✅ **Matched segments**:
   - Hiển thị các đoạn văn bản giống nhau
   - Source document reference
   - Similarity score chi tiết

### 4. Các File được Cập nhật

```
WebContent/jsp/
├── result.jsp              ✅ Hoàn toàn mới - biểu đồ tròn + UI đẹp
├── adminDashboard.jsp      ✅ Taglib Jakarta
├── login.jsp               ✅ Taglib Jakarta
├── viewSubmission.jsp      ✅ Taglib Jakarta
└── upload.jsp              ✅ Taglib Jakarta

src/controller/
└── QueueWorker.java        ✅ Logic tổng hợp kết quả

src/utils/
└── TextSimilarity.java     ✅ Method findMatchedSegments()

pom.xml                     ✅ Jakarta JSTL 3.0.1
```

## Công thức Tính toán

### Tỷ lệ hiển thị:
```
Độc nhất (Unique) = 100% - (Similarity * 100%)
Exact Match = Similarity * 100%
Partial Match = 0% (sẽ được cải thiện với TF-IDF)
```

### Status logic:
```java
if (similarity > 0.5)      → "Plagiarism Suspected"
else if (similarity > 0.2) → "Partial Match"
else                       → "No Issues"
```

## Cách Sử dụng

### 1. Build Project
```bash
cd E:\PBL4\PlagiarismChecker
mvnw.cmd clean package
```

### 2. Deploy to Tomcat 10+
- Copy file `target/PlagiarismChecker-1.0-SNAPSHOT.war` vào `TOMCAT_HOME/webapps/`
- Start Tomcat
- Truy cập: `http://localhost:8080/PlagiarismChecker-1.0-SNAPSHOT/upload`

### 3. Test
1. Upload 1 file → Kết quả sẽ hiển thị "No Issues" nếu không có file nào để so sánh
2. Upload file thứ 2 → Hệ thống so sánh với file đầu tiên
3. Xem kết quả với biểu đồ tròn đẹp mắt

## Kết quả Mong đợi

### Khi không có đạo văn:
- Biểu đồ tròn màu xanh lá 100%
- Badge "✓ No Issues" màu xanh
- "Congratulations - Plagiarism not found!"

### Khi có đạo văn:
- Biểu đồ hiển thị tỷ lệ Độc nhất (ví dụ: 37% nếu có 63% đạo văn)
- Badge "⚠ Plagiarism Suspected" màu đỏ
- Section "Xem các nguồn đạo văn" hiển thị chi tiết

## Compatibility

✅ Tomcat 10.x
✅ Java 8+
✅ Jakarta EE 9+
✅ MySQL 8.x
✅ Modern browsers (Chrome, Firefox, Edge)

## Next Steps

### Cải thiện trong tương lai:
1. Implement TF-IDF để tính Partial Match chính xác hơn
2. Thêm Winnowing algorithm cho fingerprint matching
3. Citation detection để phân biệt trích dẫn hợp lệ
4. Export PDF report
5. Batch comparison với nhiều files cùng lúc

## Support

Nếu gặp lỗi:
1. Kiểm tra Tomcat version >= 10.0
2. Đảm bảo MySQL đang chạy
3. Verify database schema đã được tạo
4. Check logs tại `TOMCAT_HOME/logs/catalina.out`

---
**Phiên bản**: 2.0
**Ngày cập nhật**: 21/11/2025
**Tương thích**: Tomcat 10+, Jakarta EE 9+

