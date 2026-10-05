<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>${applicationName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
</head>
<body>
<main class="container">
    <span class="badge">NathaFw</span>
    <h1>${applicationName}</h1>
    <p class="lead">${message}</p>

    <section class="grid">
        <a class="card" href="${pageContext.request.contextPath}/products">
            <h2>GET + ModelView</h2><p>Catalogue avec une liste Java transmise à une JSP.</p>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/products/featured">
            <h2>Objet dans le modèle</h2><p>Affichage d'un produit complet avec EL.</p>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/about">
            <h2>Vue HTML</h2><p>Validation de l'extension HTML autorisée.</p>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/debug">
            <h2>Retour non ModelView</h2><p>Affiche la page interne des contrôleurs et routes.</p>
        </a>
        <a class="card" href="${pageContext.request.contextPath}/route-inconnue">
            <h2>Erreur 404</h2><p>Teste une URL qui n'existe pas.</p>
        </a>
    </section>

    <form method="post" action="${pageContext.request.contextPath}/products/create" class="post-test">
        <h2>Test de la route POST</h2>
        <p>Le framework actuel ne lie pas encore les champs du formulaire, mais il distingue GET et POST.</p>
        <button type="submit">Envoyer POST /products/create</button>
    </form>
</main>
</body>
</html>
