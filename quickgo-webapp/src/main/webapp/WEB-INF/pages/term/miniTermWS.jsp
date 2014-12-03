<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${term.id}</title>
<%@include file="/templates/imports.jsp"%>
<style>
body {
	background: none;
}
</style>
</head>
<body>
	<h3>
		<strong>Definition</strong>
	</h3>
	<p>${term.definition}</p>
	<div id="child-terms">
		<h3>
			<strong>Child Terms</strong>
		</h3>
		<c:choose>
			<c:when test="${fn:length(childTermsRelations) == 0}">
			Term ${term.id} has no children
			</c:when>
			<c:otherwise>
				<p>This table lists all terms that are direct descendants (child
					terms) of ${term.id}:</p>
				<table id="childTerms">
					<tr>
						<th>Relationship to ${term.id}</th>
						<th>Child Term</th>
						<th>Child Term Name</th>
					</tr>
					<c:forEach var="childRelation" items="${childTermsRelations}">
						<tr>
							<td>${childRelation.typeof.description}</td>
							<td><a
								href="${pageContext.request.contextPath}/term/${childRelation.child.id}">${childRelation.child.id}</a></td>
							<td>${childRelation.child.name}</td>
						</tr>
					</c:forEach>
				</table>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>