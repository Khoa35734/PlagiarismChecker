# Cập nhật hoàn tất

Các thay đổi sau đã được thực hiện theo yêu cầu của bạn:

1.  **Trang chủ cho client:** Client truy cập vào trang web sẽ được chuyển hướng đến trang tải lên tài liệu (`/jsp/upload.jsp`).

2.  **Trang đăng nhập cho admin:** Một trang đăng nhập riêng cho admin đã được tạo tại `/jsp/adminLogin.jsp`.
    *   **Tên đăng nhập:** `admin`
    *   **Mật khẩu:** `password`

3.  **Tải lên tài liệu cho admin:** Sau khi đăng nhập, admin sẽ được chuyển đến trang tải lên tài liệu (`/jsp/adminUpload.jsp`) để cập nhật tài liệu vào cơ sở dữ liệu.

4.  **Thư mục lưu trữ tài liệu:** Một thư mục chuyên dụng `WebContent/documents` đã được tạo để lưu trữ các tài liệu được tải lên.

5.  **Cập nhật cơ sở dữ liệu:** Tệp `database/schema.sql` đã được cập nhật để bao gồm một bảng `Documents` mới. Vui lòng chạy lại tập lệnh này để cập nhật lược đồ cơ sở dữ liệu của bạn.

Các tệp sau đã được tạo hoặc sửa đổi:

*   `e:\PBL4\PlagiarismChecker\WebContent\documents\.placeholder` (thư mục được tạo)
*   `e:\PBL4\PlagiarismChecker\WebContent\WEB-INF\web.xml` (đã sửa đổi)
*   `e:\PBL4\PlagiarismChecker\WebContent\jsp\adminLogin.jsp` (mới)
*   `e:\PBL4\PlagiarismChecker\WebContent\jsp\adminUpload.jsp` (mới)
*   `e:\PBL4\PlagiarismChecker\src\controller\AdminLoginServlet.java` (mới)
*   `e:\PBL4\PlagiarismChecker\src\controller\AdminUploadServlet.java` (mới)
*   `e:\PBL4\PlagiarismChecker\database\schema.sql` (đã sửa đổi)