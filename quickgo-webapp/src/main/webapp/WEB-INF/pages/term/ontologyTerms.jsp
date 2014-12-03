<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>All GO terms in the ${ontology} ontology</title>
<%@include file="/templates/imports.jsp"%>
<style>
body {
	background: none;
}
</style>
</head>
<body>
	<h2>All GO terms in the ${ontology} ontology</h2>
	<ul>
		<c:forEach var="term" items="${terms}">
			<li><a href="<c:url value="/"/>term/${term.key}">${term.key}</a>   ${term.value}</li>
		</c:forEach>
	</ul>
</body>
</html>