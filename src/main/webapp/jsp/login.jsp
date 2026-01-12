<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <!DOCTYPE html>
        <html>             
             
        <head>
            <meta charset="UTF-8" />
            <title>Sign in — CyberEdu</title>
            <link href="https://cdn.jsdelivr.net/npm/tailwindcss@3.4.4/dist/tailwind.min.css" rel="stylesheet" />
            <link href="${pageContext.request.contextPath}/static/css/tailwind.css" rel="stylesheet" />
        </head>

        <body class="bg-gradient-to-br from-slate-900 to-indigo-900 min-h-screen text-white">
            <div class="container mx-auto p-6">
                <div class="max-w-3xl mx-auto p-8 rounded-xl shadow-lg backdrop-blur-md bg-white/5">
                    <h1 class="text-2xl font-semibold mb-4">Sign in or create an account</h1>

                    <c:if test="${not empty requestScope.error}">
                        <div class="mb-4 p-3 rounded bg-red-700/70 text-sm">${requestScope.error}</div>
                    </c:if>

                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <form method="post" action="${pageContext.request.contextPath}/auth"
                            class="p-4 rounded bg-white/5 space-y-3">
                            <h2 class="text-lg font-medium">Login</h2>
                            <input name="username" type="text" required placeholder="Username"
                                class="w-full p-3 rounded bg-white/10 border border-white/10" />
                            <input name="password" type="password" required placeholder="Password"
                                class="w-full p-3 rounded bg-white/10 border border-white/10" />
                            <input type="hidden" name="action" value="login" />
                            <button class="w-full px-4 py-2 bg-indigo-600 hover:bg-indigo-500 rounded shadow">Sign
                                in</button>
                        </form>

                        <form method="post" action="${pageContext.request.contextPath}/auth"
                            class="p-4 rounded bg-white/5 space-y-3">
                            <h2 class="text-lg font-medium">Register</h2>
                            <input name="username" type="text" required placeholder="Create username"
                                class="w-full p-3 rounded bg-white/10 border border-white/10" />
                            <input name="password" type="password" required placeholder="Create password"
                                class="w-full p-3 rounded bg-white/10 border border-white/10" />
                            <input type="hidden" name="action" value="register" />
                            <button class="w-full px-4 py-2 bg-emerald-600 hover:bg-emerald-500 rounded shadow">Create
                                account</button>
                        </form>
                    </div>

                    <div class="mt-6 text-slate-200">
                        <p>If you already have an account, sign in. Otherwise create one to proceed to the dashboard and
                            tools.</p>
                    </div>
                </div>
            </div>
        </body>

        </html>