package utils;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ENGINE ĐA LUỒNG XỬ LÍ TẤT CẢ CÁC TÌNH HUỐNG:
 *  1) Một người upload nhiều file
 *  2) Nhiều người upload cùng một file
 *  3) Nhiều người upload nhiều file cùng lúc
 *
 *  - Queue: BlockingQueue
 *  - Worker Pool: ExecutorService
 *  - File Deduplication: SHA-256 cache
 *  - Per-user task limit: limit 3 task/user
 *  - Timeout mỗi file: 120 giây
 */
public class PlagiarismMultiThreadEngine {

    // ==========================
    //  CẤU HÌNH
    // ==========================

    private static final int MAX_USER_TASKS = 3;             // mỗi user chỉ tối đa 3 file chạy song song
    private static final int THREAD_POOL_SIZE = 8;           // worker pool
    private static final int WORKER_COUNT = 4;               // số thread consumer
    private static final long PROCESS_TIMEOUT = 120;         // giây

    // ==========================
    //  BIẾN HỆ THỐNG
    // ==========================

    private final ExecutorService executor;
    private final BlockingQueue<Task> taskQueue;
    private final Map<String, String> resultCache;           // cache theo SHA-256 file
    private final Map<Integer, AtomicInteger> userActiveTasks;
    private final Thread[] workers;
    private volatile boolean running = true;

    // ==========================
    //  KHỞI TẠO ENGINE
    // ==========================

    public PlagiarismMultiThreadEngine() {
        this.executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        this.taskQueue = new LinkedBlockingQueue<>();
        this.resultCache = new ConcurrentHashMap<>();
        this.userActiveTasks = new ConcurrentHashMap<>();

        // Tạo worker
        this.workers = new Thread[WORKER_COUNT];
        for (int i = 0; i < WORKER_COUNT; i++) {
            Thread worker = new Thread(new QueueWorker());
            worker.setDaemon(true); // daemon để JVM có thể thoát khi main kết thúc (kiểm thử)
            worker.setName("PME-Worker-" + i);
            worker.start();
            this.workers[i] = worker;
        }
    }

    // ==========================
    //  TASK MODEL
    // ==========================

    public static class Task {
        public int userId;
        public File file;
        public String hash;
        public Task(int userId, File file, String hash) {
            this.userId = userId;
            this.file = file;
            this.hash = hash;
        }
    }

    // ==========================
    //  API HÀM GỬI FILE VÀO HÀNG ĐỢI
    // ==========================

    public boolean submitFile(int userId, File file) throws Exception {

        if (!running) throw new IllegalStateException("Engine is shutting down");

        // 1. Tính SHA-256 để chống xử lý trùng
        String hash = sha256Of(file);

        // 2. Nếu file đã có kết quả → trả về cache (Trường hợp số 2)
        if (resultCache.containsKey(hash)) {
            System.out.println("[CACHE HIT] Kết quả có sẵn cho file: " + file.getName());
            return false;
        }

        // 3. Check số task đang chạy của user (Trường hợp số 1)
        userActiveTasks.putIfAbsent(userId, new AtomicInteger(0));
        if (userActiveTasks.get(userId).get() >= MAX_USER_TASKS) {
            System.out.println("[LIMIT] User " + userId + " đang upload quá " + MAX_USER_TASKS + " file.");
            return false;
        }

        // 4. Đưa task vào Queue (Trường hợp 3: nhiều user nhiều file)
        taskQueue.put(new Task(userId, file, hash));
        userActiveTasks.get(userId).incrementAndGet();
        return true;
    }

    // ==========================
    //  WORKER: LẤY TASK & XỬ LÍ
    // ==========================

    private class QueueWorker implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    Task task = taskQueue.take();

                    // Gửi vào thread pool để xử lý file
                    Future<String> future = executor.submit(() -> {
                        return processFile(task.file);
                    });

                    // Timeout
                    String result = null;
                    try {
                        result = future.get(PROCESS_TIMEOUT, TimeUnit.SECONDS);
                    } catch (TimeoutException te) {
                        future.cancel(true);
                        System.out.println("❌ TIMEOUT xử lý file: " + task.file.getName());
                    }

                    if (result != null) {
                        // Lưu cache SHA-256
                        resultCache.put(task.hash, result);
                        System.out.println("✔ DONE: " + task.file.getName());
                    }

                    // giảm count userActiveTasks
                    AtomicInteger ai = userActiveTasks.get(task.userId);
                    if (ai != null) ai.decrementAndGet();

                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ==========================
    //  XỬ LÍ FILE (NƠI BẠN GẮN Winnowing + TF-IDF)
    // ==========================

    private String processFile(File file) throws Exception {
        // ⚠️ BẠN SẼ NHÉT THUẬT TOÁN Kiểm tra đạo văn VÀO ĐÂY
        // Hiện tại chỉ mô phỏng xử lý nặng
        Thread.sleep(2000);
        return "Fake result for: " + file.getName();
    }

    // ==========================
    //  SHA-256
    // ==========================

    private String sha256Of(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] data = Files.readAllBytes(file.toPath());
        byte[] hash = digest.digest(data);

        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    // ==========================
    //  SHUTDOWN
    // ==========================

    public void shutdown() {
        running = false;
        for (Thread t : workers) {
            if (t != null) t.interrupt();
        }
        executor.shutdownNow();
    }

    // ==========================
    //  TRỢ THÚC (ví dụ: truy vấn cache)
    // ==========================

    public boolean hasResultFor(File file) throws Exception {
        return resultCache.containsKey(sha256Of(file));
    }

}
