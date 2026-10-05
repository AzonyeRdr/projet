<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr"><head><meta charset="UTF-8"><title>Produit vedette</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
    <body>
        <main class="container"><a href="${pageContext.request.contextPath}/">← Accueil</a>
            <h1>Produit vedette</h1><article class="detail-card"><span class="badge">${statusLabel}</span>
            <h2>${product.name()}</h2><p>Catégorie : ${product.category()}</p><p class="price">${product.price()} Ar</p></article>
        </main>
    </body>
</html>
