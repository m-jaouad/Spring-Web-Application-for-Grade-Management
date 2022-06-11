<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6"
	crossorigin="anonymous">
<title>Application</title>
</head>
<body>


	<div class="container">

		<jsp:include page="./fragments/header.jsp" />







		<div>
			<h3>List of All Niveaux</h3>
		</div>

		<div>

			<table class="table">
				<thead>
					<tr>
						<th scope="col">Id De NIveau</th>
						<th scope="col">Alias</th>
						<th scope="col">Titre</th>

					</tr>
				</thead>
				<c:forEach items="${niveaux}" var="niveau">
					<tr>
						<td><c:out value="${niveau.idNiveau}" /></td>
						<td><c:out value="${niveau.alias}" /></td>
						<td><c:out value="${niveau.titre}" /></td>



						<td>
							<ul>
								<li><a href="modules/${niveau.idNiveau}">Afficher les
										modules</a></li>

								<li>Générer les fichiers de notes pour ce niveau:
									<ul>
										<li><a href="zip/${niveau.idNiveau}"> session normale
										</a></li>
										<li><a href="zip/ratt/${niveau.idNiveau}"> session
												rattrapage</a></li>
									</ul>
								</li>
							</ul>
						</td>

					</tr>

				</c:forEach>

			</table>
		</div>
	</div>
</body>
</html>