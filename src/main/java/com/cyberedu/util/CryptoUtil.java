package com.cyberedu.util;

import org.mindrot.jbcrypt.BCrypt;

public class CryptoUtil {
    private static final int WORKLOAD = 12; // adjust for environment

    public static String hashPassword(String plain) {
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plain, salt);
    }

    public static boolean checkPassword(String plain, String hashed) {
        if (hashed == null || !hashed.startsWith("$2a$")) {
            return false;
        }
        return BCrypt.checkpw(plain, hashed);
    }
}
