<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
	<head>
		<meta charset="utf-8"/>
		<meta content="width=device-width, initial-scale=1" name="viewport"/>
		<title>Mon espace sant&eacute;</title>
		<link href="${pageContext.request.contextPath}/assets/style.css" rel="stylesheet"/>
	</head>
	<body>
		<header class="topbar">
			<a class="brand" href="${pageContext.request.contextPath}/">
				AM
				<span>+</span>
			</a>
			<a class="logout" href="${pageContext.request.contextPath}/auth/logout">D&eacute;connexion</a>
		</header>
		<main class="container">
			<span class="badge">ESPACE PERSONNEL</span>
			<h1>Bonjour ${utilisateur.prenom} ${utilisateur.nom}</h1>
			<p class="lead">${utilisateur.role.libelle} &middot; Identifiant ${utilisateur.id}</p>
			<c:if test="${isPatient}">
				<section class="detail-card">
					<div class="section-heading">
						<div>
							<span class="eyebrow">VOTRE DOSSIER</span>
							<h2>Informations du patient</h2>
						</div>
						<a class="button-link secondary" href="${pageContext.request.contextPath}/patient/profile/edit">Modifier le dossier</a>
					</div>
					<c:choose>
						<c:when test="${not empty patient}">
							<div class="info-grid">
								<p>
									<small>Nom</small>
									<strong>${utilisateur.nom}</strong>
								</p>
								<p>
									<small>Pr&eacute;nom</small>
									<strong>${utilisateur.prenom}</strong>
								</p>
								<p>
									<small>Identifiant vital</small>
									<strong>${patient.idVital}</strong>
								</p>
								<p>
									<small>Date de naissance</small>
									<strong>${patient.dateNaissance}</strong>
								</p>
								<p>
									<small>Lieu de naissance</small>
									<strong>${patient.lieuNaissance}</strong>
								</p>
								<p>
									<small>Genre</small>
									<strong>${patient.genre}</strong>
								</p>
								<p>
									<small>Situation matrimoniale</small>
									<strong>
										<c:choose>
											<c:when test="${patient.marie}">Mari&eacute;(e)</c:when>
											<c:otherwise>C&eacute;libataire</c:otherwise>
										</c:choose>
									</strong>
								</p>
							</div>
						</c:when>
						<c:otherwise>
							<p>Votre dossier patient n&rsquo;est pas encore renseign&eacute;.</p>
							<a class="text-link" href="${pageContext.request.contextPath}/patient/create?idUtilisateur=${utilisateur.id}">Compl&eacute;ter mon dossier &rarr;</a>
						</c:otherwise>
					</c:choose>
				</section>
				<a class="text-link" href="${pageContext.request.contextPath}/auth/profile/edit">Modifier mon nom et pr&eacute;nom &rarr;</a>
			</c:if>
			<c:if test="${isStaff}">
				<section class="detail-card">
					<span class="eyebrow">GESTION</span>
					<h2>Liste des patients</h2>
					<p>
						<a class="text-link" href="${pageContext.request.contextPath}/patients">Actualiser la liste &rarr;</a>
					</p>
					<form class="patient-search" action="${pageContext.request.contextPath}/patients" method="get">
						<div class="search-field">
							<label for="searchNom">Nom ou pr&eacute;nom</label>
							<input id="searchNom" name="nom" type="search" value="${searchNom}"
								placeholder="Rechercher un patient"/>
						</div>
						<div class="search-field">
							<label for="searchIdVital">Identifiant vital</label>
							<input id="searchIdVital" name="idVital" type="search" value="${searchIdVital}"
								placeholder="Ex. 908de70b-..."/>
						</div>
						<button type="submit">Rechercher</button>
						<a class="button-link secondary" href="${pageContext.request.contextPath}/patients">R&eacute;initialiser</a>
					</form>
					<c:choose>
						<c:when test="${not empty patients}">
							<div class="table-wrap">
								<table>
									<thead>
										<tr>
											<th>Nom</th>
											<th>Pr&eacute;nom</th>
											<th>Identifiant utilisateur</th>
											<th>Identifiant vital</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach items="${patients}" var="p">
											<tr>
												<td>
													<a class="text-link" href="${pageContext.request.contextPath}/patient/detail?idUtilisateur=${p.idUtilisateur}">
														${p.nom}
													</a>
												</td>
												<td>
													<a class="text-link" href="${pageContext.request.contextPath}/patient/detail?idUtilisateur=${p.idUtilisateur}">
														${p.prenom}
													</a>
												</td>
												<td>${p.idUtilisateur}</td>
												<td>
													<c:choose>
														<c:when test="${empty p.idVital}">Dossier &agrave; compl&eacute;ter</c:when>
														<c:otherwise>${p.idVital}</c:otherwise>
													</c:choose>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</div>
						</c:when>
						<c:otherwise>
							<p>Aucun patient enregistr&eacute; pour le moment.</p>
						</c:otherwise>
					</c:choose>
				</section>
			</c:if>
		</main>
	</body>
</html>
