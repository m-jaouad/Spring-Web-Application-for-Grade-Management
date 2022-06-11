<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ taglib prefix="f" uri="http://www.springframework.org/tags/form"%>

<!DOCTYPE html>
<html>
<head>
<title>Modules</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css"
	rel="stylesheet"
	integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6"
	crossorigin="anonymous">

<style>
h3 {
	margin-top: 20px;
	
}
a{
	text-decoration: none;
}
</style>


</head>
<body>
	<div class="container">

	<jsp:include page="./fragments/header.jsp" />






		<div>
			<h3>List of Modules</h3>
		</div>

		<div>

			<table class="table">
				<thead>
					<tr>
						<th scope="col">ID Module</th>
						<th scope="col">Titre</th>
						<th scope="col">Code</th>
						<th scope="col" class="text-center">les fichiers de Notes </th>

					</tr>
				</thead>
				<c:forEach items="${modules}" var="module">
					<tr>
						<td><c:out value="${module.idModule}" /></td>
						<td><c:out value="${module.titre}" /></td>
						<td><c:out value="${module.code}" /></td>


						<td>
						<table class="table table-info">
						<td><a href="normal/${idNiveau}/${module.idModule}"> Session Normale </a></td>
						<td><a href="ratt/${idNiveau}/${module.idModule}"> Session Rattrapage </a></td>
						</table>
						</td>

					</tr>

				</c:forEach>

			</table>
		</div>
	</div>
</body>
</html>