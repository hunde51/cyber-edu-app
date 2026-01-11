package com.cyberedu.util;

import java.util.regex.Pattern;

public class InputSanitizer {
    private static final Pattern SIMPLE_SAFE = Pattern.compile("[\\p{Alnum}\\-_.@:/?&=+#%]+");

    public static String sanitizeForDisplay(String input) {
        if (input == null) return "";
        return input.replaceAll("&", "&amp;")
                    .replaceAll("<", "&lt;")
                    .replaceAll(">", "&gt;");
    }

    public static boolean isSimpleSafe(String input) {
        if (input == null) return false;
        return SIMPLE_SAFE.matcher(input).matches();
    }
}
