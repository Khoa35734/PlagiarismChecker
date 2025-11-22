package utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Cleans and normalizes multilingual text while preserving Vietnamese accents.
 */
public final class TextCleaner {
    private static final Pattern MULTI_WHITESPACE = Pattern.compile("\\s+");
    private static final Pattern SPECIAL_CHARS = Pattern.compile("[^\\p{L}0-9%\\s]");

    private TextCleaner() {
    }

    public static String clean(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);
        normalized = normalized.toLowerCase(Locale.ROOT);
        normalized = SPECIAL_CHARS.matcher(normalized).replaceAll(" ");
        normalized = MULTI_WHITESPACE.matcher(normalized).replaceAll(" ").trim();
        return normalized;
    }
}
