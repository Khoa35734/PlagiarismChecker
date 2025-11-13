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
}
