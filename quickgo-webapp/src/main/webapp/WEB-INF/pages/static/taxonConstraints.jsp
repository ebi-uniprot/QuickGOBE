<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true" %>
<!doctype html>
<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js ie6 oldie" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js ie7 oldie" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js ie8 oldie" lang="en"> <![endif]-->
<!-- Consider adding an manifest.appcache: h5bp.com/d/Offline -->
<!--[if gt IE 8]><!-->
<html xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
<!--<![endif]-->
<head>
<title>Taxon Constraints Rules &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%= request.getContextPath() %>/js/annotations.js"></script>
</head>
<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>
		
		<div class="sidebar grid_2">
			<h2 class="page-title"></h2>
		</div>
		<div class="mainContent grid_8">
			<p></p>
		</div>
		<h2>Taxon Constraint Rules</h2>
		<div style="padding:50px">
			<table id="taxConstraintsTable" class="two-colours" style="font-size:12px">
					<tr>
						<th>Taxon Rule</th>
						<th>Ancestor GO ID</th>
						<th>Ancestor GO Term Name</th>
						<th>Relationship</th>
						<th>Taxon ID</th>
						<th>Taxon</th>
						<th>Reference(s)</th>						
					<tr>
				<c:forEach var="taxonConstraint" items="${taxonConstraints}">
					<tr>
						<td>${taxonConstraint.ruleId}</td>
						<td><a href="<c:url value="/"/>term/${taxonConstraint.goId}">${taxonConstraint.goId}</a></td>
						<td>${taxonConstraint.name}</td>
						<td>${taxonConstraint.relationship}</td>						
						<td>${taxonConstraint.taxId}</td>
						<td>${taxonConstraint.taxonName}</td>
						<td>${taxonConstraint.sourcesString}</td>
					</tr>
				</c:forEach>
			</table>	
			
		</div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>
</body>
</html>