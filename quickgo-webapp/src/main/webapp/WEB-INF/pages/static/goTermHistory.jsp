<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page session="true"%>
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
<title>GOA Term History &lt; QuickGO &lt; EMBL-EBI</title>
<!-- Imports -->
<%@include file="/templates/imports.jsp"%>
<script src="<%=request.getContextPath()%>/js/annotations.js"></script>
</head>
<body>
	<div id="content" class="container_12">
		<!--Header -->
		<%@include file="/templates/header.jsp"%>

		<div class="sidebar grid_2">
			<h2 class="page-title"></h2>
		</div>
		<div class="mainContent grid_12">
			<h3>GO Term History: Displaying ${fn:length(allChanges)} audit records for all terms for the period since ${from}</h3>
		</div>
		<div class="mainContent grid_12">

			<div id="goTermsHistoryTabs">

				<c:set var="termchangesDisplay" value="display:none" />
				<c:set var="definitionchangesDisplay" value="display:none" />
				<c:set var="relationshipschangesDisplay" value="display:none" />
				<c:set var="otherchangesDisplay" value="display:none" />
				<c:set var="xrefschangesDisplay" value="display:none" />
				<c:set var="obsoletionschangesDisplay" value="display:none" />

				<ul>
					<li><a href="#allchanges">All changes</a></li>

					<c:if test="${fn:length(termsRecords) > 0}">
						<c:set var="termchangesDisplay" value="display:block" />
						<li><a href="#termchanges">Term</a></li>
					</c:if>
					<c:if test="${fn:length(definitionsRecords) > 0}">
						<c:set var="definitionchangesDisplay" value="display:block" />
						<li><a href="#definitionchanges">Definition/Synonyms</a></li>
					</c:if>
					<c:if test="${fn:length(relationsRecords) > 0}">
						<c:set var="relationshipschangesDisplay" value="display:block" />
						<li><a href="#relationshipschanges">Relationships</a></li>
					</c:if>
					<c:if test="${fn:length(xrefRecords) > 0}">
						<c:set var="xrefschangesDisplay" value="display:block" />
						<li><a href="#xrefschanges">Cross-references</a></li>
					</c:if>
				</ul>

				<div id="allchanges">
					<table id="allChangesTable" class="two-colours">
						<tr>
							<th>Timestamp</th>
							<th>GO ID</th>
							<th>GO Term Name</th>
							<th>Action</th>
							<th>Category</th>
							<th>Detail</th>
						</tr>
						<c:forEach var="history" items="${allChanges}">
							<tr>
								<td>${history.timestamp}</td>
								<td><a target="_blank" href="<c:url value="/"/>term/${history.termID}">${history.termID}</a></td>
								<td>${history.termName}</td>
								<td>${history.actionString}</td>
								<td>${history.category}</td>
								<td>${history.text}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				<div id="termchanges" style="${termchangesDisplay}">
					<table id="termchangesTable" class="two-colours">
						<tr>
							<th>Timestamp</th>
							<th>GO ID</th>
							<th>GO Term Name</th>
							<th>Action</th>
							<th>Category</th>
							<th>Detail</th>
						</tr>
						<c:forEach var="history" items="${termsRecords}">
							<tr>
								<td>${history.timestamp}</td>
								<td><a target="_blank" href="<c:url value="/"/>term/${history.termID}">${history.termID}</a></td>
								<td>${history.termName}</td>
								<td>${history.actionString}</td>
								<td>${history.category}</td>
								<td>${history.text}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				<div id="definitionchanges" style="${definitionchangesDisplay}">
					<table id="definitionchangesTable" class="two-colours">
						<tr>
							<th>Timestamp</th>
							<th>GO ID</th>
							<th>GO Term Name</th>
							<th>Action</th>
							<th>Category</th>
							<th>Detail</th>
						</tr>
						<c:forEach var="history" items="${definitionsRecords}">
							<tr>
								<td>${history.timestamp}</td>
								<td><a target="_blank" href="<c:url value="/"/>term/${history.termID}">${history.termID}</a></td>
								<td>${history.termName}</td>
								<td>${history.actionString}</td>
								<td>${history.category}</td>
								<td>${history.text}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				<div id="relationshipschanges"
					style="${relationshipschangesDisplay}">
					<table id="relationshipschangesTable" class="two-colours">
						<tr>
							<th>Timestamp</th>
							<th>GO ID</th>
							<th>GO Term Name</th>
							<th>Action</th>
							<th>Category</th>
							<th>Detail</th>
						</tr>
						<c:forEach var="history" items="${relationsRecords}">
							<tr>
								<td>${history.timestamp}</td>
								<td><a target="_blank" href="<c:url value="/"/>term/${history.termID}">${history.termID}</a></td>
								<td>${history.termName}</td>
								<td>${history.actionString}</td>
								<td>${history.category}</td>
								<td>${history.text}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
				<div id="xrefschanges" style="${xrefschangesDisplay}">
					<table id="xrefschangesTable" class="two-colours">
						<tr>
							<th>Timestamp</th>
							<th>GO ID</th>
							<th>GO Term Name</th>
							<th>Action</th>
							<th>Category</th>
							<th>Detail</th>
						</tr>
						<c:forEach var="history" items="${xrefRecords}">
							<tr>
								<td>${history.timestamp}</td>
								<td><a target="_blank" href="<c:url value="/"/>term/${history.termID}">${history.termID}</a></td>
								<td>${history.termName}</td>
								<td>${history.actionString}</td>
								<td>${history.category}</td>
								<td>${history.text}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
			<script>
				// Tabs
				$("#goTermsHistoryTabs").tabs();
			</script>
		</div>
	</div>
	<!-- Footer -->
	<%@include file="/templates/footer.jsp"%>
</body>
</html>