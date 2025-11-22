package utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class TextSimilarity {
    private TextSimilarity() {
    }

    public static double jaccardSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return 0.0d;
        }
        Set<String> words1 = tokenize(text1);
        Set<String> words2 = tokenize(text2);
        if (words1.isEmpty() && words2.isEmpty()) {
            return 1.0d;
        }

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        return union.isEmpty() ? 0.0d : (double) intersection.size() / (double) union.size();
    }

    private static Set<String> tokenize(String text) {
        Set<String> tokens = new HashSet<>();
        for (String token : text.toLowerCase(Locale.ENGLISH).split("\\W+")) {
            if (!token.isBlank()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     * Find matched segments between two texts and return as JSON array
     */
    public static String findMatchedSegments(String text1, String text2, int sourceId) {
        return findMatchedSegmentsWithSource(text1, text2, "Submission " + sourceId);
    }

    /**
     * Find matched segments between two texts with custom source name and return as JSON array
     */
    public static String findMatchedSegmentsWithSource(String text1, String text2, String sourceName) {
        if (text1 == null || text2 == null) {
            return "[]";
        }

        // Split into sentences
        String[] sentences1 = text1.split("[.!?]+");
        String[] sentences2 = text2.split("[.!?]+");

        StringBuilder segments = new StringBuilder("[");
        boolean hasMatch = false;

        for (String sent1 : sentences1) {
            sent1 = sent1.trim();
            if (sent1.length() < 20) continue; // Skip short sentences

            for (String sent2 : sentences2) {
                sent2 = sent2.trim();
                if (sent2.length() < 20) continue;

                double similarity = jaccardSimilarity(sent1, sent2);
                if (similarity > 0.6) { // High similarity threshold
                    if (hasMatch) {
                        segments.append(",");
                    }
                    segments.append("{")
                            .append("\"text\":\"").append(escapeJson(sent1.substring(0, Math.min(100, sent1.length())))).append("...\",")
                            .append("\"source\":\"").append(escapeJson(sourceName)).append("\",")
                            .append("\"similarity\":").append(String.format("%.2f", similarity * 100)).append(",")
                            .append("\"hasCitation\":false")
                            .append("}");
                    hasMatch = true;
                    break; // Found a match for this sentence
                }
            }
        }

        segments.append("]");
        return segments.toString();
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
