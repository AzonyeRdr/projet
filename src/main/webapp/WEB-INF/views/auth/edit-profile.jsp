<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8"/>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		<title>Modifier mon profil</title>
		<link href="${pageContext.request.contextPath}/assets/style.css" rel="stylesheet"/>
	</head>
	<body>
		<main class="container narrow">
			<a class="back" href="${pageContext.request.contextPath}/auth/profile">&larr; Retour au profil</a>
			<section class="form-card">
				<span class="badge">MON COMPTE</span>
				<h1>Modifier mes informations.</h1>
				&lt;% if (request.getAttribute("error") != null) { %&gt;
				<p class="alert" role="alert">${error}</p>
				&lt;% } %&gt;
				<form action="${pageContext.request.contextPath}/auth/profile/edit" method="post">
					<label for="prenom">Pr&eacute;nom</label>
					<input id="prenom" maxlength="50" name="prenom" required="" type="text" value="${empty prenom ? utilisateur.prenom : prenom}"/>
					<label for="nom">Nom</label>
					<input id="nom" maxlength="50" name="nom" required="" type="text" value="${empty nom ? utilisateur.nom : nom}"/>
					<button type="submit">Enregistrer les modifications</button>
				</form>
			</section>
		</main>
	</body>
</html>
