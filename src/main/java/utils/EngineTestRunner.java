package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EngineTestRunner {
    public static void main(String[] args) throws Exception {
        PlagiarismMultiThreadEngine engine = new PlagiarismMultiThreadEngine();

        // Tạo thư mục tmp/test-files
        File tmp = new File("tmp/test-files");
        tmp.mkdirs();

        // Tạo một số file giả để test
        File f1 = writeFile(tmp, "fileA.txt", "This is a test content A " + new Random().nextInt());
        File f2 = writeFile(tmp, "fileB.txt", "This is a test content B " + new Random().nextInt());
        File f3 = writeFile(tmp, "fileC.txt", "This is a test content A " + new Random().nextInt()); // similar to A

        System.out.println("Submitting multiple files from user 1 (multi-file)");
        engine.submitFile(1, f1);
        engine.submitFile(1, f2);
        engine.submitFile(1, f3);

        System.out.println("Submitting duplicate file from user 2 (should hit cache or dedup)");
        engine.submitFile(2, f1); // duplicate of f1

        System.out.println("Submitting many files concurrently from multiple users");
        for (int u = 3; u <= 6; u++) {
            final int userId = u;
            new Thread(() -> {
                try {
                    for (int i = 0; i < 2; i++) {
                        File f = writeFile(tmp, "user" + userId + "-file" + i + ".txt", "User " + userId + " content " + i + " " + new Random().nextInt());
                        engine.submitFile(userId, f);
                        Thread.sleep(200);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Chờ cho engine xử lý
        System.out.println("Waiting 15 seconds for processing...");
        Thread.sleep(15000);

        // Kiểm tra cache
        System.out.println("Has result for f1: " + engine.hasResultFor(f1));

        // Shutdown engine
        System.out.println("Shutting down engine...");
        engine.shutdown();

        System.out.println("Done.");
    }

    private static File writeFile(File dir, String name, String content) throws Exception {
        File f = new File(dir, name);
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
        }
        return f;
    }
}
