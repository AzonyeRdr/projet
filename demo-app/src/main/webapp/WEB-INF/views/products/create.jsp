<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Créer un produit</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css">
    <style>
        .form-container {
            max-width: 500px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        .form-container h1 {
            text-align: center;
            color: #333;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #555;
        }
        .form-group input {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
            font-size: 14px;
        }
        .form-group input:focus {
            outline: none;
            border-color: #4CAF50;
            box-shadow: 0 0 5px rgba(76, 175, 80, 0.3);
        }
        .form-group input[type="submit"] {
            background-color: #4CAF50;
            color: white;
            cursor: pointer;
            font-weight: bold;
            transition: background-color 0.3s;
        }
        .form-group input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h1>Créer un nouveau produit</h1>
        <form action="${pageContext.request.contextPath}/products/create" method="POST">
            <div class="form-group">
                <label for="id">ID:</label>
                <input type="number" id="id" name="id" placeholder="Entrez l'ID" required>
            </div>
            
            <div class="form-group">
                <label for="name">Nom:</label>
                <input type="text" id="name" name="name" placeholder="Entrez le nom du produit" required>
            </div>
            
            <div class="form-group">
                <label for="category">Catégorie:</label>
                <input type="text" id="category" name="category" placeholder="Entrez la catégorie" required>
            </div>
            
            <div class="form-group">
                <label for="prix">Prix:</label>
                <input type="number" id="prix" name="prix" placeholder="Entrez le prix" step="0.01" required>
            </div>
            
            <div class="form-group">
                <input type="submit" value="Créer">
            </div>
        </form>
    </div>
</body>
</html>
