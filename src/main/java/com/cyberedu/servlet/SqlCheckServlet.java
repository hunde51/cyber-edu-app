package com.cyberedu.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cyberedu.dao.ScanHistoryDao;
import com.cyberedu.service.SqlAnalysisService;
import com.cyberedu.util.InputSanitizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/scan/sql")
public class SqlCheckServlet extends HttpServlet {
    private final SqlAnalysisService sqlService = new SqlAnalysisService();
    private final ScanHistoryDao historyDao = new ScanHistoryDao();
    private static final Logger LOGGER = Logger.getLogger(SqlCheckServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String sqlInput = req.getParameter("sqlInput");
        String sanitized = InputSanitizer.sanitizeForDisplay(sqlInput);
        SqlAnalysisService.Result result = sqlService.analyze(sqlInput);
        req.setAttribute("result", result);
        req.setAttribute("input", sanitized);

        HttpSession s = req.getSession(false);
        String username = s != null ? (String) s.getAttribute("username") : null;
        if (username != null) {
            try {
                historyDao.insertSqlScan(username, sanitized, result.getRisk(), result.getExplanation());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to insert SQL scan", e);
            }
        }

        req.getRequestDispatcher("/jsp/sql.jsp").forward(req, resp);
    }
}
