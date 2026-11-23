<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8"/>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		<title>Connexion</title>
		<link href="${pageContext.request.contextPath}/assets/style.css" rel="stylesheet"/>
	</head>
	<body>
		<main class="container narrow">
			<a class="back" href="${pageContext.request.contextPath}/">&larr; Accueil</a>
			<section class="form-card">
				<span class="badge">ESPACE SANT&Eacute;</span>
				<h1>Bon retour.</h1>
				<p class="lead">Connectez-vous avec votre identifiant.</p>
				<c:if test="${not empty error}">
					<p class="alert" role="alert">${error}</p>
				</c:if>
				<c:if test="${not empty message}">
					<p class="success">${message}</p>
				</c:if>
				<form action="${pageContext.request.contextPath}/auth/login" method="post">
					<label for="id">Identifiant</label>
					<input id="id" min="1" name="id" required="" type="number" value="${id}"/>
					<button type="submit">Se connecter</button>
				</form>
				<p class="form-foot">
					Pas encore de compte ?
					<a href="${pageContext.request.contextPath}/auth/register">Cr&eacute;er un compte</a>
				</p>
			</section>
		</main>
	</body>
</html>
