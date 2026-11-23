<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8"/>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		<title>Fiche patient</title>
		<link href="${pageContext.request.contextPath}/assets/style.css" rel="stylesheet"/>
	</head>
	<body>
		<header class="topbar">
			<a class="brand" href="${pageContext.request.contextPath}/">Ant&eacute;cedents <span>M&eacute;dicaux</span></a>
			<a class="logout" href="${pageContext.request.contextPath}/auth/logout">D&eacute;connexion</a>
		</header>
		<main class="container">
			<a class="back" href="${pageContext.request.contextPath}/patients">&larr; Liste des patients</a>
			<span class="badge">FICHE PATIENT</span>
			<c:if test="${not empty error}">
				<p class="alert" role="alert">${error}</p>
			</c:if>
			<c:if test="${not empty patientDetail}">
				<h1>${patientDetail.prenom} ${patientDetail.nom}</h1>
				<section class="detail-card">
					<span class="eyebrow">IDENTIT&Eacute;</span>
					<h2>Informations utilisateur</h2>
					<div class="info-grid">
						<p><small>Nom</small><strong>${patientDetail.nom}</strong></p>
						<p><small>Pr&eacute;nom</small><strong>${patientDetail.prenom}</strong></p>
						<p><small>Identifiant utilisateur</small><strong>${patientDetail.idUtilisateur}</strong></p>
						<p><small>R&ocirc;le</small><strong>${patientDetail.role}</strong></p>
					</div>
				</section>

				<section class="detail-card">
					<span class="eyebrow">DOSSIER M&Eacute;DICAL</span>
					<h2>Donn&eacute;es patient</h2>
					<c:choose>
						<c:when test="${not empty patientDetail.idVital}">
							<div class="info-grid">
								<p><small>Identifiant vital</small><strong>${patientDetail.idVital}</strong></p>
								<p><small>Date de naissance</small><strong>${patientDetail.dateNaissance}</strong></p>
								<p><small>Lieu de naissance</small><strong>${patientDetail.lieuNaissance}</strong></p>
								<p><small>Genre</small><strong>${patientDetail.genre}</strong></p>
								<p>
									<small>Situation matrimoniale</small>
									<strong>
										<c:choose>
											<c:when test="${patientDetail.marie}">Mari&eacute;(e)</c:when>
											<c:otherwise>C&eacute;libataire</c:otherwise>
										</c:choose>
									</strong>
								</p>
							</div>
						</c:when>
						<c:otherwise>
							<p>Ce patient n&rsquo;a pas encore compl&eacute;t&eacute; son dossier m&eacute;dical.</p>
						</c:otherwise>
					</c:choose>
				</section>
			</c:if>
		</main>
	</body>
</html>
