<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %> <%-- Indispensable pour <c:choose> --%>

<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="UTF-8">
        <title>Détails du produit</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    </head>
    <body>
        <main class="container">
            <a href="${pageContext.request.contextPath}/products">← Retour à la liste</a>
            <h1>Détails du produit</h1>

            <c:choose>
                <c:when test="${not empty product}">
                    <table class="product-detail">
                        <tr><th>ID</th><td>${product.id}</td></tr>
                        <tr><th>Nom</th><td>${product.name}</td></tr>
                        <tr><th>Catégorie</th><td>${product.category}</td></tr>
                        <tr><th>Prix</th><td>${product.price} Ar</td></tr>
                        <tr><th>État</th><td>${statusLabel}</td></tr>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>Produit introuvable.</p>
                </c:otherwise>
            </c:choose>
        </main>
    </body>
</html>