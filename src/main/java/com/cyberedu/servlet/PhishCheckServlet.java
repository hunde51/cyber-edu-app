package com.cyberedu.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cyberedu.dao.ScanHistoryDao;
import com.cyberedu.service.UrlScannerService;
import com.cyberedu.util.InputSanitizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/scan/url")
public class PhishCheckServlet extends HttpServlet {
    private UrlScannerService scanner;
    private final ScanHistoryDao historyDao = new ScanHistoryDao();
    private static final Logger LOGGER = Logger.getLogger(PhishCheckServlet.class.getName());

    @Override
    public void init() {
        try {
            Properties p = new Properties();
            p.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            scanner = new UrlScannerService(p.getProperty("google.safe_browsing_api_key"), p.getProperty("phishtank.api_key"));
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.WARNING, "Failed to load properties for UrlScannerService; using defaults", e);
            scanner = new UrlScannerService(null, null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getParameter("url");
        url = InputSanitizer.sanitizeForDisplay(url);
        UrlScannerService.Result result = scanner.scanUrl(url);
        req.setAttribute("result", result);
        req.setAttribute("input", url);

        // Save history if logged in
        HttpSession s = req.getSession(false);
        String username = s != null ? (String) s.getAttribute("username") : null;
        if (username != null) {
            try {
                historyDao.insertUrlScan(username, url, result.getRisk(), result.getExplanation());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to insert URL scan", e);
            }
        }

        req.getRequestDispatcher("/jsp/phish.jsp").forward(req, resp);
    }
}
