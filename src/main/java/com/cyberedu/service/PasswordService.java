package com.cyberedu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.cyberedu.util.CryptoUtil;

public class PasswordService {
    private static final Pattern UPPER = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWER = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SYMBOL = Pattern.compile(".*[^A-Za-z0-9].*");

    public static class Result {
        private String risk; // SAFE, WARNING, DANGEROUS
        private int score; // 0-100
        private String explanation;
        private String hashed; // hashed password (for storing scan history only)

        public String getRisk() { return risk; }
        public void setRisk(String risk) { this.risk = risk; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }

        public String getHashed() { return hashed; }
        public void setHashed(String hashed) { this.hashed = hashed; }
    }

    public Result evaluatePassword(String password) {
        Result r = new Result();
        if (password == null || password.isBlank()) {
            r.setRisk("DANGEROUS");
            r.setScore(0);
            r.setExplanation("Empty password is not allowed.");
            return r;
        }

        int score = 0;
        List<String> reasons = new ArrayList<>();

        if (password.length() >= 12) score += 40; else if (password.length() >= 8) score += 20; else score += 0;
        if (UPPER.matcher(password).matches()) score += 15; else reasons.add("No uppercase letters.");
        if (LOWER.matcher(password).matches()) score += 15; else reasons.add("No lowercase letters.");
        if (DIGIT.matcher(password).matches()) score += 15; else reasons.add("No digits.");
        if (SYMBOL.matcher(password).matches()) score += 15; else reasons.add("No symbols.");

        // Common patterns check (very simple)
        String lower = password.toLowerCase();
        if (lower.contains("password") || lower.contains("1234") || lower.contains("qwerty")) {
            score = Math.min(score, 20);
            reasons.add("Contains common patterns or sequences.");
        }

        // Brute-force estimate (very rough)
        int entropy = estimateEntropy(password);
        if (entropy < 40) {
            reasons.add("Estimated entropy is low; password may be brute-forced quickly.");
        }

        if (score >= 70 && entropy >= 50) r.setRisk("SAFE");
        else if (score >= 40 || entropy >= 40) r.setRisk("WARNING");
        else r.setRisk("DANGEROUS");

        r.setScore(Math.min(100, score));
        if (reasons.isEmpty()) r.setExplanation("Password looks strong. Use a password manager and enable MFA.");
        else r.setExplanation(String.join(" ", reasons) + " Recommended: increase length, add unique characters, avoid common words.");

        // Hash for storage in history (never store raw)
        r.setHashed(CryptoUtil.hashPassword(password));
        return r;
    }

    private int estimateEntropy(String password) {
        int pool = 0;
        if (password.matches(".*[a-z].*")) pool += 26;
        if (password.matches(".*[A-Z].*")) pool += 26;
        if (password.matches(".*\\d.*")) pool += 10;
        if (password.matches(".*[^A-Za-z0-9].*")) pool += 32;
        double entropy = password.length() * (Math.log(pool) / Math.log(2));
        return (int) entropy;
    }
}
