package com.cyberedu.service;

import java.util.*;
import java.util.regex.Pattern;

public class SqlAnalysisService {
    private static final Pattern SUSPICIOUS_PATTERN = Pattern.compile(
            "(?i)(\\bUNION\\b|\\bSELECT\\b.*\\bFROM\\b.*\\bWHERE\\b|--|;|/\\*|\\*/|\\bOR\\b\\s+1=1|\\bDROP\\b|\\bINSERT\\b|\\bUPDATE\\b|\\bDELETE\\b)"
    );

    public static class Result {
        private String risk;       // SAFE, WARNING, DANGEROUS
        private int score;         // 0-100
        private String explanation;

        public String getRisk() { return risk; }
        public void setRisk(String risk) { this.risk = risk; }

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public Result analyze(String input) {
        Result r = new Result();
        if (input == null || input.isBlank()) {
            r.setRisk("SAFE");
            r.setScore(0);
            r.setExplanation("Empty input.");
            return r;
        }
        int score = 0;
        List<String> reasons = new ArrayList<>();

        if (SUSPICIOUS_PATTERN.matcher(input).find()) {
            score += 70;
            reasons.add("Input contains SQL keywords or comment markers often used in injection attempts.");
        }

        // Detect tautologies
        if (input.matches("(?i).*\\bOR\\b\\s+\\d+=\\d+.*")) {
            score += 20;
            reasons.add("Contains tautology (e.g., OR 1=1) which is a common SQL injection pattern.");
        }

        // Detect UNION SELECT
        if (input.toLowerCase().contains("union select")) {
            score += 30;
            reasons.add("Contains UNION SELECT which can be used to exfiltrate data.");
        }

        if (score >= 80) r.setRisk("DANGEROUS");
        else if (score >= 40) r.setRisk("WARNING");
        else r.setRisk("SAFE");

        r.setScore(Math.min(100, score));
        if (reasons.isEmpty()) r.setExplanation("No obvious SQL injection patterns detected. Still use parameterized queries and least privilege.");
        else r.setExplanation(String.join(" ", reasons) + " Remediation: use PreparedStatements, input validation, and least-privileged DB accounts.");
        return r;
    }
}
