# Checklist kiểm tra Admin Dashboard và Upload

## 1. Kiểm tra Database
- [ ] Database `plagiarism_checker` đã được tạo
- [ ] Bảng `Users` có user admin với role='ADMIN'
- [ ] Bảng `Documents` tồn tại và có đúng cấu trúc
- [ ] Foreign key giữa Documents và Users hoạt động

### SQL để kiểm tra:
```sql
USE plagiarism_checker;

-- Kiểm tra user admin
SELECT * FROM Users WHERE role = 'ADMIN';

-- Kiểm tra documents hiện có
SELECT d.*, u.username as owner_name 
FROM Documents d 
JOIN Users u ON d.owner_id = u.id 
WHERE u.role = 'ADMIN' 
ORDER BY d.upload_time DESC;

-- Đếm số documents
SELECT COUNT(*) as total_docs, SUM(filesize) as total_size 
FROM Documents d 
JOIN Users u ON d.owner_id = u.id 
WHERE u.role = 'ADMIN';
```

## 2. Kiểm tra Build
- [x] Maven build thành công (BUILD SUCCESS)
- [x] WAR file được tạo trong target/
- [ ] Deploy WAR vào Tomcat
- [ ] Tomcat khởi động không lỗi

### Command để build:
```cmd
cd /d E:\PBL4\PlagiarismChecker
mvnw.cmd clean package -DskipTests
```

## 3. Kiểm tra Login
- [ ] Truy cập: http://localhost:8080/PlagiarismChecker/login
- [ ] Login với username: `admin`, password: `admin123`
- [ ] Redirect về `/admin` hoặc `/admin/dashboard`
- [ ] Session adminId được set

## 4. Kiểm tra Admin Dashboard
- [ ] URL: http://localhost:8080/PlagiarismChecker/admin/dashboard
- [ ] Hiển thị danh sách TẤT CẢ file đã upload bởi admin
- [ ] Hiển thị đúng các cột: ID, Filename, Owner, Size, Upload Date, Actions
- [ ] Hiển thị đúng Total Documents
- [ ] Hiển thị đúng Total Storage (MB)
- [ ] Nút "Upload Document" hoạt động
- [ ] Nút "Delete" hiển thị confirm dialog
- [ ] Link download file hoạt động

### Kiểm tra nếu chưa có file:
- [ ] Hiển thị empty state "No Reference Documents Yet"
- [ ] Nút "Upload First Document" hoạt động

## 5. Kiểm tra Admin Upload Page
- [ ] URL: http://localhost:8080/PlagiarismChecker/adminUpload
- [ ] Nút "Upload Documents" bị disable ban đầu
- [ ] Click "Click to browse" mở file picker
- [ ] Accept chỉ .pdf, .docx, .txt files

### A. Kiểm tra chọn file:
- [ ] Chọn 1 file → hiển thị trong danh sách
- [ ] Chọn nhiều file → hiển thị tất cả
- [ ] Hiển thị đúng tên file và size (KB)
- [ ] Hiển thị Total Size / 25 MB
- [ ] Màu xanh nếu ≤ 25MB
- [ ] Màu đỏ + thông báo lỗi nếu > 25MB
- [ ] Nút "Upload Documents" enable khi có file và ≤ 25MB

### B. Kiểm tra xóa file:
- [ ] Nút ✕ màu đỏ hiển thị bên mỗi file
- [ ] Click ✕ → file bị xóa khỏi danh sách
- [ ] Total Size được cập nhật lại
- [ ] Nếu xóa hết file → nút "Upload Documents" disable

### C. Kiểm tra tiến trình upload:
- [ ] Click "Upload Documents" → hiển thị progress bar
- [ ] Nút "Upload Documents" bị disable
- [ ] Hiển thị text: "Uploading file 1/X: filename"
- [ ] Progress bar chạy từ 0% → 100% cho file đầu tiên
- [ ] Sau khi file 1 xong → chuyển sang file 2
- [ ] Hiển thị Overall progress
- [ ] Hiển thị đúng số file đã upload / tổng số file

### D. Kiểm tra kết quả upload:
- [ ] Tất cả file upload xong → progress bar màu xanh
- [ ] Hiển thị "All files uploaded successfully!"
- [ ] Hiển thị thông báo success với số file đã upload
- [ ] Sau 1.5 giây tự động redirect về dashboard
- [ ] Dashboard hiển thị các file mới upload

### E. Kiểm tra xử lý lỗi:
- [ ] Upload file quá 25MB → thông báo lỗi
- [ ] Upload file không đúng định dạng → thông báo lỗi
- [ ] Mất kết nối trong lúc upload → thông báo "Network error"
- [ ] Server error → progress bar màu đỏ + thông báo lỗi

## 6. Kiểm tra tích hợp với plagiarism check
- [ ] File được lưu vào database
- [ ] File được lưu vào thư mục documents/
- [ ] QueueWorker có thể đọc file từ documents/
- [ ] Plagiarism check so sánh với file trong Documents
- [ ] Kết quả hiển thị đúng source document

### Test flow:
1. Admin upload file reference (e.g., "textbook.pdf")
2. User upload file để check
3. Kết quả hiển thị % giống với "textbook.pdf"

## 7. Kiểm tra bảo mật
- [ ] Không có session admin → redirect về login
- [ ] URL /admin/* không truy cập được khi chưa login
- [ ] Upload servlet kiểm tra adminId
- [ ] Dashboard servlet kiểm tra adminId
- [ ] SQL injection prevention (PreparedStatement)

## 8. Kiểm tra UI/UX
- [ ] Responsive trên các kích thước màn hình
- [ ] Hover effect trên các nút
- [ ] Icon hiển thị đúng (📤, 🗑️, ✕, 📕, 📘, 📄)
- [ ] Progress bar animation mượt
- [ ] Transition effect khi thêm/xóa file
- [ ] Màu sắc phù hợp (xanh = success, đỏ = error)

## 9. Performance
- [ ] Upload nhiều file nhỏ (5-10 files < 1MB mỗi file)
- [ ] Upload file lớn (1 file ~ 20MB)
- [ ] Upload tổng ~ 25MB
- [ ] Dashboard load nhanh với nhiều documents (50-100 files)

## 10. Browser compatibility
- [ ] Chrome
- [ ] Firefox
- [ ] Edge
- [ ] Safari (nếu có)

---

## Kết quả kiểm tra

### Ngày kiểm tra: _______________
### Người kiểm tra: _______________

| Mục | Pass | Fail | Ghi chú |
|-----|------|------|---------|
| Database setup | [ ] | [ ] | |
| Build project | [x] | [ ] | BUILD SUCCESS |
| Login | [ ] | [ ] | |
| Dashboard load | [ ] | [ ] | |
| Dashboard hiển thị files | [ ] | [ ] | |
| Upload page UI | [ ] | [ ] | |
| Chọn/xóa files | [ ] | [ ] | |
| Upload progress | [ ] | [ ] | |
| Auto-redirect | [ ] | [ ] | |
| Error handling | [ ] | [ ] | |
| Plagiarism check integration | [ ] | [ ] | |
| Security | [ ] | [ ] | |

---

## Issues phát hiện (nếu có)

1. _______________________________________________
2. _______________________________________________
3. _______________________________________________

---

## Action items

- [ ] Item 1
- [ ] Item 2
- [ ] Item 3


