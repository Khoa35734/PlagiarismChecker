# Hướng Dẫn Sử Dụng Admin Dashboard - Quick Start

## 🚀 Bắt đầu nhanh

### 1. Đăng nhập Admin

**URL:** http://localhost:8080/PlagiarismChecker/login

**Thông tin đăng nhập:**
- Username: `admin`
- Password: `admin123`

Click **Login** → Tự động chuyển đến Admin Dashboard

---

## 📊 Admin Dashboard

### Màn hình chính
Sau khi đăng nhập, bạn sẽ thấy:

1. **Statistics Cards** (3 cards):
   - 📁 TOTAL DOCUMENTS: Tổng số file reference
   - 💾 TOTAL STORAGE: Tổng dung lượng (MB)
   - ✅ STATUS: Active

2. **Document Repository Table**:
   - Danh sách TẤT CẢ file đã upload bởi admin
   - Các cột: ID | Filename | Owner | Size | Upload Date | Actions

### Thao tác trên Dashboard

#### Xem file:
- Click vào tên file → Download file về máy

#### Xóa file:
- Click nút **🗑️ Delete** 
- Confirm "Are you sure?" 
- File sẽ bị xóa khỏi hệ thống

#### Upload file mới:
- Click nút **📤 Upload Document** ở góc trên bên phải
- Hoặc click **Upload First Document** nếu chưa có file nào

---

## 📤 Upload Reference Documents

### Bước 1: Chọn file

1. Click vào vùng **"Click to browse or drag files here"**
2. Chọn 1 hoặc nhiều file (PDF, DOCX, TXT)
3. File được chọn sẽ hiển thị trong danh sách

**Lưu ý:**
- ✅ Chỉ chấp nhận: `.pdf`, `.docx`, `.txt`
- ✅ Tổng dung lượng tất cả file ≤ 25 MB
- ✅ Có thể chọn nhiều file cùng lúc

### Bước 2: Quản lý danh sách file

Mỗi file trong danh sách hiển thị:
```
📄 filename.pdf (1.23 MB)  [✕]
```

**Xóa file khỏi danh sách:**
- Click nút **✕** màu đỏ bên cạnh file
- File sẽ bị loại bỏ, không được upload

**Kiểm tra tổng dung lượng:**
```
Total Size: 12.5 MB / 25 MB
```
- ✅ Màu xanh: OK, có thể upload
- ❌ Màu đỏ: Vượt quá 25MB, không thể upload

### Bước 3: Upload

1. Click nút **📤 Upload Documents**
   - Nút này chỉ enable khi:
     - ✅ Có ít nhất 1 file được chọn
     - ✅ Tổng dung lượng ≤ 25 MB

2. **Tiến trình upload:**
   ```
   Uploading file 1/3: document1.pdf - 45% (Overall: 15%)
   ```
   - Upload tuần tự từng file một
   - Progress bar hiển thị % hoàn thành
   - Không thể cancel trong lúc upload

3. **Hoàn thành:**
   ```
   ✓ All files uploaded successfully!
   ✓ Successfully uploaded 3 file(s).
   ```
   - Tự động chuyển về Dashboard sau 1.5 giây
   - Dashboard sẽ hiển thị các file vừa upload

### Xử lý lỗi

❌ **File quá lớn:**
```
⚠ Error: Total size exceeds 25 MB limit!
```
→ Xóa bớt file hoặc chọn file nhỏ hơn

❌ **Upload thất bại:**
```
⚠ Failed to upload filename.pdf: Server error
```
→ Kiểm tra file và thử lại

❌ **Mất kết nối:**
```
⚠ Network error. Please retry.
```
→ Kiểm tra internet và upload lại

---

## 🎯 Use Cases

### Case 1: Upload 1 file reference mới
```
1. Vào Dashboard
2. Click "Upload Document"
3. Chọn file "textbook.pdf"
4. Click "Upload Documents"
5. Chờ upload xong (progress bar)
6. Tự động về Dashboard
7. Thấy "textbook.pdf" trong danh sách
```

### Case 2: Upload nhiều file cùng lúc
```
1. Vào Upload page
2. Chọn 5 file PDF (tổng 20MB)
3. Xem danh sách 5 file
4. Kiểm tra: "Total Size: 20 MB / 25 MB" (màu xanh)
5. Click "Upload Documents"
6. Xem tiến trình: File 1/5 → File 2/5 → ... → File 5/5
7. Thấy thông báo success
8. Tự động về Dashboard
9. Thấy 5 file mới trong danh sách
```

### Case 3: Xóa file trước khi upload
```
1. Chọn 3 file
2. Thấy file thứ 2 không cần thiết
3. Click nút ✕ bên file thứ 2
4. File bị xóa, còn 2 file
5. Total Size được cập nhật
6. Upload 2 file còn lại
```

### Case 4: File quá lớn
```
1. Chọn file 30MB
2. Thấy thông báo đỏ: "Total size exceeds 25 MB limit!"
3. Nút "Upload Documents" bị disable
4. Click ✕ để xóa file
5. Chọn file nhỏ hơn
```

---

## 💡 Tips & Tricks

### 1. Upload hiệu quả
- ✅ Upload nhiều file nhỏ thay vì 1 file lớn
- ✅ Nén file PDF trước khi upload
- ✅ Convert DOCX sang PDF để giảm dung lượng
- ✅ Xóa file không cần thiết trước khi upload

### 2. Quản lý file
- ✅ Đặt tên file rõ ràng, dễ nhận biết
- ✅ Thường xuyên kiểm tra và xóa file cũ
- ✅ Backup file quan trọng trước khi xóa
- ✅ Sử dụng file reference chất lượng cao

### 3. Troubleshooting

**Q: Dashboard không hiển thị file vừa upload?**
- A: Refresh trang (F5) hoặc Ctrl+Shift+R

**Q: Upload bị stuck ở một file?**
- A: Chờ timeout hoặc refresh trang, thử lại

**Q: Nút Upload bị disable mặc dù đã chọn file?**
- A: Kiểm tra tổng dung lượng có vượt 25MB không

**Q: Không thể xóa file khỏi danh sách?**
- A: Refresh trang và chọn lại file

---

## 🔒 Bảo mật

- ⚠️ **KHÔNG** chia sẻ thông tin đăng nhập admin
- ⚠️ Đổi password mặc định sau lần đăng nhập đầu tiên
- ⚠️ Chỉ upload file từ nguồn tin cậy
- ⚠️ Không upload file chứa thông tin nhạy cảm

---

## 📞 Hỗ trợ

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra lại các bước trong hướng dẫn
2. Xem file TESTING_CHECKLIST.md để debug
3. Kiểm tra server logs trong Tomcat
4. Liên hệ team phát triển

---

## 📝 Changelog

### Version 1.0 (22/11/2025)
- ✅ Dashboard hiển thị tất cả file admin đã upload
- ✅ Upload tuần tự với progress bar chi tiết
- ✅ Xóa file khỏi danh sách trước khi upload
- ✅ Auto-redirect về dashboard sau upload
- ✅ Kiểm tra dung lượng real-time
- ✅ Error handling và thông báo user-friendly

---

**Chúc bạn sử dụng hiệu quả! 🎉**

