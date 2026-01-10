package com.cyberedu.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cyberedu.dao.ScanHistoryDao;
import com.cyberedu.dao.ScanHistoryDao.ScanRecord;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final ScanHistoryDao historyDao = new ScanHistoryDao();
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String username = session != null ? (String) session.getAttribute("username") : null;

        if (username != null) {
            try {
                List<ScanRecord> history = historyDao.findRecentByUser(username, 15);
                req.setAttribute("history", history);
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Unable to load history for user " + username, e);
                req.setAttribute("historyError", "Could not load scan history right now.");
            }
        }

        req.getRequestDispatcher("/jsp/dashboard.jsp").forward(req, resp);
    }
}
