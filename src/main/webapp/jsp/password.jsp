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


    <body class="bg-gradient-to-br from-slate-900 to-indigo-900 text-white min-h-screen">
      <div class="container mx-auto p-6">
        <div class="glass p-6 rounded-xl shadow-lg">
          <div class="flex items-start justify-between gap-3">
            <div>
              <h1 class="text-2xl font-semibold">Password Strength Checker</h1>
              <p class="text-slate-200">
                Evaluate complexity, entropy, and get actionable tips.
              </p>
            </div>
            <a href="${pageContext.request.contextPath}/jsp/index.jsp"
              class="text-indigo-200 hover:text-white text-sm">Back
              home</a>
          </div>

          <form method="post" action="${pageContext.request.contextPath}/scan/password" class="mt-4 space-y-3">
            <input name="password" type="password" autocomplete="off" placeholder="Enter password to evaluate"
              class="w-full p-3 rounded bg-white/10 border border-white/10 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              required />
            <div class="flex items-center gap-3">
              <button class="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded shadow">
                Evaluate
              </button>
              <p class="text-sm text-slate-300">
                We never store raw passwords; only hashed values for history.
              </p>
            </div>
          </form>

          <c:if test="${not empty requestScope.result}">
            <c:choose>
              <c:when test="${requestScope.result.risk eq 'SAFE'}">
                <c:set var="riskClass" value="bg-green-800/80" />
              </c:when>
              <c:when test="${requestScope.result.risk eq 'WARNING'}">
                <c:set var="riskClass" value="bg-yellow-800/80" />
              </c:when>
              <c:otherwise>
                <c:set var="riskClass" value="bg-red-800/80" />
              </c:otherwise>
            </c:choose>
            <div class="mt-5 p-4 rounded-lg ${riskClass}">
              <h2 class="text-xl font-semibold">
                Result: ${requestScope.result.risk} — Score
                ${requestScope.result.score}
              </h2>
              <p class="mt-2">${requestScope.result.explanation}</p>
              <p class="mt-2 text-sm text-slate-200">
                Masked input: ${requestScope.masked}
              </p>
            </div>
          </c:if>

          <div class="mt-6 text-slate-200">
            <h3 class="font-medium">Educational tips</h3>
            <ul class="list-disc ml-5 space-y-1">
              <li>
                Use a password manager to generate and store unique passwords.
              </li>
              <li>
                Prefer passphrases of 12+ characters with mixed character classes.
              </li>
              <li>Enable multi-factor authentication wherever possible.</li>
            </ul>
          </div>
        </div>
      </div>
    </body>

    </html>