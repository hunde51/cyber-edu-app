package com.cyberedu.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level; 
import java.util.logging.Logger;

import com.cyberedu.util.InputSanitizer;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UrlScannerService {

    private final String googleApiKey;
    private final String phishtankKey;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(UrlScannerService.class.getName());

    public UrlScannerService(String googleApiKey, String phishtankKey) {
        this.googleApiKey = googleApiKey;
        this.phishtankKey = phishtankKey;
    }

    // ================= RESULT CLASS =================
    public static class Result {
        private String risk;        // SAFE, WARNING, DANGEROUS
        private int score;          // 0–100
        private String explanation;

        public String getRisk() {
            return risk;
        }

        public void setRisk(String risk) {
            this.risk = risk;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }
    // =================================================

    public Result scanUrl(String urlToCheck) {
        Result r = new Result();
        urlToCheck = urlToCheck.trim();

        // Basic validation
        if (!InputSanitizer.isSimpleSafe(urlToCheck) || urlToCheck.length() > 2000) {
            r.setRisk("DANGEROUS");
            r.setScore(90);
            r.setExplanation(
                "URL contains unusual characters or is too long; could be obfuscated. Avoid clicking unknown links."
            );
            return r;
        }

        int score = 0;
        List<String> reasons = new ArrayList<>();

        // Heuristic checks
        if (urlToCheck.contains("@") || urlToCheck.contains("%40")) {
            score += 30;
            reasons.add("Contains @ symbol which can hide the real destination.");
        }

        if (urlToCheck.matches(".*(xn--|%u|%[0-9A-Fa-f]{2}).*")) {
            score += 20;
            reasons.add("Contains encoded or punycode characters (possible homograph attack).");
        }

        if (urlToCheck.length() > 100) {
            score += 10;
            reasons.add("Very long URL which can hide malicious parameters.");
        }

        // Google Safe Browsing (optional)
        try {
            if (checkGoogleSafeBrowsing(urlToCheck)) {
                score = Math.max(score, 95);
                reasons.add("Google Safe Browsing flagged this URL as malicious.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Google Safe Browsing check failed", e);
        }

        // PhishTank (optional)
        try {
            if (checkPhishTank(urlToCheck)) {
                score = Math.max(score, 95);
                reasons.add("PhishTank community flagged this URL as phishing.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "PhishTank check failed", e);
        }

        // Risk decision
        if (score >= 90) {
            r.setRisk("DANGEROUS");
        } else if (score >= 40) {
            r.setRisk("WARNING");
        } else {
            r.setRisk("SAFE");
        }

        r.setScore(Math.min(100, score));

        if (reasons.isEmpty()) {
            r.setExplanation("No immediate red flags detected. Still verify the sender.");
        } else {
            r.setExplanation(String.join(" ", reasons));
        }

        return r;
    }

    private boolean checkGoogleSafeBrowsing(String urlToCheck) throws IOException {
        if (googleApiKey == null || googleApiKey.isBlank()) return false;

        String endpoint =
            "https://safebrowsing.googleapis.com/v4/threatMatches:find?key=" + googleApiKey;

        URL url = URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        Map<String, Object> body = Map.of(
            "client", Map.of("clientId", "cyber-edu-app", "clientVersion", "1.0"),
            "threatInfo", Map.of(
                "threatTypes", List.of("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE"),
                "platformTypes", List.of("ANY_PLATFORM"),
                "threatEntryTypes", List.of("URL"),
                "threatEntries", List.of(Map.of("url", urlToCheck))
            )
        );

        try (OutputStream os = conn.getOutputStream()) {
            os.write(mapper.writeValueAsBytes(body));
        }

        if (conn.getResponseCode() != 200) return false;

        try (InputStream is = conn.getInputStream()) {
            Map<?, ?> resp = mapper.readValue(is, Map.class);
            return resp.containsKey("matches");
        }
    }

    private boolean checkPhishTank(String urlToCheck) throws IOException {
        if (phishtankKey == null || phishtankKey.isBlank()) return false;

        String endpoint = "https://checkurl.phishtank.com/checkurl/";
        URL url = URI.create(endpoint).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setDoOutput(true);

        String payload =
            "url=" + java.net.URLEncoder.encode(urlToCheck, "UTF-8") +
            "&format=json&app_key=" + phishtankKey;

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes());
        }

        if (conn.getResponseCode() != 200) return false;

        try (InputStream is = conn.getInputStream()) {
            Map<?, ?> resp = mapper.readValue(is, Map.class);
            Object results = resp.get("results");
            return results != null && results.toString().contains("phish");
        }
    }
}
