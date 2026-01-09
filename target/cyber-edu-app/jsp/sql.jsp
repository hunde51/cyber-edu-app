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
              <h1 class="text-2xl font-semibold">SQL Injection Analyzer</h1>
              <p class="text-slate-200">
                Detect patterns like UNION SELECT, tautologies, and comment
                markers.
              </p>
            </div>
            <a href="${pageContext.request.contextPath}/jsp/index.jsp"
              class="text-indigo-200 hover:text-white text-sm">Back
              home</a>
          </div>

          <form method="post" action="${pageContext.request.contextPath}/scan/sql" class="mt-4 space-y-3">
            <textarea name="sqlInput" rows="4"
              class="w-full p-3 rounded bg-white/10 border border-white/10 focus:outline-none focus:ring-2 focus:ring-indigo-400"
              placeholder="Enter SQL or user input to analyze" required></textarea>
            <div class="flex items-center gap-3">
              <button class="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded shadow">
                Analyze
              </button>
              <p class="text-sm text-slate-300">
                We return a risk score and remediation guidance.
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
                Input: ${requestScope.input}
              </p>
            </div>
          </c:if>

          <div class="mt-6 text-slate-200">
            <h3 class="font-medium">Educational tips</h3>
            <ul class="list-disc ml-5 space-y-1">
              <li>
                Always use parameterized queries (PreparedStatement) to avoid SQL
                injection.
              </li>
              <li>
                Use least-privileged DB accounts and avoid dynamic SQL with
                concatenation.
              </li>
              <li>Sanitize and validate user input on server side.</li>
            </ul>
          </div>
        </div>
      </div>
    </body>

    </html>