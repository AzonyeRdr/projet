<%@ page import="java.util.List" %>
<%@ page import="com.nathafw.demo.model.Product" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
%>
<!DOCTYPE html>
<html lang="fr">
    <head>
        <meta charset="UTF-8"><title>${title}</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
        <body>
            <main class="container"><a href="${pageContext.request.contextPath}/">← Accueil</a>
                <h1>${title}</h1>
                <div class="table-wrap"><table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nom</th>
                            <th>Catégorie</th>
                            <th>Prix</th>
                            <th>État</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (Product product : products) { %>
                        <tr><td><%= product.id() %></td><td><%= product.name() %></td><td><%= product.category() %></td><td><%= String.format("%,.0f Ar", product.price()) %></td><td><%= product.available() ? "Disponible" : "Rupture" %></td></tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </main>
    </body>
</html>
