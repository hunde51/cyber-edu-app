<%@ page contentType="text/html;charset=UTF-8" language="java" %>
  <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <!DOCTYPE html>
    <html>

    <head>
      <meta charset="UTF-8" />
      <title>Phishing Check</title>
      <link href="https://cdn.jsdelivr.net/npm/tailwindcss@3.4.4/dist/tailwind.min.css" rel="stylesheet" />
      <link href="${pageContext.request.contextPath}/static/css/tailwind.css" rel="stylesheet" />
      <script src="${pageContext.request.contextPath}/static/js/app.js" defer></script>
    </head>
    ...

    <body class="bg-gradient-to-br from-slate-900 to-indigo-900 min-h-screen text-white">
      <div class="container mx-auto p-6">
        <div class="max-w-4xl mx-auto p-8 rounded-xl shadow-lg backdrop-blur-md bg-white/5">
          <h1 class="text-3xl font-semibold mb-4">
            CyberEdu — Detection & Prevention Lab
          </h1>

          <p class="mb-6 text-slate-200">
            Scan URLs, evaluate passwords, and check SQL inputs. Educational only.
          </p>

          <c:if test="${not empty requestScope.error}">
            <div class="mb-4 p-3 rounded bg-red-700/70 text-sm">
              ${requestScope.error}
            </div>
          </c:if>

          <c:choose>
            <c:when test="${not empty sessionScope.username}">
              <div class="mb-6 flex items-center justify-between p-4 rounded bg-white/5">
                <p class="text-slate-100">
                  Logged in as ${sessionScope.username}
                </p>
                <form method="post" action="${pageContext.request.contextPath}/auth">
                  <input type="hidden" name="action" value="logout" />
                  <button class="px-3 py-2 bg-red-600 hover:bg-red-500 rounded text-sm">
                    Log out
                  </button>
                </form>
              </div>
            </c:when>
            <c:otherwise>
              <c:redirect url="${pageContext.request.contextPath}/jsp/login.jsp" />
            </c:otherwise>
          </c:choose>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <a href="${pageContext.request.contextPath}/jsp/phish.jsp"
              class="block p-4 rounded-lg bg-white/5 hover:bg-white/10 transition">
              <h2 class="text-xl font-medium">Phishing URL Check</h2>
              <p class="text-slate-300">
                Check a URL for phishing indicators and get remediation tips.
              </p>
            </a>

            <a href="${pageContext.request.contextPath}/jsp/password.jsp"
              class="block p-4 rounded-lg bg-white/5 hover:bg-white/10 transition">
              <h2 class="text-xl font-medium">Password Strength</h2>
              <p class="text-slate-300">
                Evaluate password strength and learn how to improve it.
              </p>
            </a>

            <a href="${pageContext.request.contextPath}/jsp/sql.jsp"
              class="block p-4 rounded-lg bg-white/5 hover:bg-white/10 transition">
              <h2 class="text-xl font-medium">SQL Injection Check</h2>
              <p class="text-slate-300">
                Analyze SQL input for injection patterns and prevention guidance.
              </p>
            </a>

            <a href="${pageContext.request.contextPath}/dashboard"
              class="block p-4 rounded-lg bg-white/5 hover:bg-white/10 transition">
              <h2 class="text-xl font-medium">Dashboard</h2>
              <p class="text-slate-300">
                View risk scores and scan history (requires login).
              </p>
            </a>
          </div>
        </div>
      </div>
    </body>

    </html>