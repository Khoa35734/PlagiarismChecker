package utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Unified parser that extracts plain text from PDF, DOCX, or TXT streams.
 */
public final class FileParser {

    private FileParser() {
    }

    public static String extractText(InputStream inputStream, String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".pdf")) {
            return parsePdf(inputStream);
        }
        if (lower.endsWith(".docx")) {
            return parseDocx(inputStream);
        }
        if (lower.endsWith(".txt")) {
            return parseTxt(inputStream);
        }
        throw new IOException("Unsupported file type: " + fileName);
    }

    private static String parsePdf(InputStream inputStream) throws IOException {
        try (RandomAccessRead rar = new RandomAccessReadBuffer(inputStream);
             PDDocument document = Loader.loadPDF(rar)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String parseDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private static String parseTxt(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }
    }
}

