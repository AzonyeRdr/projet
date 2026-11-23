<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8"/>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		<title>Cr&eacute;er un compte</title>
		<link href="${pageContext.request.contextPath}/assets/style.css" rel="stylesheet"/>
	</head>
	<body>
		<main class="container narrow">
			<a class="back" href="${pageContext.request.contextPath}/">&larr; Accueil</a>
			<section class="form-card">
				<span class="badge">NOUVEAU COMPTE</span>
				<h1>Cr&eacute;er un compte.</h1>
				<p class="lead">Quelques informations pour commencer.</p>
				<c:if test="${not empty error}">
					<p class="alert" role="alert">${error}</p>
				</c:if>
				<form action="${pageContext.request.contextPath}/auth/register" method="post">
					<label for="prenom">Pr&eacute;nom</label>
					<input id="prenom" maxlength="50" name="utilisateur.prenom" required="" type="text" value="${prenom}"/>
					<label for="nom">Nom</label>
					<input id="nom" maxlength="50" name="utilisateur.nom" required="" type="text" value="${nom}"/>
					<fieldset>
						<legend>Choisissez votre r&ocirc;le</legend>
						<c:forEach items="${roles}" var="role">
							<label class="role-choice">
								<input name="idRole" required="" type="radio" value="${role.id}"/>
								<span>${role.libelle}</span>
							</label>
						</c:forEach>
					</fieldset>
					<button type="submit">Cr&eacute;er mon compte</button>
				</form>
				<p class="form-foot">
					D&eacute;j&agrave; inscrit ?
					<a href="${pageContext.request.contextPath}/auth/login">Se connecter</a>
				</p>
			</section>
		</main>
	</body>
</html>
