<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8"/>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		<title>Dossier patient</title>
		<link href="${pageContext.request.contextPath}/assets/style.css" rel="stylesheet"/>
	</head>
	<body>
		<main class="container narrow">
			<a class="back" href="${pageContext.request.contextPath}/auth/profile">&larr; Mon profil</a>
			<section class="form-card">
				<span class="badge">DOSSIER PATIENT</span>
				<h1>Mes informations.</h1>
				<p class="lead">Renseignez les informations de votre dossier médical.</p>
				<c:if test="${not empty error}">
					<p class="alert" role="alert">${error}</p>
				</c:if>
				<form action="${formAction}" method="post">
					<input type="hidden" name="idUtilisateur" value="${utilisateur.id}"/>
					<input type="hidden" name="idVital" value="${patient.idVital}"/>

					<label for="dateNaissance">Date de naissance</label>
					<input id="dateNaissance" name="dateNaissance" type="date" value="${patient.dateNaissance}" required=""/>

					<label for="lieuNaissance">Lieu de naissance</label>
					<input id="lieuNaissance" name="lieuNaissance" type="text" maxlength="100" value="${patient.lieuNaissance}" required=""/>

					<label for="genre">Genre</label>
					<select id="genre" name="genre" required="">
						<option value="">Choisissez</option>
						<option value="Homme" <c:if test="${patient.genre == 'Homme'}">selected</c:if>>Homme</option>
						<option value="Femme" <c:if test="${patient.genre == 'Femme'}">selected</c:if>>Femme</option>
					</select>

					<fieldset>
						<legend>Situation matrimoniale</legend>
						<label class="role-choice">
							<input type="radio" name="marie" value="true" <c:if test="${patient.marie}">checked</c:if>/>
							<span>Marié(e)</span>
						</label>
						<label class="role-choice">
							<input type="radio" name="marie" value="false" <c:if test="${not patient.marie}">checked</c:if>/>
							<span>Célibataire</span>
						</label>
					</fieldset>
					<button type="submit">Enregistrer le dossier</button>
				</form>
			</section>
		</main>
	</body>
</html>
