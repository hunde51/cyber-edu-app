<%@ page contentType="text/html;charset=UTF-8" language="java" %> <%@ taglib
prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>Phishing Check</title>
    <link
      href="https://cdn.jsdelivr.net/npm/tailwindcss@3.4.4/dist/tailwind.min.css"
      rel="stylesheet"
    />
    <link
      href="${pageContext.request.contextPath}/static/css/tailwind.css"
      rel="stylesheet"
    />
    <script
      src="${pageContext.request.contextPath}/static/js/app.js"
      defer
    ></script>
  </head>
  ...

  <body
    class="bg-gradient-to-br from-slate-900 to-indigo-900 text-white min-h-screen"
  >
    <div class="container mx-auto p-6">
      <div class="glass p-6 rounded-xl shadow-lg">
        <div class="flex items-start justify-between gap-3">
          <div>
            <h1 class="text-2xl font-semibold">Dashboard</h1>
            <p class="text-slate-200">Risk insights and scan history.</p>
          </div>
          <div class="flex items-center gap-3 text-sm">
            <a
              href="${pageContext.request.contextPath}/jsp/index.jsp"
              class="text-indigo-200 hover:text-white"
              >Back home</a
            >
            <c:if test="${not empty sessionScope.username}">
              <form
                method="post"
                action="${pageContext.request.contextPath}/auth"
              >
                <input type="hidden" name="action" value="logout" />
                <button class="px-3 py-1 bg-red-600 hover:bg-red-500 rounded">
                  Log out
                </button>
              </form>
            </c:if>
          </div>
        </div>

        <div class="mt-5">
          <c:choose>
            <c:when test="${not empty sessionScope.username}">
              <p class="text-lg">Welcome, ${sessionScope.username}.</p>

              <c:if test="${not empty requestScope.historyError}">
                <div class="mt-3 p-3 rounded bg-red-700/70 text-sm">
                  ${requestScope.historyError}
                </div>
              </c:if>

              <c:choose>
                <c:when test="${empty requestScope.history}">
                  <div class="mt-4 p-4 rounded bg-white/5 text-slate-200">
                    No scans yet. Run a URL, password, or SQL check while logged
                    in to see it here.
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="mt-4 overflow-x-auto">
                    <table class="min-w-full text-sm text-left">
                      <thead class="uppercase text-slate-300">
                        <tr>
                          <th class="py-2 pr-4">When</th>
                          <th class="py-2 pr-4">Type</th>
                          <th class="py-2 pr-4">Risk</th>
                          <th class="py-2 pr-4">Input</th>
                          <th class="py-2 pr-4">Details</th>
                        </tr>
                      </thead>
                      <tbody class="divide-y divide-white/10">
                        <c:forEach items="${requestScope.history}" var="h">
                          <tr class="align-top">
                            <td class="py-2 pr-4 text-slate-200">
                              ${h.createdAt}
                            </td>
                            <td class="py-2 pr-4">${h.type}</td>
                            <td class="py-2 pr-4">
                              <c:choose>
                                <c:when test="${h.riskLevel eq 'SAFE'}">
                                  <span
                                    class="px-2 py-1 rounded bg-green-800/70"
                                    >SAFE</span
                                  >
                                </c:when>
                                <c:when test="${h.riskLevel eq 'WARNING'}">
                                  <span
                                    class="px-2 py-1 rounded bg-yellow-800/70"
                                    >WARNING</span
                                  >
                                </c:when>
                                <c:otherwise>
                                  <span class="px-2 py-1 rounded bg-red-800/70"
                                    >${h.riskLevel}</span
                                  >
                                </c:otherwise>
                              </c:choose>
                            </td>
                            <td class="py-2 pr-4 max-w-xs text-slate-200">
                              <div class="truncate" title="${h.inputValue}">
                                ${h.inputValue}
                              </div>
                            </td>
                            <td class="py-2 pr-4 max-w-md text-slate-200">
                              <div class="truncate" title="${h.explanation}">
                                ${h.explanation}
                              </div>
                            </td>
                          </tr>
                        </c:forEach>
                      </tbody>
                    </table>
                  </div>
                </c:otherwise>
              </c:choose>
            </c:when>
            <c:otherwise>
              <div class="p-4 rounded-lg bg-white/5 text-slate-200">
                <p>Please log in to view personalized history.</p>
                <p class="mt-2">
                  Return to the home page to authenticate or register.
                </p>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </body>
</html>
