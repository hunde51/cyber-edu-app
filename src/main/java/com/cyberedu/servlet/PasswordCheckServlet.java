package com.cyberedu.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cyberedu.dao.ScanHistoryDao;
import com.cyberedu.service.PasswordService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/scan/password")
public class PasswordCheckServlet extends HttpServlet {
    private final PasswordService passwordService = new PasswordService();
    private final ScanHistoryDao historyDao = new ScanHistoryDao();
    private static final Logger LOGGER = Logger.getLogger(PasswordCheckServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String password = req.getParameter("password");
        // Do not store raw password anywhere; only evaluate and hash for history
        PasswordService.Result result = passwordService.evaluatePassword(password);
        req.setAttribute("result", result);
        req.setAttribute("masked", mask(password));

        HttpSession s = req.getSession(false);
        String username = s != null ? (String) s.getAttribute("username") : null;
        if (username != null) {
            try {
                historyDao.insertPasswordScan(username, result.getHashed(), result.getRisk(), result.getExplanation());
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to insert password scan", e);
            }
        }

        req.getRequestDispatcher("/jsp/password.jsp").forward(req, resp);
    }

    private String mask(String password) {
        if (password == null) return "";
        return "*".repeat(Math.min(8, password.length()));
    }
}
