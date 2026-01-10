package com.cyberedu.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cyberedu.dao.UserDao;
import com.cyberedu.util.CryptoUtil;
import com.cyberedu.util.InputSanitizer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private final UserDao userDao = new UserDao();
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        switch (action == null ? "" : action) {
            case "register": {
                String username = InputSanitizer.sanitizeForDisplay(req.getParameter("username"));
                String password = req.getParameter("password");
                try {
                    String hash = CryptoUtil.hashPassword(password);
                    boolean ok = userDao.createUser(username, hash);
                    if (ok) {
                        req.getSession().setAttribute("username", username);
                        // After successful registration, route to index.jsp as requested
                        resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
                    } else {
                        req.setAttribute("error", "Unable to create user.");
                        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Error creating user", e);
                    req.setAttribute("error", "Error creating user.");
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                } catch (Exception e) {
                    // Handle unexpected initialization/runtime errors (e.g. DbPool issue)
                    LOGGER.log(Level.SEVERE, "Unexpected error creating user", e);
                    req.setAttribute("error", "Server error while creating account. Please check server logs.");
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                }
                break;
            }
            case "login": {
                String username = InputSanitizer.sanitizeForDisplay(req.getParameter("username"));
                String password = req.getParameter("password");
                try {
                    String storedHash = userDao.findPasswordHashByUsername(username);
                    if (storedHash != null && CryptoUtil.checkPassword(password, storedHash)) {
                        HttpSession s = req.getSession();
                        s.setAttribute("username", username);
                        s.setMaxInactiveInterval(30 * 60);
                        // After successful login, route to index.jsp
                        resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
                    } else {
                        req.setAttribute("error", "Invalid credentials.");
                        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Login error", e);
                    req.setAttribute("error", "Login error.");
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Unexpected login error", e);
                    req.setAttribute("error", "Server error during login. Please check server logs.");
                    req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                }
                break;
            }
            case "logout":
                req.getSession().invalidate();
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
        }
    }
}
