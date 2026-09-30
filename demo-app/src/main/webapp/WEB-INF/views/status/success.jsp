<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr"><head><meta charset="UTF-8"><title>${title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
    <body>
        <main class="container">
            <span class="badge">POST OK</span><h1>${title}</h1><p class="lead">${message}</p>
            <a class="button-link" href="${pageContext.request.contextPath}/">Retour à l'accueil</a>
        </main>
    </body>
</html>
